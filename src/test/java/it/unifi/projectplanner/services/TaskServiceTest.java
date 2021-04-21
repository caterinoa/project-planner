package it.unifi.projectplanner.services;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.unifi.projectplanner.exceptions.NonExistingTaskException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.repositories.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

	@Mock
	private TaskRepository taskRepository;

	@InjectMocks
	private TaskService taskService;

	private static final Long TASK_ID = 1L;
	private static final String SAVED_TASK_DESCRIPTION = "saved task";
	private static final Task SAVED_TASK = new Task(TASK_ID, SAVED_TASK_DESCRIPTION);
	private static final String SECOND_TASK_DESCRIPTION = "second task";


	@Test
	void test_GetTaskById_Found() throws NonExistingTaskException {
		when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(SAVED_TASK));
		assertThat(taskService.getTaskById(TASK_ID)).isSameAs(SAVED_TASK);
	}

	@Test
	void test_GetTaskById_NotFound() {
		when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThrows(NonExistingTaskException.class, () -> {
			taskService.getTaskById(TASK_ID);
		});
	}
	
	@Test
	void test_getAllProjectTasks() {
		Task secondTask = new Task(SECOND_TASK_DESCRIPTION);
		Project project = new Project("project", asList(SAVED_TASK, secondTask));
		when(taskRepository.findByProject(project)).thenReturn(asList(SAVED_TASK, secondTask));
		assertThat(taskService.getAllProjectTasks(project)).containsExactly(SAVED_TASK, secondTask);
	}	
}
