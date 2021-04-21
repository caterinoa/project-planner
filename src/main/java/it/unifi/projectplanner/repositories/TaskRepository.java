package it.unifi.projectplanner.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;

@Repository
public class TaskRepository {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";

	public Optional<Task> findById(Long id) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public List<Task> findByProject(Project project) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Task save(Task task) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}
}
