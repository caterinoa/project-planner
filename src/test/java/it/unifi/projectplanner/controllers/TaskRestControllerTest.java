package it.unifi.projectplanner.controllers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = TaskRestController.class)
class TaskRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectService projectService;
	@MockBean
	private TaskService taskService;

	@Test
	void test_AllProjectTasks_Empty() throws Exception {
		this.mvc.perform(get("/api/projects/1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json("[]"));
	}
	
	@Test
	void test_AllProjectTasks_NotEmpty() throws Exception {
		Project project = new Project(1L, "project", emptyList());
		when(taskService.getAllProjectTasks(1L)).thenReturn(
				asList(new Task(1L, "first", project), new Task(2L, "second", project)));
		
		this.mvc.perform(get("/api/projects/1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].description", is("first")))
				.andExpect(jsonPath("$[0].completed", is(false)))
				.andExpect(jsonPath("$[0].project.id", is(1)))
				.andExpect(jsonPath("$[0].project.name", is("project")))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].description", is("second")))
				.andExpect(jsonPath("$[1].completed", is(false)))
				.andExpect(jsonPath("$[0].project.id", is(1)))
				.andExpect(jsonPath("$[0].project.name", is("project")));				
	}

}
