package it.unifi.projectplanner.services;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.exceptions.NonExistingTaskException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.repositories.ProjectRepository;
import it.unifi.projectplanner.repositories.TaskRepository;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

	@Mock
	private TaskRepository taskRepository;
	@Mock
	private ProjectRepository projectRepository;
	
	@InjectMocks
	private TaskService taskService;

	private static final Long TASK_ID = 1L;
	private static final Task SAVED_TASK = new Task(TASK_ID, "saved task");
	private static final Task SAVED_TASK_2 = new Task(TASK_ID + 1, "second task");

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
	void test_GetAllProjectTasks_OfExistingProject() throws NonExistingProjectException {
		Project project = new Project(1L, "project", asList(SAVED_TASK, SAVED_TASK_2));
		when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
		when(taskRepository.findByProject(any(Project.class))).thenReturn(asList(SAVED_TASK, SAVED_TASK_2));
		
		assertThat(taskService.getAllProjectTasks(1L)).isEqualTo(asList(SAVED_TASK, SAVED_TASK_2));
	}
	
	@Test
	void test_GetAllProjectTasks_OfNotExistingProject() throws NonExistingProjectException {
		Long id = 1L;
		when(projectRepository.findById(id)).thenReturn(Optional.empty());
		assertThrows(NonExistingProjectException.class, () -> {
			taskService.getAllProjectTasks(id);
		});
	}

	@Test
	void test_DeletePTaskById_ExistingTask() throws NonExistingTaskException {
		Project savedProject = spy(new Project(1L, "project", new ArrayList<>()));
		Task taskToDelete = new Task(TASK_ID, "task", savedProject);
		savedProject.addTask(taskToDelete);
		when(taskRepository.findById(anyLong())).thenReturn(Optional.of(taskToDelete));
		when(projectRepository.findById(anyLong())).thenReturn(Optional.of(savedProject));
		
		taskService.deleteProjectTaskById(TASK_ID);
		
		InOrder inOrder = inOrder(savedProject, projectRepository, taskRepository);
		inOrder.verify(taskRepository, times(1)).findById(TASK_ID);
		inOrder.verify(savedProject, times(1)).removeTask(taskToDelete);
		inOrder.verify(projectRepository, times(1)).save(savedProject);
		verify(taskRepository, times(1)).deleteById(TASK_ID);
	}

	@Test
	void test_DeleteProjectById_NotExistingProject() throws NonExistingTaskException {
		Project savedProject = new Project(1L, "project", new ArrayList<>());
		Task taskToDelete = new Task(TASK_ID, "task", savedProject);
		savedProject.addTask(taskToDelete);
		when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
		
		assertThrows(NonExistingTaskException.class, () -> taskService.deleteProjectTaskById(TASK_ID));
		verify(taskRepository, times(0)).deleteById(TASK_ID);
	}

	@Test
	void test_UpdateTask() {
		Project savedProject = new Project(1L, "project", emptyList());
		Task savedTask = spy(new Task(TASK_ID, "task", savedProject));
		
		String updatedDescription = "updated task";
		Task updatedTask = new Task(TASK_ID, updatedDescription, savedProject);
		updatedTask.setCompleted(true);
		
		when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
		
		Task result = taskService.updateTask(savedTask, updatedDescription, true);
		
		assertThat(result).isSameAs(updatedTask);
		
		InOrder inOrder = inOrder(savedTask, taskRepository);
		inOrder.verify(savedTask, times(1)).setDescription(updatedDescription);
		inOrder.verify(savedTask, times(1)).setCompleted(true);
		inOrder.verify(taskRepository, times(1)).save(savedTask);
	}
}
