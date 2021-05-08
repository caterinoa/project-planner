package it.unifi.projectplanner.controllers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import it.unifi.projectplanner.exceptions.NonExistingTaskException;
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
	private TaskService taskService;
	@MockBean
	private ProjectService projectService;

	@Test
	void test_AllProjectTasks_Empty() throws Exception {
		this.mvc.perform(get("/api/tasks/project/1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().json("[]"));
	}
	
	@Test
	void test_AllProjectTasks_NotEmpty() throws Exception {
		Project project = new Project(1L, "project", emptyList());
		when(taskService.getAllProjectTasks(1L)).thenReturn(
				asList(new Task(1L, "first", project), new Task(2L, "second", project)));
		
		this.mvc.perform(get("/api/tasks/project/1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json"))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].description", is("first")))
				.andExpect(jsonPath("$[0].completed", is(false)))
				.andExpect(jsonPath("$[1].id", is(2)))
				.andExpect(jsonPath("$[1].description", is("second")))
				.andExpect(jsonPath("$[1].completed", is(false)));				
	}
	
	@Test
	void test_DeleteTaskById_WithExistingTaskIdShouldDelete() throws Exception {
		this.mvc.perform(delete("/api/tasks/1").accept(MediaType.APPLICATION_JSON))
        		.andExpect(status().isOk())
        		.andExpect(jsonPath("$").doesNotExist());
		verify(taskService, times(1)).deleteProjectTaskById(1L);
    }
	
	@Test
	void test_DeleteTaskById_WithNonExistingTaskIdShouldThrow() throws Exception {
		doThrow(new NonExistingTaskException(1L)).when(taskService).deleteProjectTaskById(1L);
		
		this.mvc.perform(delete("/api/tasks/1").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is5xxServerError())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof NonExistingTaskException))
				.andExpect(result -> assertEquals("The task with id=1 does not exist", result.getResolvedException().getMessage()));
		verify(taskService, times(1)).deleteProjectTaskById(1L);
    }

}
 
