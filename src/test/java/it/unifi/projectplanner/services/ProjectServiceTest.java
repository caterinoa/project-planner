package it.unifi.projectplanner.services;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unifi.projectplanner.exceptions.ConflictingProjectNameException;
import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.repositories.ProjectRepository;
import it.unifi.projectplanner.repositories.TaskRepository;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

	@Mock
	private ProjectRepository projectRepository;
	@Mock
	private TaskRepository taskRepository;

	@InjectMocks
	private ProjectService projectService;

	private static final Long PROJECT_ID = 1L;
	private static final String SAVED_PROJECT_NAME = "saved project";
	private static final Project SAVED_PROJECT = new Project(PROJECT_ID, SAVED_PROJECT_NAME, emptyList());

	@Test
	void test_GetAllProjects() {
		Project secondProject = new Project("second project", emptyList());
		when(projectRepository.findAll()).thenReturn(asList(SAVED_PROJECT, secondProject));
		assertThat(projectService.getAllProjects()).containsExactly(SAVED_PROJECT, secondProject);
	}

	@Test
	void test_GetProjectById_Found() throws NonExistingProjectException {
		when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(SAVED_PROJECT));
		assertThat(projectService.getProjectById(PROJECT_ID)).isSameAs(SAVED_PROJECT);
	}

	@Test
	void test_GetProjectById_NotFound() {
		when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThrows(NonExistingProjectException.class, () -> {
			projectService.getProjectById(PROJECT_ID);
		});
	}

	@Test
	void test_GetProjectByName_Found() throws NonExistingProjectException {
		when(projectRepository.findByName(anyString())).thenReturn(Optional.of(SAVED_PROJECT));
		assertThat(projectService.getProjectByName(SAVED_PROJECT_NAME)).isSameAs(SAVED_PROJECT);
	}

	@Test
	void test_GetProjectByName_NotFound() throws NonExistingProjectException {
		when(projectRepository.findByName(anyString())).thenReturn(Optional.empty());
		assertThrows(NonExistingProjectException.class, () -> {
			projectService.getProjectByName(SAVED_PROJECT_NAME);
		});
	}

	@Test
	void test_InsertNewProject_WithNonExistingName() throws ConflictingProjectNameException {
		Project toSave = spy(new Project("project to save", emptyList()));
		when(projectRepository.save(any(Project.class))).thenReturn(SAVED_PROJECT);

		Project result = projectService.insertNewProject(toSave);

		assertThat(result).isSameAs(SAVED_PROJECT);
		InOrder inOrder = inOrder(toSave, projectRepository);
		inOrder.verify(projectRepository, times(1)).findByName("project to save");
		inOrder.verify(projectRepository, times(1)).save(toSave);
	}

	@Test
	void test_InsertNewProject_WithExistingName() {
		Project toSave = spy(new Project(SAVED_PROJECT_NAME, emptyList()));
		when(projectRepository.findByName(anyString())).thenReturn(Optional.of(SAVED_PROJECT));

		assertThrows(ConflictingProjectNameException.class, () -> projectService.insertNewProject(toSave));

		InOrder inOrder = inOrder(toSave, projectRepository);
		inOrder.verify(projectRepository, times(1)).findByName(SAVED_PROJECT_NAME);
		inOrder.verify(projectRepository, times(0)).save(toSave);
	}

	@Test
	void test_InsertNewTaskIntoProject() {
		Project projectToUpdate = spy(new Project(1L, SAVED_PROJECT_NAME, new ArrayList<>()));
		Task taskToAdd = spy(new Task(1L, "to add", projectToUpdate));
		projectToUpdate.addTask(taskToAdd);
		
		when(taskRepository.save(any(Task.class))).thenReturn(taskToAdd);
		when(projectRepository.save(any(Project.class))).thenReturn(projectToUpdate);
		
		Task toAdd = new Task("to add", projectToUpdate);
		Project updated = projectService.insertNewTaskIntoProject(toAdd, projectToUpdate);
		
		assertThat(updated).isSameAs(projectToUpdate);
		InOrder inOrder = inOrder(projectToUpdate, taskToAdd, projectRepository, taskRepository);
		inOrder.verify(taskRepository, times(1)).save(toAdd);
		inOrder.verify(projectToUpdate, times(1)).addTask(taskToAdd);
		inOrder.verify(projectRepository, times(1)).save(projectToUpdate);
	}
	
	@Test
	void test_DeleteProjectById_ExistingProject() throws NonExistingProjectException {
		when(projectRepository.findById(anyLong())).thenReturn(Optional.of(SAVED_PROJECT));
		projectService.deleteProjectById(PROJECT_ID);
		verify(projectRepository, times(1)).deleteById(PROJECT_ID);
	}

	@Test
	void test_DeleteProjectById_NotExistingProject() throws NonExistingProjectException {
		when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThrows(NonExistingProjectException.class, () -> projectService.deleteProjectById(PROJECT_ID));
		verify(projectRepository, times(0)).deleteById(PROJECT_ID);
	}
	
	@Test
	void test_DeleteAllProjects() throws NonExistingProjectException {
		projectService.deleteAllProjects();
		verify(projectRepository, times(1)).deleteAll();
	}
}
