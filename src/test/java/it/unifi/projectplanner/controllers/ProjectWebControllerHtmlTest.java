package it.unifi.projectplanner.controllers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

import it.unifi.projectplanner.exceptions.ConflictingProjectNameException;
import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.services.ProjectService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = ProjectWebController.class)
class ProjectWebControllerHtmlTest {

	@Autowired
	private WebClient webClient;

	@MockBean
	private ProjectService projectService;
	
	@Test
	void test_HomePage_Title() throws Exception {
		HtmlPage page = this.webClient.getPage("/");
		assertThat(page.getTitleText()).isEqualTo("Projects");
	}
	
	@Test
	void test_HomePage_WithNoProjects() throws Exception {
		when(projectService.getAllProjects()).thenReturn(emptyList());

		HtmlPage page = this.webClient.getPage("/");

		assertThat(page.getBody().getTextContent()).contains("No projects");
	}

	@Test
	void test_HomePage_WithProjectsShouldShowThemInATable() throws Exception {
		when(projectService.getAllProjects())
				.thenReturn(asList(new Project(1L, "first", emptyList()), new Project(2L, "second", emptyList())));

		HtmlPage page = this.webClient.getPage("/");
		assertThat(page.getBody().getTextContent()).doesNotContain("No projects");

		HtmlTable table = page.getHtmlElementById("projects_table");
		assertThat(table.asText()).isEqualTo(
				"My projects\n" +
				"ID	Name	Completion percentage\n" + 
				"1	first	0%	Delete\n" + 
				"2	second	0%	Delete"
		);
	}
	
	@Test
	void test_HomePage_NewProject_WithNameShouldInsert() throws Exception {
		HtmlPage page = this.webClient.getPage("/");
		
		final HtmlForm form = page.getFormByName("new_project_form");
		form.getInputByName("name").setValueAttribute("new");
		form.getButtonByName("new_project_submit").click();
		
		verify(projectService, times(1)).insertNewProject(new Project("new", emptyList()));
	}
	
	@Test
	void test_HomePage_NewProject_WithExistingNameShouldNotInsert() throws Exception {
		String existingProjectName = "existing project";
		Project project = new Project(existingProjectName, emptyList());
		when(projectService.insertNewProject(project)).thenThrow(new ConflictingProjectNameException(existingProjectName));
				
		HtmlPage page = this.webClient.getPage("/");
		final HtmlForm form = page.getFormByName("new_project_form");
		form.getInputByName("name").setValueAttribute(existingProjectName);
		form.getButtonByName("new_project_submit").click();
		
		verify(projectService, times(1)).insertNewProject(project);
	}
	
	@Test
	void test_HomePage_NewProject_WithNoNameShouldNotInsert() throws Exception {
		HtmlPage page = this.webClient.getPage("/");
		
		final HtmlForm form = page.getFormByName("new_project_form");
		form.getInputByName("name").setValueAttribute("");
		form.getButtonByName("new_project_submit").click();
		
		verify(projectService, times(0)).insertNewProject(new Project("", emptyList()));
	}

	@Test
	void test_HomePage_DeleteProject_ByExistingIdShouldDelete() throws Exception {
		this.webClient.getPage("/delete/1");
		verify(projectService, times(1)).deleteProjectById(1L);
	}

	@Test
	void test_HomePage_DeleteProject_ByNonExistingIdShouldNotDelete() throws Exception {
		doThrow(new NonExistingProjectException(1L)).when(projectService).deleteProjectById(1L);
		this.webClient.getPage("/delete/1");
		verify(projectService, times(1)).deleteProjectById(1L);
	}
}
