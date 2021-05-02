package it.unifi.projectplanner.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;

public interface TaskRepository extends JpaRepository<Task, Long>{
	
	public Optional<Task> findById(Long id);
	public List<Task> findByProject(Project project);
	
}