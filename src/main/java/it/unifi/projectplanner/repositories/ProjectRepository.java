package it.unifi.projectplanner.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.unifi.projectplanner.model.Project;

public interface ProjectRepository extends JpaRepository<Project, Long>{
	
	public List<Project> findAll();
	public Optional<Project> findByName(String name);
	public Optional<Project> findById(Long id);

}