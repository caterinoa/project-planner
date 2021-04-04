package it.unifi.projectplanner;

import static java.util.Collections.emptyList;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import it.unifi.projectplanner.exceptions.ConflictingProjectNameException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.repositories.ProjectRepository;
import it.unifi.projectplanner.services.ProjectService;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(ProjectService.class)
class ProjectServiceRepositoryIT {

	@Autowired
	private ProjectService projectService;
	@Autowired
	private ProjectRepository projectRepository;

	private static final String SAVED = "saved";

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
	void test_ServiceCanGetAllProjectsFromRepository() {

		Project first = projectRepository.save(new Project("first", emptyList()));
		Project second = projectRepository.save(new Project("second", emptyList()));
		List<Project> savedProjects = asList(first,second);
		List<Project> retrievedProjects = projectService.getAllProjects();

		assertThat(retrievedProjects).containsAll(savedProjects);

	}
}
