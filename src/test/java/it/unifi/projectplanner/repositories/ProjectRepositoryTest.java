package it.unifi.projectplanner.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import it.unifi.projectplanner.model.Project;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class ProjectRepositoryTest {

	@Autowired
	private ProjectRepository projectRepository;

	private static final String PROJECT_NAME = "name";

	@Autowired
	private TestEntityManager entityManager;

	@Test
	void testJpaMapping() {
		Project saved = entityManager.persistFlushFind(new Project(PROJECT_NAME, Collections.emptyList()));
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getId()).isPositive();
		assertThat(saved.getName()).isEqualTo(PROJECT_NAME);
		assertThat(saved.getTasks()).isEmpty();
	}

	@Test
	void testFindAll() {
		Project project = new Project(PROJECT_NAME, Collections.emptyList());
		Project saved = projectRepository.save(project);
		Collection<Project> projects = projectRepository.findAll();
		assertThat(projects).containsExactly(saved);
	}

	@Test
	void testFindByProjectName() {
		Project saved = entityManager.persistFlushFind(new Project(PROJECT_NAME, Collections.emptyList()));
		Project found = projectRepository.findByName(PROJECT_NAME);
		assertThat(found).isEqualTo(saved);
	}

	@Test
	void testFindById() {
		Project saved = entityManager.persistFlushFind(new Project(PROJECT_NAME, Collections.emptyList()));
		Optional<Project> found = projectRepository.findById(saved.getId());
		assertThat(found).isEqualTo(Optional.of(saved));
	}

}
