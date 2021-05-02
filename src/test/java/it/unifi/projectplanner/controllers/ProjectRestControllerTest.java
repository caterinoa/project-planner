package it.unifi.projectplanner.controllers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.services.ProjectService;
import it.unifi.projectplanner.services.TaskService;
import net.minidev.json.JSONObject;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ProjectRestController.class)
class ProjectRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectService projectService;
	@MockBean
	private TaskService taskService;

	@Test
	void test_AllProjects_Empty() throws Exception {
		this.mvc.perform(get("/api/projects").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json("[]"));
	}

	@Test
	void test_AllProjects_NotEmpty() throws Exception {
		when(projectService.getAllProjects()).thenReturn(
				asList(new Project(1L, "first", emptyList()), new Project(2L, "second", emptyList())));
		this.mvc.perform(get("/api/projects").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("first")))
				.andExpect(jsonPath("$[0].tasks", is(empty())))
				.andExpect(jsonPath("$[0].completionPercentage", is(0)))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].name", is("second")))
				.andExpect(jsonPath("$[1].tasks", is(empty())))
				.andExpect(jsonPath("$[1].completionPercentage", is(0)));
	}

	@Test
	void test_NewProject() throws Exception {
		Project requestBodyProject = new Project("new", emptyList());
		when(projectService.insertNewProject(requestBodyProject))
				.thenReturn(new Project(1L, "new", new ArrayList<>()));
		
		JSONObject body = new JSONObject();
		body.put("name", "new");
		this.mvc.perform(post("/api/projects/new").content(body.toJSONString()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id", is(1)))
				.andExpect(jsonPath("name", is("new")))
				.andExpect(jsonPath("completionPercentage", is(0)))
				.andExpect(jsonPath("tasks", is(empty())));
	}
	
	@Test
	void test_NewProjectTask() throws Exception {
		Project savedProject = new Project(1L, "project", emptyList());
		Task requestBodyTask = new Task("new task", savedProject);
		when(projectService.getProjectById(1L)).thenReturn(savedProject);
		Project updatedProject = new Project(1L, "project", asList(new Task(1L, "new task", savedProject)));
		when(projectService.insertNewTaskIntoProject(requestBodyTask)).thenReturn(updatedProject);
		
		JSONObject body = new JSONObject();
		body.put("description", "new task");
		this.mvc.perform(post("/api/projects/1/newtask").content(body.toJSONString()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id", is(1)))
				.andExpect(jsonPath("name", is("project")))
				.andExpect(jsonPath("completionPercentage", is(0)))
				.andExpect(jsonPath("tasks[0].id", is(1)))
				.andExpect(jsonPath("tasks[0].description", is("new task")))
				.andExpect(jsonPath("tasks[0].completed", is(false)))
				.andExpect(jsonPath("tasks[0].project.id", is(1)))
				.andExpect(jsonPath("tasks[0].project.name", is("project")));
	}
	
	@Test
	void test_DeleteProjectById() throws Exception {
		this.mvc.perform(delete("/api/projects/1").accept(MediaType.APPLICATION_JSON))
        		.andExpect(status().isOk())
        		.andExpect(jsonPath("$").doesNotExist());
		verify(projectService, times(1)).deleteProjectById(1L);
    }
	
	@Test
	void test_DeleteAllProjects() throws Exception {
		this.mvc.perform(delete("/api/projects/deleteall").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").doesNotExist());
		verify(projectService, times(1)).deleteAllProjects();
	}
}
