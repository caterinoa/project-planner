package it.unifi.projectplanner.controllers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.exceptions.NonExistingTaskException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.services.ProjectService;
import it.unifi.projectplanner.services.TaskService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = TaskWebController.class)
class TaskWebControllerTest {

	private static final String INDEX = "index";
	private static final String PROJECT_TASKS = "projectTasks";
	private static final String REDIRECT_PROJECT_TASKS = "redirect:/projectTasks";
	private static final String PROJECT_ID_ATTRIBUTE = "project_id";
	private static final String ERROR_ATTRIBUTE = "error";
	private static final String MESSAGE_ATTRIBUTE = "message";
	private static final String PROJECT_TASKS_ATTRIBUTE = "tasks";
	private static final String PROJECTS_ATTRIBUTE = "projects";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectService projectService;
	@MockBean
	private TaskService taskService;

	@Test
	void test_ViewProjectTasks_ShowTasks() throws Exception {
		Project project = new Project(1L, "project", emptyList());
		List<Task> tasks = asList(new Task(1L, "first", project), new Task(2L, "second", project));

		when(taskService.getAllProjectTasks(1L)).thenReturn(tasks);

		this.mvc.perform(get("/projectTasks/1"))
				.andExpect(view().name(PROJECT_TASKS))
				.andExpect(model().attribute("tasks", tasks))
				.andExpect(model().attribute(MESSAGE_ATTRIBUTE, ""));
	}
	
	@Test
	void test_ViewProjectTasks_ShowMessageWhenThereAreNoTasks() throws Exception {
		when(taskService.getAllProjectTasks(1L)).thenReturn(emptyList());

		this.mvc.perform(get("/projectTasks/1"))
				.andExpect(view().name(PROJECT_TASKS))
				.andExpect(model().attribute("tasks", emptyList()))
				.andExpect(model().attribute(MESSAGE_ATTRIBUTE, "No tasks"));
	}

	@Test
	void test_ViewProjectTasks_OfNotExistingProject() throws Exception {
		when(taskService.getAllProjectTasks(1L)).thenThrow(new NonExistingProjectException(1L));

		this.mvc.perform(get("/projectTasks/1"))
				.andExpect(view().name(INDEX))
				.andExpect(model().attribute(ERROR_ATTRIBUTE, "The project with id=1 does not exist"))
				.andExpect(model().attribute("projects", emptyList()));
	}
	

	@Test
    void test_NewTaskIntoProject_WithDescriptionShouldInsert() throws Exception {
    	Project project = new Project(1L, "project", emptyList());
    	when(projectService.getProjectById(1L)).thenReturn(project);
    	
    	mvc.perform(post("/projectTasks/1/savetask")
    			.param("projectId", "1")
    			.param("description", "new task"))
    			.andExpect(view().name(REDIRECT_PROJECT_TASKS + "/1"));
    			
		verify(projectService, times(1)).insertNewTaskIntoProject(new Task("new task", project), project);
    }
	
	@Test
	void test_NewTaskIntoProject_OfNotExistingProjectShouldNotInsert() throws Exception {
		Project project = new Project(1L, "project", emptyList());
		when(projectService.getProjectById(1L)).thenThrow(new NonExistingProjectException(1L));

		mvc.perform(post("/projectTasks/1/savetask")
				.param("projectId", "1")
				.param("description", ""))
				.andExpect(view().name(INDEX))
				.andExpect(model().attribute(ERROR_ATTRIBUTE, "The project with id=1 does not exist"));
				
		verify(projectService, times(0)).insertNewTaskIntoProject(new Task("new task", project), project);
	}

	@Test
	void test_NewTaskIntoProject_WithNoDescriptionShouldNotInsert() throws Exception {
		Project project = new Project(1L, "project", emptyList());
		when(projectService.getProjectById(1L)).thenReturn(project);

		mvc.perform(post("/projectTasks/1/savetask")
				.param("projectId", "1")
				.param("description", ""))
				.andExpect(view().name(PROJECT_TASKS))
				.andExpect(model().attribute(PROJECT_ID_ATTRIBUTE, 1L))
				.andExpect(model().attribute(ERROR_ATTRIBUTE, "The task description should not be empty"));
				
		verify(projectService, times(0)).insertNewTaskIntoProject(new Task("new task", project), project);
	}
	
	@Test
	void test_DeleteTask_ByExistingTaskIdShouldDelete() throws Exception {
		mvc.perform(get("/projectTasks/1/deletetask/1"))
				.andExpect(view().name(REDIRECT_PROJECT_TASKS + "/1"));
		
		verify(taskService, times(1)).deleteTaskById(1L);
	}
	
	@Test
	void test_DeleteTask_ByNonExistingTaskIdShouldNotDelete() throws Exception {
		Long taskId = 1L;
		when(projectService.getProjectById(1L)).thenReturn(new Project(1L, "project", emptyList()));
		doThrow(new NonExistingTaskException(taskId)).when(taskService).deleteTaskById(taskId);
		
		mvc.perform(get("/projectTasks/1/deletetask/1"))
				.andExpect(view().name(PROJECT_TASKS))
				.andExpect(model().attribute(ERROR_ATTRIBUTE, "The task with id=" + taskId + " does not exist"))
				.andExpect(model().attribute(PROJECT_TASKS_ATTRIBUTE, emptyList()));
		
		verify(taskService, times(1)).deleteTaskById(1L);
	}

	@Test
	void test_DeleteTask_OfNonExistingProjectShouldNotDelete() throws Exception {
		Long projectId = 1L;
		doThrow(new NonExistingProjectException(projectId)).when(projectService).getProjectById(projectId);

		mvc.perform(get("/projectTasks/1/deletetask/1"))
				.andExpect(view().name(INDEX))
				.andExpect(model().attribute(ERROR_ATTRIBUTE, "The project with id=" + projectId + " does not exist"))
				.andExpect(model().attribute(PROJECTS_ATTRIBUTE, emptyList()));

		verifyNoInteractions(taskService);
	}
	
}
