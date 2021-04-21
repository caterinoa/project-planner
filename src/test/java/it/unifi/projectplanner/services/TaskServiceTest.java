package it.unifi.projectplanner.services;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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
	private static final String SAVED_TASK_DESCRIPTION_2 = "second task";
	private static final Task SAVED_TASK = new Task(TASK_ID, SAVED_TASK_DESCRIPTION, null);
	private static final Task SAVED_TASK_2 = new Task(TASK_ID + 1, SAVED_TASK_DESCRIPTION_2, null);

	@Test
	void test_GetTaskById_Found() throws NonExistingTaskException {
		when(taskRepository.findById(TASK_ID)).thenReturn(Optional.of(SAVED_TASK));
		assertThat(taskService.getTaskById(TASK_ID)).isEqualTo(SAVED_TASK);
	}

	@Test
	void test_GetTaskById_NotFound() {
		when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
		assertThrows(NonExistingTaskException.class, () -> {
			taskService.getTaskById(TASK_ID);
		});
	}

	@Test
	void test_GetAllProjectTasks() {
		when(taskRepository.findByProject(any(Project.class))).thenReturn(asList(SAVED_TASK, SAVED_TASK_2));
		
		Project project = new Project("project", asList(SAVED_TASK, SAVED_TASK_2));
		assertThat(taskService.getAllProjectTasks(project)).isEqualTo(asList(SAVED_TASK, SAVED_TASK_2));
	}
	
	@Test
	void test_InsertNewTask() {
		Task toSave = new Task("task to save", null);
		when(taskRepository.save(any(Task.class))).thenReturn(SAVED_TASK);
		
		Task result = taskService.insertNewTask(toSave);
		assertThat(result).isSameAs(SAVED_TASK);
		verify(taskRepository, times(1)).save(toSave);
	}
}
