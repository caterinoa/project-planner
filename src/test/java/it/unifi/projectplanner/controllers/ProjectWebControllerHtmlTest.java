package it.unifi.projectplanner.controllers;

import static java.util.Collections.emptyList;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
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
		when((projectService.getAllProjects())).thenReturn(emptyList());

		HtmlPage page = this.webClient.getPage("/");

		assertThat(page.getBody().getTextContent()).contains("No projects");
	}

	@Test
	void test_HomePage_WithProjects_ShouldShowThemInATable() throws Exception {
		when((projectService.getAllProjects()))
				.thenReturn(asList(new Project(1L, "first", emptyList()), new Project(2L, "second", emptyList())));

		HtmlPage page = this.webClient.getPage("/");
		assertThat(page.getBody().getTextContent()).doesNotContain("No projects");

		HtmlTable table = page.getHtmlElementById("projects_table");
		assertThat(table.asText()).isEqualTo(
				"ID	Name	Completion percentage\n" + 
				"1	first	0%\n" + 
				"2	second	0%"
		);
	}
	
	@Test
	void test_NewProject() throws Exception {
		HtmlPage page = this.webClient.getPage("/");
		
		final HtmlForm form = page.getFormByName("new_project_form");
		form.getInputByName("name").setValueAttribute("new");
		form.getButtonByName("new_project_submit").click();
		
		verify(projectService, times(1)).insertNewProject(new Project("new", emptyList()));
	}

}
