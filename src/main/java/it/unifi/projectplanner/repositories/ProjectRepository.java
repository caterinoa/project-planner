package it.unifi.projectplanner.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import it.unifi.projectplanner.model.Project;

@Repository
public class ProjectRepository {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";
	
	public List<Project> findAll() {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Optional<Project> findById(Long id) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Optional<Project> findByName(String projectFixture1Name) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public Project save(Project project) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}
	
	public void deleteById(Long id) {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

	public void deleteAll() {
		 throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
