package it.unifi.projectplanner.model;

import java.util.Collection;

public class Project {
	
	private Long id;
	private String name;
	private String description;
	private Collection<Task> tasks;
	private int completionPercentage;
	
	
	public Project(Long id, String name, String description, Collection<Task> tasks) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.tasks = tasks;
		this.completionPercentage = 0;
	}


	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + ", description=" + description + ", tasks=" + tasks
				+ ", completionPercentage=" + completionPercentage + "]";
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((tasks == null) ? 0 : tasks.hashCode());
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
		Project other = (Project) obj;
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
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (tasks == null) {
			if (other.tasks != null)
				return false;
		} else if (!tasks.equals(other.tasks))
			return false;
		return true;
	}
		
}
