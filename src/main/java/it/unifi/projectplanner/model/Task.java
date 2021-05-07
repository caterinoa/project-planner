package it.unifi.projectplanner.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "tasks")
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String description;
	@Column(nullable = false)
	private boolean completed;
	@ManyToOne
	private Project project;

	public Task() {
		
	}
	
	public Task(String description, Project project) {
		super();
		this.description = description;
		this.project = project;
		this.completed = false;
	}
	
	public Task(Long id, String description, Project project) {
		super();
		this.id = id;
		this.description = description;
		this.project = project;
		this.completed = false;
	}
	
	public Task(Long id, String description) {
		super();
		this.id = id;
		this.description = description;
		this.completed = false;
	}
	
	public Task(String description) {
		super();
		this.description = description;
		this.completed = false;
	}

	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public boolean isCompleted() {
		return completed;
	}
	
	public Long getProjectId() {
		return project.getId();
	}

	@Override
	public String toString() {
		return "Task [id=" + id + ", description=" + description + ", completed=" + completed + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (completed ? 1231 : 1237);
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
		if (completed != other.completed)
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
