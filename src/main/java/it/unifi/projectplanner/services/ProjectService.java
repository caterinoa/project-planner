package it.unifi.projectplanner.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unifi.projectplanner.exceptions.ConflictingProjectNameException;
import it.unifi.projectplanner.exceptions.NonExistingProjectException;
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

	@Transactional(readOnly = true)
	public List<Project> getAllProjects() {
		return this.projectRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Project getProjectById(Long id) {
		return this.projectRepository.findById(id).orElse(null);
	}

	@Transactional(readOnly = true)
	public Project getProjectByName(String name) {
		return this.projectRepository.findByName(name).orElse(null);
	}

	@Transactional
	public Project insertNewProject(Project project) throws ConflictingProjectNameException {
		Optional<Project> foundByName = this.projectRepository.findByName(project.getName());
		if (foundByName.isPresent()) {
			throw new ConflictingProjectNameException(project.getName());
		}
		return this.projectRepository.save(project);
	}

	@Transactional
	public void deleteProjectById(Long id) throws NonExistingProjectException {
		Optional<Project> retrievedProject = this.projectRepository.findById(id);
		if (retrievedProject.isPresent()) {
			this.projectRepository.deleteById(id);
		} else {
			throw new NonExistingProjectException(id);
		}
	}

	@Transactional
	public void deleteAllProjects() {
		this.projectRepository.deleteAll();
	}

}
