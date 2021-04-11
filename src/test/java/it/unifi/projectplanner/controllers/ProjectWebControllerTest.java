package it.unifi.projectplanner.controllers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
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
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.services.ProjectService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ProjectWebController.class)
class ProjectWebControllerTest {

	private static final String INDEX = "index";
	private static final String REDIRECT = "redirect:/";
	private static final String ERROR = "error";
	private static final String MESSAGE = "message";

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectService projectService;

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
		List<Project> projects = asList(new Project(1L, "first", emptyList()),
				new Project(2L, "second", emptyList()));

		when(projectService.getAllProjects()).thenReturn(projects);

		this.mvc.perform(get("/")).andExpect(view().name(INDEX)).andExpect(model().attribute("projects", projects))
				.andExpect(model().attribute(MESSAGE, ""));
	}

	@Test
	void test_HomeView_ShowsMessageWhenThereAreNoProjects() throws Exception {
		when(projectService.getAllProjects()).thenReturn(emptyList());

		this.mvc.perform(get("/")).andExpect(view().name(INDEX))
				.andExpect(model().attribute("projects", emptyList()))
				.andExpect(model().attribute(MESSAGE, "No projects"));
	}

	@Test
	void test_NewProject_WithNameShouldSave() throws Exception {
		Project project = new Project("new", emptyList());

		this.mvc.perform(post("/save").param("name", "new")).andExpect(view().name(REDIRECT));

		verify(projectService).insertNewProject(project);
	}
	
    @Test
    void test_newProject_withExistingNameShouldNotSave() throws Exception {
        Project project = new Project("new", emptyList());

        when(projectService.insertNewProject(project)).thenThrow(new ConflictingProjectNameException());

        mvc.perform(post("/save").param("name", "new"))
                .andExpect(view().name(INDEX))
                .andExpect(model().attribute(ERROR, "The specified name is already used for another project"));

        verify(projectService).insertNewProject(project);
    }
}
