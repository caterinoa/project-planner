package it.unifi.projectplanner.dto;

public class TaskDTO {

	private String description;

	public TaskDTO() {
		super();
	}

	public TaskDTO(String description) {
		super();
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}
}