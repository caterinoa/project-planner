package it.unifi.projectplanner.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import it.unifi.projectplanner.model.Project;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class ProjectRepositoryTest {

	@Autowired
	private ProjectRepository projectRepository;

	private static final String SAVED_PROJECT_NAME_1 = "SAVED_1";
	private static final String SAVED_PROJECT_NAME_2 = "SAVED_2";
	private static final Project SAVED_PROJECT_1 = new Project(SAVED_PROJECT_NAME_1, Collections.emptyList());
	private static final Project SAVED_PROJECT_2 = new Project(SAVED_PROJECT_NAME_2, Collections.emptyList());

	@Test
	void testJpaMapping() {
		Project saved = projectRepository.save(SAVED_PROJECT_1);
		Project saved2 = projectRepository.save(SAVED_PROJECT_2);
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getId()).isPositive();
		assertThat(saved.getId()).isLessThan(saved2.getId());
		assertThat(saved.getName()).isEqualTo(SAVED_PROJECT_NAME_1);
		assertThat(saved.getTasks()).isEmpty();
	}

	@Test
	void test_FindAll() {
		Project saved = projectRepository.save(SAVED_PROJECT_1);
		Collection<Project> projects = projectRepository.findAll();
		assertThat(projects).containsExactly(saved);
	}

	@Test
	void test_FindByProjectName() {
		Project saved = projectRepository.save(SAVED_PROJECT_1);
		Optional<Project> found = projectRepository.findByName(SAVED_PROJECT_NAME_1);
		assertThat(found).isEqualTo(Optional.of(saved));
	}

	@Test
	void test_FindById() {
		Project saved = projectRepository.save(SAVED_PROJECT_1);
		Optional<Project> found = projectRepository.findById(saved.getId());
		assertThat(found).isEqualTo(Optional.of(saved));
	}

}
