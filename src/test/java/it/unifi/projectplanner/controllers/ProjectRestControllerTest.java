package it.unifi.projectplanner.controllers;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.empty;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.services.ProjectService;
import net.minidev.json.JSONObject;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ProjectRestController.class)
class ProjectRestControllerTest {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectService projectService;

	@Test
	void test_allProjects_empty() throws Exception {
		this.mvc.perform(get("/api/projects").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(content().json("[]"));
	}

	@Test
	void test_allProjects_notEmpty() throws Exception {
		when(projectService.getAllProjects()).thenReturn(
				asList(new Project("first", Collections.emptyList()), new Project("second", Collections.emptyList())));
		this.mvc.perform(get("/api/projects").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name", is("first"))).andExpect(jsonPath("$[0].tasks", is(empty())))
				.andExpect(jsonPath("$[1].name", is("second"))).andExpect(jsonPath("$[1].tasks", is(empty())));
	}

	@Test
	void test_newProject_() throws Exception {
		Project requestBodyProject = new Project("new", Collections.emptyList());
		when(projectService.insertNewProject(requestBodyProject))
				.thenReturn(new Project("new", new ArrayList<>()));
	    
        JSONObject body = new JSONObject();
        body.put("name", "new");
		this.mvc.perform(post("/api/projects/new").content(body.toJSONString()).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(jsonPath("name", is("new")));
	}
}
