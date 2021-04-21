package it.unifi.projectplanner.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.unifi.projectplanner.exceptions.NonExistingTaskException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.repositories.TaskRepository;

@Service
public class TaskService {

	@Autowired
	private TaskRepository taskRepository;

	@Transactional(readOnly = true)
	public Task getTaskById(Long id) throws NonExistingTaskException {
		return this.taskRepository.findById(id).orElseThrow(() -> new NonExistingTaskException(id));
	}

	@Transactional(readOnly = true)
	public Collection<Task> getAllProjectTasks(Project project) {
		return this.taskRepository.findByProject(project);
	}

	@Transactional
	public Task insertNewTask(Task task) {
		return taskRepository.save(task);
	}

}
