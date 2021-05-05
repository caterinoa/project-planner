package it.unifi.projectplanner.services;

import java.util.List;

import org.springframework.stereotype.Service;

import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.exceptions.NonExistingTaskException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;

@Service
public class TaskService {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public List<Task> getAllProjectTasks(Long projectId) throws NonExistingProjectException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}
	
	public void deleteProjectTaskById(Long taskId, Project project) throws NonExistingTaskException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}
}
