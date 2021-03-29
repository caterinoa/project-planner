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
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.repositories.ProjectRepository;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

	@Mock
	private ProjectRepository projectRepository;

	@InjectMocks
	private ProjectService projectService;

	private static final String SAVED_PROJECT_NAME = "saved project";
	private static final Project SAVED_PROJECT = new Project(SAVED_PROJECT_NAME, Collections.emptyList());
	private static final String PROJECT_FIXTURE_2_NAME = "second project";

	@Test
	void test_getAllProjects() {
		Project secondProject = new Project(PROJECT_FIXTURE_2_NAME, Collections.emptyList());
		when(projectRepository.findAll()).thenReturn(asList(SAVED_PROJECT, secondProject));
		assertThat(projectService.getAllProjects()).containsExactly(SAVED_PROJECT, secondProject);
	}

	@Test
	void test_getProjectById_found() {
		Long id = SAVED_PROJECT.getId();
		when(projectRepository.findById(id)).thenReturn(Optional.of(SAVED_PROJECT));
		assertThat(projectService.getProjectById(id)).isSameAs(SAVED_PROJECT);
	}

	@Test
	void test_getProjectById_notFound() {
		when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThat(projectService.getProjectById(1L)).isNull();
	}

	@Test
	void test_getProjectByName_found() {
		when(projectRepository.findByName(anyString())).thenReturn(Optional.of(SAVED_PROJECT));
		assertThat(projectService.getProjectByName(SAVED_PROJECT_NAME)).isSameAs(SAVED_PROJECT);
	}

	@Test
	void test_getProjectByName_notFound() {
		when(projectRepository.findByName(anyString())).thenReturn(Optional.empty());
		assertThat(projectService.getProjectByName(SAVED_PROJECT_NAME)).isNull();
	}

	@Test
	void test_insertNewProject_withNonExistingName() throws ConflictingProjectNameException {
		Project toSave = spy(new Project(PROJECT_FIXTURE_2_NAME, Collections.emptyList()));
		when(projectRepository.save(any(Project.class))).thenReturn(SAVED_PROJECT);

		Project result = projectService.insertNewProject(toSave);

		assertThat(result).isSameAs(SAVED_PROJECT);
		InOrder inOrder = inOrder(toSave, projectRepository);
		inOrder.verify(projectRepository).findByName(PROJECT_FIXTURE_2_NAME);
		inOrder.verify(projectRepository).save(toSave);
	}
	
	@Test
	void test_insertNewProject_withExistingName() {
		Project toSave = spy(new Project(SAVED_PROJECT_NAME, Collections.emptyList()));
		when(projectRepository.findByName(anyString())).thenReturn(Optional.of(SAVED_PROJECT));

		assertThrows(ConflictingProjectNameException.class, () -> projectService.insertNewProject(toSave));
		
		InOrder inOrder = inOrder(toSave, projectRepository);
		inOrder.verify(projectRepository).findByName(SAVED_PROJECT_NAME);
		inOrder.verify(projectRepository, times(0)).save(toSave);
	}

}
