package telran.courses.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.*;
import org.springframework.stereotype.Service;

import telran.courses.dto.Course;
import telran.courses.exceptions.ResourceNotFoundException;

import static telran.courses.api.ApiConstants.*;

@Service
public class CoursesServiceImpl implements CoursesService {

	private static final long serialVersionUID = 1L;
	private transient String fileName = "courses.data";
	static Logger LOG = LoggerFactory.getLogger(CoursesService.class);
	private Map<Integer, Course> courses = new HashMap<>();

	@Override
	public Course addCourse(Course course) {
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
		if (course == null) {
			throw new ResourceNotFoundException(String.format("course with id %d not found", id));
		}
		return course;
	}

	@Override
	public Course updateCourse(int id, Course course) {

		Course courseUpdated = courses.replace(id, course);
		if (courseUpdated == null) {
			throw new ResourceNotFoundException(String.format("course with id %d not found", id));
		}
		return courseUpdated;
	}

	@Override
	public void restore() {
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
			CoursesServiceImpl coursesService = (CoursesServiceImpl) ois.readObject();
			this.courses = coursesService.courses;
			LOG.debug("data has been restored from file {}", fileName);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e.getMessage());
		} catch (FileNotFoundException e) {
			LOG.debug("data is empty");
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	@Override
	public void save() {
		LOG.debug("save procedure");
		try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
			oos.writeObject(this);
			LOG.debug("data has been saved");
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	/* V.R.
	 *  Due to your design the method restoreInvocation is redundant.
	 *  The annotation @PostConstruct has to come before the method restore()
	 */
	@PostConstruct
	void restoreInvocation() {
		restore();
	}

	@PreDestroy
	void saveInvocation() {
		LOG.debug("Destroing..");
		/* V.R.  Generally speaking the following call
		 *  is redundant. All of procedures are called from 
		 *  the bean CoursesSavingService.
		 *  So the whole method saveInvocation is redundant.
		 *  Due to your desin it does nothing
		 */
		save();
	}

}
