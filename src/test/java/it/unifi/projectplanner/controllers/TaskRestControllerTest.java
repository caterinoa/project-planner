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

import it.unifi.projectplanner.exceptions.EmptyMandatoryInputException;
import it.unifi.projectplanner.exceptions.NonExistingTaskException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.services.ProjectService;
import it.unifi.projectplanner.services.TaskService;
import net.minidev.json.JSONObject;

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
	
	@Test
	void test_UpdateTask_OfNonExistingTaskShouldThrow() throws Exception {
		when(taskService.getTaskById(1L)).thenThrow(new NonExistingTaskException(1L));
		
		JSONObject body = new JSONObject();
		body.put("description", "new task");
		body.put("completed", "1");
		
		this.mvc.perform(post("/api/tasks/1").content(body.toJSONString()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is5xxServerError())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof NonExistingTaskException))
				.andExpect(result -> assertEquals("The task with id=1 does not exist", result.getResolvedException().getMessage()));
	}
	
	@Test
	void test_UpdateTask_WithEmptyDescriptionShouldThrow() throws Exception {
		JSONObject body = new JSONObject();
		body.put("description", "");
		body.put("completed", "1");
		
		this.mvc.perform(post("/api/tasks/1").content(body.toJSONString()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().is5xxServerError())
				.andExpect(result -> assertTrue(result.getResolvedException() instanceof EmptyMandatoryInputException))
				.andExpect(result -> assertEquals("Invalid input: mandatory field 'description' was empty.", result.getResolvedException().getMessage()));
	}
	
	@Test
	void test_UpdateTask_WithDescriptionShouldInsert() throws Exception {
		Project project = new Project(1L, "project", new ArrayList<>());
		Task savedTask = new Task(1L, "saved", project);
		
		when(taskService.getTaskById(1L)).thenReturn(savedTask);

		String updatedDescription = "updated";
		Task updatedTask = new Task(1L, updatedDescription, project);
		updatedTask.setCompleted(true);
		
		when(taskService.updateTask(savedTask, updatedDescription, true)).thenReturn(updatedTask);
		
		JSONObject body = new JSONObject();
		body.put("description", updatedDescription);
		body.put("completed", "1");
		
		this.mvc.perform(post("/api/tasks/1").content(body.toJSONString()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("id", is(1)))
				.andExpect(jsonPath("description", is(updatedDescription)))
				.andExpect(jsonPath("completed", is(true)));
	}
}
 
