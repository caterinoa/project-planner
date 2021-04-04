package it.unifi.projectplanner.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.unifi.projectplanner.exceptions.ConflictingProjectNameException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.repositories.ProjectRepository;

@Service
public class ProjectService {

	private ProjectRepository projectRepository;

	@Autowired
	public ProjectService(ProjectRepository projectRepository) {
		super();
		this.projectRepository = projectRepository;
	}

	public List<Project> getAllProjects() {
		return this.projectRepository.findAll();
	}

	public Project getProjectById(Long id) {
		return this.projectRepository.findById(id).orElse(null);
	}

	public Project getProjectByName(String name) {
		return this.projectRepository.findByName(name).orElse(null);
	}

	public Project insertNewProject(Project project) throws ConflictingProjectNameException {
		Optional<Project> foundByName = this.projectRepository.findByName(project.getName());
		if(foundByName.isPresent()) {
			throw new ConflictingProjectNameException();
		}
		return this.projectRepository.save(project);
	}

}
