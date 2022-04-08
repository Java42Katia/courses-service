package telran.courses.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import telran.courses.dto.Course;
import telran.courses.exceptions.ResourceNotFoundException;

import static telran.courses.api.ApiConstants.*;
@Service
public class CoursesServiceImpl implements CoursesService {
@Value("${app.interval.minutes: 1}")
	int interval;

	private static final long serialVersionUID = 1L;
	private transient String fileName = "courses.data";
	private transient CoursesSavingThread savingThread;
	static Logger LOG = LoggerFactory.getLogger(CoursesService.class);		
	private Map<Integer, Course> courses = new HashMap<>();
	
	@Override
	public  Course addCourse(Course course) {
	    course.id = generateId();
	    Course res = add(course);
	   
	    return res;
	}

	private Course add(Course course) {
		courses.put(course.id, course);
		return course;
	}

	@Override
	public List<Course> getAllCourses() {
	    
	    return new ArrayList<>(courses.values());
	}

	
	private Integer generateId() {
	    ThreadLocalRandom random = ThreadLocalRandom.current();
	    int randomId;

	    do {
	        randomId = random.nextInt(MIN_ID, MAX_ID);
	    } while (exists(randomId));
	    return randomId;
	}

	private boolean exists(int id) {
		return courses.containsKey(id);
	}

	@Override
	public Course getCourse(int id) {
		Course course = courses.get(id);
		if (course == null) {
			throw new ResourceNotFoundException(String.format("course with id %d not found", id));
		}
		return course;
	}

	@Override
	public Course removeCourse(int id) {
		Course course = courses.remove(id);
		if (course == null){
			throw new ResourceNotFoundException(String.format("course with id %d not found", id));
		}
		return course;
	}

	@Override
	public Course updateCourse(int id, Course course) {
		
		Course courseUpdated = courses.replace(id, course);
		if (courseUpdated == null){
			throw new ResourceNotFoundException(String.format("course with id %d not found", id));
		}
		return courseUpdated;
	}

	@Override
	public void restore() {
		File inputFile = new File(fileName);
		if (inputFile.exists()) {
			try (ObjectInputStream input = new ObjectInputStream(new FileInputStream(inputFile))) {
				CoursesServiceImpl coursesFromFile = (CoursesServiceImpl) input.readObject();
				this.courses = coursesFromFile.courses;
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage());
			} 
		}
	}

	@Override
	public void save() {
		try(ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(fileName))) {
			output.writeObject(this);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}
	
	@PostConstruct
	void restoreInvocation() throws InterruptedException {
		LOG.debug("interval: {}", interval);
		restore();
		savingThread = new CoursesSavingThread(this);
		savingThread.start();
	}
	
	@PreDestroy
	void saveInvocation() {
		LOG.debug("Stop saving...");
		savingThread.stopSaving();
	}
	//CoursesServiceImpl
	private class CoursesSavingThread extends Thread {
		int intervalU;
		CoursesService service;
		boolean stopThread = false;
		
		public CoursesSavingThread(CoursesService service) {
			this.intervalU = interval*1000*60;
			this.service = service;
		}
		
		public void stopSaving() {
			stopThread = true;
		}
		
		@Override
		public void run() {
			while(!stopThread) {
				try {
					sleep(intervalU);
					service.save();
					LOG.debug("Saving is done");
				} catch (InterruptedException e) {
					e.printStackTrace();
					LOG.debug("sleep is interruped");
					break;
				}
			}
			LOG.debug("CoursesSavingThread is stopped");
		}
	}

}


