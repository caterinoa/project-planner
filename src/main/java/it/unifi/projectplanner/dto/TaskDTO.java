package it.unifi.projectplanner.dto;

public class TaskDTO {

	private String description;
	private String completed;

	public TaskDTO() {
		super();
	}
	
	public String getDescription() {
		return this.description;
	}
	
	public String getCompleted() {
		return this.completed;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCompleted(String completed) {
		this.completed = completed;
	}
	
}