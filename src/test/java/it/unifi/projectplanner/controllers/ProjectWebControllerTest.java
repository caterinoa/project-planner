package it.unifi.projectplanner.controllers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;

import it.unifi.projectplanner.exceptions.ConflictingProjectNameException;
import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.services.ProjectService;
import it.unifi.projectplanner.services.TaskService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ProjectWebController.class)
class ProjectWebControllerTest {

	private static final String INDEX = "index";
	private static final String REDIRECT = "redirect:/";
	private static final String PROJECT_TASKS = "projectTasks";
	private static final String REDIRECT_PROJECT_TASKS = "redirect:/projectTasks";
	private static final String PROJECT_ID_ATTRIBUTE = "project_id";
	private static final String ERROR_ATTRIBUTE = "error";
	private static final String MESSAGE_ATTRIBUTE = "message";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectService projectService;
	@MockBean
	private TaskService taskService;

	@Test
	void test_Status200() throws Exception {
		this.mvc.perform(get("/")).andExpect(status().is2xxSuccessful());
	}

	@Test
	void test_ReturnHomeView() throws Exception {
		ModelAndViewAssert.assertViewName(this.mvc.perform(get("/")).andReturn().getModelAndView(), INDEX);
	}

	@Test
	void test_HomeView_ShowsProjects() throws Exception {
		List<Project> projects = asList(new Project(1L, "first", emptyList()), new Project(2L, "second", emptyList()));

		when(projectService.getAllProjects()).thenReturn(projects);

		this.mvc.perform(get("/"))
				.andExpect(view().name(INDEX))
				.andExpect(model().attribute("projects", projects))
				.andExpect(model().attribute(MESSAGE_ATTRIBUTE, ""));
	}

	@Test
	void test_HomeView_ShowsMessageWhenThereAreNoProjects() throws Exception {
		when(projectService.getAllProjects()).thenReturn(emptyList());

		this.mvc.perform(get("/"))
				.andExpect(view().name(INDEX))
				.andExpect(model().attribute("projects", emptyList()))
				.andExpect(model().attribute(MESSAGE_ATTRIBUTE, "No projects"));
	}

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
				.andExpect(model().attribute(ERROR_ATTRIBUTE, "The project with id=1 does not exist"));
//				.andExpect(model().attribute("projects", emptyList()));
	}
	
	
	@Test
	void test_NewProject_WithNameShouldInsert() throws Exception {
		Project project = new Project("new", emptyList());

		this.mvc.perform(post("/save").param("name", "new")).andExpect(view().name(REDIRECT));

		verify(projectService, times(1)).insertNewProject(project);
	}

	@Test
	void test_NewProject_WithExistingNameShouldNotInsert() throws Exception {
		String existingProjectName = "existing project";
		Project project = new Project(existingProjectName, emptyList());

		when(projectService.insertNewProject(project))
				.thenThrow(new ConflictingProjectNameException(existingProjectName));

		mvc.perform(post("/save").param("name", existingProjectName)).andExpect(view().name(INDEX)).andExpect(
				model().attribute(ERROR_ATTRIBUTE, "The name '" + existingProjectName + "' is already used for another project"));

		verify(projectService, times(1)).insertNewProject(project);
	}

	@Test
	void test_NewProject_WithNoNameShouldNotInsert() throws Exception {
		Project project = new Project("", emptyList());

		mvc.perform(post("/save").param("name", "")).andExpect(view().name(INDEX))
				.andExpect(model().attribute(ERROR_ATTRIBUTE, "The project name should not be empty"));

		verify(projectService, times(0)).insertNewProject(project);
	}

	@Test
    void test_NewTaskIntoProject_WithDescriptionShouldInsert() throws Exception {
    	Project project = new Project(1L, "project", emptyList());
    	when(projectService.getProjectById(1L)).thenReturn(project);
    	
    	mvc.perform(post("/projectTasks/1/savetask")
    			.param("projectId", "1")
    			.param("description", "new task"))
    			.andExpect(view().name(REDIRECT_PROJECT_TASKS + "/1"));
    			
		verify(projectService, times(1)).insertNewTaskIntoProject(new Task("new task", project));
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
				
		verify(projectService, times(0)).insertNewTaskIntoProject(new Task("new task", project));
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
				
		verify(projectService, times(0)).insertNewTaskIntoProject(new Task("new task", project));
	}

	@Test
	void test_DeleteProject_ByExistingIdShouldDelete() throws Exception {
		mvc.perform(get("/delete/1")).andExpect(view().name(REDIRECT));
		verify(projectService, times(1)).deleteProjectById(1L);
	}

	@Test
	void test_DeleteProject_ByNonExistingIdShouldNotDelete() throws Exception {
		Long id = 1L;
		doThrow(new NonExistingProjectException(id)).when(projectService).deleteProjectById(id);

		mvc.perform(get("/delete/1")).andExpect(view().name(INDEX))
				.andExpect(model().attribute(ERROR_ATTRIBUTE, "The project with id=" + id + " does not exist"));

		verify(projectService, times(1)).deleteProjectById(id);
	}

}
