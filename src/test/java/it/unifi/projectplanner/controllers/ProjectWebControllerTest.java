package it.unifi.projectplanner.controllers;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Collections;
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
	void testStatus200() throws Exception {
		this.mvc.perform(get("/")).andExpect(status().is2xxSuccessful());
	}

	@Test
	void testReturnHomeView() throws Exception {
		ModelAndViewAssert.assertViewName(this.mvc.perform(get("/")).andReturn().getModelAndView(), INDEX);
	}

	@Test
	void test_HomeView_showsProjects() throws Exception {
		List<Project> projects = asList(new Project("first", Collections.emptyList()),
				new Project("second", Collections.emptyList()));

		when(projectService.getAllProjects()).thenReturn(projects);

		this.mvc.perform(get("/")).andExpect(view().name(INDEX)).andExpect(model().attribute("projects", projects))
				.andExpect(model().attribute(MESSAGE, ""));
	}

	@Test
	void test_HomeView_showsMessageWhenThereAreNoProjects() throws Exception {
		when(projectService.getAllProjects()).thenReturn(Collections.emptyList());

		this.mvc.perform(get("/")).andExpect(view().name(INDEX))
				.andExpect(model().attribute("projects", Collections.emptyList()))
				.andExpect(model().attribute(MESSAGE, "No projects"));
	}

	@Test
	void test_newProject_withNameShouldSave() throws Exception {
		Project project = new Project("new", Collections.emptyList());

		this.mvc.perform(post("/save").param("name", "new")).andExpect(view().name(REDIRECT));

		verify(projectService).insertNewProject(project);
	}
	
    @Test
    void test_newProject_withExistingNameShouldNotSave() throws Exception {
        Project project = new Project("new", Collections.emptyList());

        when(projectService.insertNewProject(project)).thenThrow(new ConflictingProjectNameException());

        mvc.perform(post("/save").param("name", "new"))
                .andExpect(view().name(INDEX))
                .andExpect(model().attribute(ERROR, "The specified name is already used for another project"));

        verify(projectService).insertNewProject(project);
    }
}
