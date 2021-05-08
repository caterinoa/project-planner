package it.unifi.projectplanner;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import it.unifi.projectplanner.exceptions.ConflictingProjectNameException;
import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.repositories.ProjectRepository;
import it.unifi.projectplanner.repositories.TaskRepository;
import it.unifi.projectplanner.services.ProjectService;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(ProjectService.class)
class ProjectServiceRepositoryIT {

	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private TaskRepository taskRepository;

	private static final String SAVED = "saved";

	@Test
	void test_ServiceCanGetProjectsByIdFromRepository() throws NonExistingProjectException {
		Project project = projectRepository.save(new Project("project", emptyList()));
		Project retrievedProject = projectService.getProjectById(project.getId());

		assertThat(retrievedProject).isEqualTo(project);
	}
	
	@Test
	void test_ServiceDoesNotGetNonExistingProjectsByIdFromRepository() throws NonExistingProjectException {
		assertThrows(NonExistingProjectException.class, () -> projectService.getProjectById(1L));
	}
	
	@Test
	void test_ServiceCanGetProjectsByNameFromRepository() throws NonExistingProjectException {
		Project project = projectRepository.save(new Project("project", emptyList()));
		Project retrievedProject = projectService.getProjectByName("project");

		assertThat(retrievedProject).isEqualTo(project);
	}
	
	@Test
	void test_ServiceCanGetAllProjectsFromRepository() {
		Project first = projectRepository.save(new Project("first", emptyList()));
		Project second = projectRepository.save(new Project("second", emptyList()));
		List<Project> savedProjects = asList(first,second);
		List<Project> retrievedProjects = projectService.getAllProjects();

		assertThat(retrievedProjects).containsAll(savedProjects);
	}
	
	@Test
	void test_ServiceCanInsertIntoRepository() throws ConflictingProjectNameException {
		Project saved = projectService.insertNewProject(new Project(SAVED, emptyList()));
		assertThat(projectRepository.findById(saved.getId())).isPresent();
	}

	@Test
	void test_ServiceDoesNotInsertProjectWithExistingNameIntoRepository() throws ConflictingProjectNameException {
		projectService.insertNewProject(new Project(SAVED, emptyList()));
		assertThrows(ConflictingProjectNameException.class,
				() -> projectService.insertNewProject(new Project(SAVED, emptyList())));
	}
	
	@Test
	void test_ServiceCanDeleteProjectFromRepository() throws NonExistingProjectException {
		Project saved = projectRepository.save(new Project(SAVED, new ArrayList<>()));
		saved = projectService.insertNewTaskIntoProject(new Task("task", saved), saved);
		Long projectId = saved.getId();
		Long taskId = saved.getTasks().iterator().next().getId();
		
		projectService.deleteProjectById(projectId);
		assertFalse(projectRepository.findById(projectId).isPresent());
		assertFalse(taskRepository.findById(taskId).isPresent());
	}
	
	@Test
	void test_ServiceDoesNotDeleteNonExistingProjectFromRepository() throws NonExistingProjectException {
		assertThrows(NonExistingProjectException.class,
				() -> projectService.deleteProjectById(1L));
	}
	
	@Test
	void test_ServiceCanInsertNewTaskIntoProjectIntoRepository() {
		Project savedProject = projectRepository.save(new Project(SAVED, new ArrayList<>()));
		Task newTask = new Task("new task", savedProject);
		Project updatedProject = projectService.insertNewTaskIntoProject(newTask, savedProject);
		
		assertTrue(updatedProject.getTasks().contains(newTask));
	}
}
