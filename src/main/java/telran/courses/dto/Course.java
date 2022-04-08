package telran.courses.dto;

import java.io.Serializable;

import javax.validation.constraints.*;

public class Course implements Serializable { 
	private static final long serialVersionUID = 1L;
	private static final long MIN_HOURS = 80;
	private static final long MAX_HOURS = 500;
	private static final long MIN_COST = 5000;
	private static final long MAX_COST = 20000;
	public Integer id;
	@NotEmpty
	public String course;
	@NotEmpty
	public String lecturer;
	@Min(MIN_HOURS) @Max(MAX_HOURS)
	public int hours;
	 @Min(MIN_COST) @Max(MAX_COST)
	public int cost;
	@NotNull
	@Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}.*")
	public String openingDate;
}
