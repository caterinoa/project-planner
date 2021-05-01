package it.unifi.projectplanner.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unifi.projectplanner.exceptions.ConflictingProjectNameException;
import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.repositories.ProjectRepository;
import it.unifi.projectplanner.repositories.TaskRepository;

@Service
public class ProjectService {

	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private TaskRepository taskRepository;

	@Transactional(readOnly = true)
	public List<Project> getAllProjects() {
		return this.projectRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Project getProjectById(Long id) throws NonExistingProjectException {
		return this.projectRepository.findById(id).orElseThrow(() -> new NonExistingProjectException(id));
	}
	
	@Transactional(readOnly = true)
	public Project getProjectByName(String name) throws NonExistingProjectException {
		return this.projectRepository.findByName(name).orElseThrow(() -> new NonExistingProjectException(name));
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
	
	@Transactional
	public Project insertNewTaskIntoProject(Task task) {
		Task savedTask = taskRepository.save(task);
		Project project = savedTask.getProject();
		project.addTask(savedTask);
		return projectRepository.save(project);
	}

}
