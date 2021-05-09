package it.unifi.projectplanner.dto;

public class TaskDTO {

	private String description;
	private String completed;

	public TaskDTO() {
		super();
	}

	public TaskDTO(String description) {
		super();
		this.description = description;
		this.completed = "0";
	}
	
	public TaskDTO(String description, String completed) {
		super();
		this.description = description;
		this.completed = completed;
	}

	public String getDescription() {
		return this.description;
	}
	
	public String getCompleted() {
		return this.completed;
	}
}