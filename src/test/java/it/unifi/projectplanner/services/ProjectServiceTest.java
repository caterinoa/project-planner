package it.unifi.projectplanner.services;

import static java.util.Arrays.asList;
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

import java.util.Collections;
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
import it.unifi.projectplanner.repositories.ProjectRepository;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

	@Mock
	private ProjectRepository projectRepository;

	@InjectMocks
	private ProjectService projectService;

	private static final Long PROJECT_ID = 1L;
	private static final String SAVED_PROJECT_NAME = "saved project";
	private static final Project SAVED_PROJECT = new Project(PROJECT_ID, SAVED_PROJECT_NAME, Collections.emptyList());
	private static final String SECOND_PROJECT_NAME = "second project";

	@Test
	void test_GetAllProjects() {
		Project secondProject = new Project(SECOND_PROJECT_NAME, Collections.emptyList());
		when(projectRepository.findAll()).thenReturn(asList(SAVED_PROJECT, secondProject));
		assertThat(projectService.getAllProjects()).containsExactly(SAVED_PROJECT, secondProject);
	}

	@Test
	void test_GetProjectById_Found() {
		when(projectRepository.findById(PROJECT_ID)).thenReturn(Optional.of(SAVED_PROJECT));
		assertThat(projectService.getProjectById(PROJECT_ID)).isSameAs(SAVED_PROJECT);
	}

	@Test
	void test_GetProjectById_NotFound() {
		when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThat(projectService.getProjectById(PROJECT_ID)).isNull();
	}

	@Test
	void test_GetProjectByName_Found() {
		when(projectRepository.findByName(anyString())).thenReturn(Optional.of(SAVED_PROJECT));
		assertThat(projectService.getProjectByName(SAVED_PROJECT_NAME)).isSameAs(SAVED_PROJECT);
	}

	@Test
	void test_GetProjectByName_NotFound() {
		when(projectRepository.findByName(anyString())).thenReturn(Optional.empty());
		assertThat(projectService.getProjectByName(SAVED_PROJECT_NAME)).isNull();
	}

	@Test
	void test_InsertNewProject_WithNonExistingName() throws ConflictingProjectNameException {
		Project toSave = spy(new Project(SECOND_PROJECT_NAME, Collections.emptyList()));
		when(projectRepository.save(any(Project.class))).thenReturn(SAVED_PROJECT);

		Project result = projectService.insertNewProject(toSave);

		assertThat(result).isSameAs(SAVED_PROJECT);
		InOrder inOrder = inOrder(toSave, projectRepository);
		inOrder.verify(projectRepository, times(1)).findByName(SECOND_PROJECT_NAME);
		inOrder.verify(projectRepository, times(1)).save(toSave);
	}
	
	@Test
	void test_InsertNewProject_WithExistingName() {
		Project toSave = spy(new Project(SAVED_PROJECT_NAME, Collections.emptyList()));
		when(projectRepository.findByName(anyString())).thenReturn(Optional.of(SAVED_PROJECT));

		assertThrows(ConflictingProjectNameException.class, () -> projectService.insertNewProject(toSave));
		
		InOrder inOrder = inOrder(toSave, projectRepository);
		inOrder.verify(projectRepository, times(1)).findByName(SAVED_PROJECT_NAME);
		inOrder.verify(projectRepository, times(0)).save(toSave);
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
