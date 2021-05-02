package it.unifi.projectplanner.controllers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlTable;

import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.services.ProjectService;
import it.unifi.projectplanner.services.TaskService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = TaskWebController.class)
class TaskWebControllerHtmlTest {
 
	private static final String PROJECT_TASKS_URL = "/projectTasks";

	@Autowired
	private WebClient webClient;

	@MockBean
	private ProjectService projectService;
	@MockBean
	private TaskService taskService;
	
	@Test
	void test_ProjectTasksPage_Title() throws Exception {
		HtmlPage page = this.webClient.getPage(PROJECT_TASKS_URL + "/1");
		assertThat(page.getTitleText()).isEqualTo("Project tasks");
	}
	
	@Test
	void test_ProjectTasksPage_OfNonExistingProject() throws Exception {
		when(taskService.getAllProjectTasks(1L)).thenThrow(new NonExistingProjectException(1L));
		HtmlPage page = this.webClient.getPage(PROJECT_TASKS_URL + "/1");
		
		assertThat(page.getBody().getTextContent()).contains("The project with id=1 does not exist");
	}
	
	@Test
	void test_ProjectTasksPage_WithNoTasks() throws Exception {
		when(taskService.getAllProjectTasks(1L)).thenReturn(emptyList());
		HtmlPage page = this.webClient.getPage(PROJECT_TASKS_URL + "/1");

		assertThat(page.getBody().getTextContent()).contains("Project 1 tasks");
		assertThat(page.getBody().getTextContent()).contains("No tasks");
	}
	
	@Test
	void test_ProjectTasksPage_WithTasksShouldShowThemInATable() throws Exception {
		Project project = new Project(1L, "project", emptyList());
		when(taskService.getAllProjectTasks(1L))
				.thenReturn(asList(new Task(1L, "first", project), new Task(2L, "second", project)));

		HtmlPage page = this.webClient.getPage(PROJECT_TASKS_URL + "/1");
		assertThat(page.getBody().getTextContent()).contains("Project 1 tasks");
		assertThat(page.getBody().getTextContent()).doesNotContain("No tasks");

		HtmlTable table = page.getHtmlElementById("tasks_table");
		assertThat(table.asText()).isEqualTo(
				"Project tasks\n" +
				"ID	Description	Completed\n" + 
				"1	first	No	View tasks	Delete\n" + 
				"2	second	No	View tasks	Delete"
		);
	}
	
	@Test
	void test_ProjectTasksPage_NewTaskIntoProject_WithDescriptionShouldInsert() throws Exception {
		Project project = new Project(1L, "project", emptyList());
		when(projectService.getProjectById(1L)).thenReturn(project);

		HtmlPage page = this.webClient.getPage(PROJECT_TASKS_URL + "/1");
		
		final HtmlForm form = page.getFormByName("new_task_form");
		form.getInputByName("description").setValueAttribute("new task");
		form.getButtonByName("new_task_submit").click();
		
		verify(projectService, times(1)).insertNewTaskIntoProject(new Task("new task", project), project);
	}
	
	@Test
	void test_ProjectTasksPage_NewTaskIntoProject_WithNoDescriptionShouldNotInsert() throws Exception {
		Project project = new Project(1L, "project", emptyList());
		when(projectService.getProjectById(1L)).thenReturn(project);

		HtmlPage page = this.webClient.getPage(PROJECT_TASKS_URL + "/1");
		
		final HtmlForm form = page.getFormByName("new_task_form");
		form.getInputByName("description").setValueAttribute("");
		form.getButtonByName("new_task_submit").click();
		
		verify(projectService, times(0)).insertNewTaskIntoProject(new Task("new task", project), project);
	}
	
	@Test
	void test_ProjectTasksPage_NewTaskIntoProject_OfNotExistingProjectShouldNotInsert() throws Exception {
		
		when(projectService.getProjectById(1L)).thenThrow(new NonExistingProjectException(1L));
		
		HtmlPage page = this.webClient.getPage(PROJECT_TASKS_URL + "/1");
		
		final HtmlForm form = page.getFormByName("new_task_form");
		form.getInputByName("description").setValueAttribute("new task");
		form.getButtonByName("new_task_submit").click();
		
		InOrder inOrder = inOrder(projectService);		
		inOrder.verify(projectService, times(1)).getProjectById(1L);
		inOrder.verify(projectService, times(1)).getAllProjects();
		verifyNoMoreInteractions(projectService);
	}
}
