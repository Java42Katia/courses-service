package telran.courses.service;

import java.io.Serializable;
import java.util.List;

import telran.courses.dto.Course;

public interface CoursesService  extends Serializable {
	Course addCourse(Course course);
	
	List<Course> getAllCourses();

	Course getCourse(int id);

	Course removeCourse(int id);

	Course updateCourse(int id, Course course);
	void restore();
	void save();
	
}
