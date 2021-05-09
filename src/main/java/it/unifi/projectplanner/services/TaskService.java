package it.unifi.projectplanner.services;

import java.util.List;

import org.springframework.stereotype.Service;

import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.exceptions.NonExistingTaskException;
import it.unifi.projectplanner.model.Task;

@Service
public class TaskService {
	
	private static final String  TEMPORARY_IMPLEMENTATION = "Temporary implementation";
	
	public Task getTaskById(Long id) throws NonExistingTaskException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public List<Task> getAllProjectTasks(Long projectId) throws NonExistingProjectException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public void deleteProjectTaskById(Long id) throws NonExistingTaskException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION); 	
	}
	
	public Task updateTask(Task task, String description, boolean completed) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}
}
