package it.unifi.projectplanner.services;

import java.util.List;

import org.springframework.stereotype.Service;

import it.unifi.projectplanner.exceptions.ConflictingProjectNameException;
import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;

@Service
public class ProjectService {
	
	private static final String  TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public List<Project> getAllProjects() {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Project insertNewProject(Project project) throws ConflictingProjectNameException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public void deleteProjectById(Long id) throws NonExistingProjectException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public void deleteAllProjects() {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Project getProjectById(Long id) throws NonExistingProjectException {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Project insertNewTaskIntoProject(Task task) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}
	
	

}
