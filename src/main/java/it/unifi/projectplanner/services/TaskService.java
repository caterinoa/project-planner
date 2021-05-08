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
	public void deleteProjectTaskById(Long taskId) throws NonExistingTaskException {
		Optional<Task> retrievedTask = this.taskRepository.findById(taskId);
		if (retrievedTask.isPresent()) {
			Long projectId = retrievedTask.get().projectId();
			Project project = projectRepository.findById(projectId).get();
			project.removeTask(retrievedTask.get());
			this.projectRepository.save(project);
			this.taskRepository.deleteById(taskId);
		} else {
			throw new NonExistingTaskException(taskId);
		}
	}
	
	@Transactional
	public Task updateTask(Task task, String description, boolean completed) {
		task.setDescription(description);
		task.setCompleted(completed);
		return taskRepository.save(task);
	}
}
