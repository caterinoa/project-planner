package it.unifi.projectplanner.services;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.exceptions.NonExistingTaskException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.repositories.ProjectRepository;
import it.unifi.projectplanner.repositories.TaskRepository;

@Service
public class TaskService {

	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private ProjectRepository projectRepository;

	@Transactional(readOnly = true)
	public Task getTaskById(Long id) throws NonExistingTaskException {
		return this.taskRepository.findById(id).orElseThrow(() -> new NonExistingTaskException(id));
	}

	@Transactional(readOnly = true)
	public Collection<Task> getAllProjectTasks(Long projectId) throws NonExistingProjectException {
		Optional<Project> project = projectRepository.findById(projectId);
		if (!project.isPresent()) {
			throw new NonExistingProjectException(projectId);
		}
		return this.taskRepository.findByProject(project.get());
	}
	
	@Transactional
	public void deleteTaskById(Long id) throws NonExistingTaskException {
		Optional<Task> retrievedTask = this.taskRepository.findById(id);
		if (retrievedTask.isPresent()) {
			this.taskRepository.deleteById(id);
		} else {
			throw new NonExistingTaskException(id);
		}
	}
}
