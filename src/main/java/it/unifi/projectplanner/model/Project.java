package it.unifi.projectplanner.model;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "projects")
public class Project {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true)
	private String name;
	@OneToMany
	private Collection<Task> tasks;
	@Column(nullable = false)
	private int completionPercentage;

	public Project() {
		
	}
	
	public Project(String name, Collection<Task> tasks) {
		super();
		this.name = name;
		this.tasks = tasks;
		this.completionPercentage = 0;
	}
	
	public Project(Long id, String name, Collection<Task> tasks) {
		super();
		this.id = id;
		this.name = name;
		this.tasks = tasks;
		this.completionPercentage = 0;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Collection<Task> getTasks() {
		return tasks;
	}

	public int getCompletionPercentage() {
		return completionPercentage;
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", name=" + name + ", tasks=" + tasks + ", completionPercentage="
				+ completionPercentage + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + completionPercentage;
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
		if (completionPercentage != other.completionPercentage)
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
