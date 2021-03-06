package it.unifi.projectplanner.services;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.repositories.ProjectRepository;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

	@Mock
	private ProjectRepository projectRepository;

	@InjectMocks
	private ProjectService projectService;

	@Test
	void test_getAllProjects() {
		Project firstProject = new Project(1L, "first", "first description", Collections.emptyList()); 
		Project secondProject = new Project(2L, "second", "second description", Collections.emptyList());
		when(projectRepository.findAll()).
			thenReturn(asList(firstProject, secondProject));
		assertThat(projectService.getAllProjects()).containsExactly(firstProject, secondProject);
		
	}

}
