package it.unifi.projectplanner.repositories;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;

@DataJpaTest
@ExtendWith(SpringExtension.class)
class TaskRepositoryTest {

	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private ProjectRepository projectRepository;

	private static final String SAVED_TASK_DESCRIPTION_1 = "task 1";
	private static final String SAVED_TASK_DESCRIPTION_2 = "task 2";
	private static final Task SAVED_TASK_1 = new Task(SAVED_TASK_DESCRIPTION_1);
	private static final Task SAVED_TASK_2 = new Task(SAVED_TASK_DESCRIPTION_2);

	@Test
	void testJpaMapping() {
		Task saved = taskRepository.save(SAVED_TASK_1);
		Task saved2 = taskRepository.save(SAVED_TASK_2);
		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getId()).isPositive();
		assertThat(saved.getId()).isLessThan(saved2.getId());
		assertThat(saved.getDescription()).isEqualTo(SAVED_TASK_DESCRIPTION_1);
		assertThat(saved.isCompleted()).isFalse();
	}

	@Test
	void test_FindById() {
		Task saved = taskRepository.save(SAVED_TASK_1);
		Optional<Task> found = taskRepository.findById(saved.getId());
		assertThat(found).isEqualTo(Optional.of(saved));
	}

	@Test
	void test_FindByProject() {
		Project savedProject = new Project("project", new ArrayList<Task>());
		Task savedTask = taskRepository.save(new Task(SAVED_TASK_DESCRIPTION_1, savedProject));
		Task savedTask2 = taskRepository.save(new Task(SAVED_TASK_DESCRIPTION_2, savedProject));
		savedProject.addTask(savedTask);
		savedProject.addTask(savedTask2);
		savedProject = projectRepository.save(savedProject);
		assertThat(asList(savedTask, savedTask2)).isEqualTo(taskRepository.findByProject(savedProject));
	}

}
