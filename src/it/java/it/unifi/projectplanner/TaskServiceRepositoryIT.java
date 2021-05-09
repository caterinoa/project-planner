package it.unifi.projectplanner;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.exceptions.NonExistingTaskException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.repositories.ProjectRepository;
import it.unifi.projectplanner.repositories.TaskRepository;
import it.unifi.projectplanner.services.TaskService;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import(TaskService.class)
class TaskServiceRepositoryIT {
	
	@Autowired
	private TaskService taskService;
	
	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private ProjectRepository projectRepository;
	
	private static final String SAVED = "saved";
	
	@Test
	void test_ServiceCanGetTaskByIdFromRepository() throws NonExistingTaskException {
		Task task = taskRepository.save(new Task(1L, SAVED));
		Task retrievedTask = taskService.getTaskById(task.getId());
		
		assertThat(retrievedTask).isEqualTo(task);
	}
	
	@Test
	void test_ServiceDoesNotGetNonExistingTaskByIdFromRepository() throws NonExistingTaskException {
		assertThrows(NonExistingTaskException.class, () -> taskService.getTaskById(1L));
	}
	
	@Test
	void test_ServiceCanGetAllProjectTasksFromRepository() throws NonExistingProjectException {
		Project savedProject = projectRepository.save(new Project("saved project", new ArrayList<>()));
		Task firstTask = taskRepository.save(new Task("first", savedProject));
		Task secondTask = taskRepository.save(new Task("second", savedProject));
		savedProject.addTask(firstTask);
		savedProject.addTask(secondTask);
		savedProject = projectRepository.save(savedProject);
		
		List<Task> savedTasks = asList(firstTask,secondTask);
		List<Task> retrievedProjects = taskService.getAllProjectTasks(savedProject.getId());

		assertThat(retrievedProjects).containsAll(savedTasks);
	}
	
	@Test
	void test_ServiceDoesNotGetTasksOfNonExistingProjectByIdFromRepository() throws NonExistingProjectException {
		assertThrows(NonExistingProjectException.class, () -> taskService.getAllProjectTasks(1L));
	}
	
	@Test
	void test_ServiceCanDeleteTaskFromRepository() throws NonExistingTaskException, NonExistingProjectException {
		Project savedProject = projectRepository.save(new Project("saved project", new ArrayList<>()));
		Task savedTask = taskRepository.save(new Task(SAVED, savedProject));
		Long taskId = savedTask.getId();
		taskService.deleteProjectTaskById(taskId);
		
		assertFalse(taskService.getAllProjectTasks(savedProject.getId()).contains(savedTask));
		assertFalse(taskRepository.findById(taskId).isPresent());
	}
	
	@Test
	void test_ServiceDoesNotDeleteNonExistingTaskFromRepository() throws NonExistingTaskException {
		assertThrows(NonExistingTaskException.class, () -> taskService.deleteProjectTaskById(1L));
	}

	@Test
	void test_ServiceCanUpdateTaskIntoRepository() {
		Project savedProject = projectRepository.save(new Project("saved project", new ArrayList<>()));
		Task savedTask = taskRepository.save(new Task(SAVED, savedProject));
		
		Task updatedTask = taskService.updateTask(savedTask, "updated task", true);
		
		assertEquals("updated task", updatedTask.getDescription());
		assertTrue(updatedTask.isCompleted());
	}

}
