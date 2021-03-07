package it.unifi.projectplanner.repositories;

import java.util.List;

import org.springframework.stereotype.Repository;

import it.unifi.projectplanner.model.Project;

@Repository
public class ProjectRepository {

	private static final String TEMPORARY_IMPLEMENTATION = "Temporary implementation";
	
	public List<Project> findAll() {
		throw new UnsupportedOperationException(TEMPORARY_IMPLEMENTATION);
	}

}
