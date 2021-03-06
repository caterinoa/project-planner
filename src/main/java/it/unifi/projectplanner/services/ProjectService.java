package it.unifi.projectplanner.services;

import java.util.List;

import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.repositories.ProjectRepository;

public class ProjectService {

	private ProjectRepository projectRepository;
	
	public ProjectService(ProjectRepository projectRepository) {
		super();
		this.projectRepository = projectRepository;
	}

	public List<Project> getAllProjects() {
		return this.projectRepository.findAll();
	}

}
