package it.unifi.projectplanner.model;

import java.util.Date;

public class Task {
	
	private Long id;
	private String description;
	private Date deadline;
	private boolean completed;
	
	
	public Task(Long id, String description, Date deadline, boolean completed) {
		super();
		this.id = id;
		this.description = description;
		this.deadline = deadline;
		this.completed = completed;
	}


	@Override
	public String toString() {
		return "Task [id=" + id + ", description=" + description + ", deadline=" + deadline + ", completed=" + completed
				+ "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deadline == null) ? 0 : deadline.hashCode());
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Task other = (Task) obj;
		if (deadline == null) {
			if (other.deadline != null)
				return false;
		} else if (!deadline.equals(other.deadline))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
