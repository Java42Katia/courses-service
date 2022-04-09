package telran.courses.service;

import org.slf4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CoursesSavingService {
	
	
	Logger LOG = LoggerFactory.getLogger(CoursesSavingService.class);
	
	@Autowired
	CoursesService coursesService;
	
	@Scheduled(fixedRateString = "${fixedRate.in.millisec}")
	void savingTask() {
		LOG.debug("savingTask invocation");
		coursesService.save();		
	}


}
