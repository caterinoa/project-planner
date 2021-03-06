package it.unifi.projectplanner.controllers;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
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
import it.unifi.projectplanner.exceptions.NonExistingTaskException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.services.ProjectService;
import it.unifi.projectplanner.services.TaskService;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(controllers = TaskWebController.class)
class TaskWebControllerHtmlTest {
 
	private static final String PROJECT_TASKS_URL = "/projectTasks";
	private static final String EDIT_TASK = "/editTask";

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
				"1	first	No	Edit task	Delete\n" + 
				"2	second	No	Edit task	Delete"
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
	
	@Test
	void test_ProjectTasksPage_DeleteTask_ByExistingTaskIdShouldDelete() throws Exception {
		Project project = new Project(1L, "project", emptyList());
		when(projectService.getProjectById(1L)).thenReturn(project);
		
		this.webClient.getPage("/projectTasks/1/deletetask/1");
		verify(taskService, times(1)).deleteProjectTaskById(1L);
	}

	@Test
	void test_ProjectTasksPage_DeleteTask_ByNonExistingTaskIdShouldNotDelete() throws Exception {
		Long taskId = 1L;		
		when(projectService.getProjectById(1L)).thenReturn(new Project(1L, "project", emptyList()));
		doThrow(new NonExistingTaskException(taskId)).when(taskService).deleteProjectTaskById(taskId);
		
		this.webClient.getPage("/projectTasks/1/deletetask/1");
		verify(taskService, times(1)).deleteProjectTaskById(1L);
	}
	
	@Test
	void test_ProjectTasksPage_DeleteTask_OfNonExistingProjectShouldNotDelete() throws Exception {
		Long projectId = 1L;
		doThrow(new NonExistingProjectException(projectId)).when(projectService).getProjectById(projectId);
		this.webClient.getPage("/projectTasks/1/deletetask/1");
		verifyNoInteractions(taskService);
	}
	
	@Test
	void test_EditTaskPage_Title() throws Exception {
		when(taskService.getTaskById(1L)).thenReturn(new Task(1L, "task"));
		HtmlPage page = this.webClient.getPage(EDIT_TASK + "/1");
		assertThat(page.getTitleText()).isEqualTo("Edit task");
	}
	
	@Test
	void test_EditTaskPage_NonExistingTask() throws Exception {
		when(taskService.getTaskById(1L)).thenThrow(new NonExistingTaskException(1L));
		this.webClient.getPage(EDIT_TASK + "/1");
		verify(projectService, times(1)).getAllProjects();
	}
	
	@Test
	void test_EditTaskPage_UpdateTask_WithDescriptionAndCheckedCompletedShouldUpdate() throws Exception {
		Task task = new Task(1L, "task", new Project(1L, "project", emptyList()));
		when(taskService.getTaskById(1L)).thenReturn(task);
		
		HtmlPage page = this.webClient.getPage(EDIT_TASK + "/1");
		
		final HtmlForm form = page.getFormByName("edit_task_form");
		form.getInputByName("description").setValueAttribute("new description");
		form.getInputByName("completed").setChecked(true);
		form.getButtonByName("edit_task_submit").click();
		
		verify(taskService, times(1)).updateTask(task, "new description", true);
	}
	
	@Test
	void test_EditTaskPage_UpdateTask_WithDescriptionAndUncheckedCompletedShouldUpdate() throws Exception {
		Task task = new Task(1L, "task", new Project(1L, "project", emptyList()));
		when(taskService.getTaskById(1L)).thenReturn(task);
		
		HtmlPage page = this.webClient.getPage(EDIT_TASK + "/1");
		
		final HtmlForm form = page.getFormByName("edit_task_form");
		form.getInputByName("description").setValueAttribute("new description");
		form.getInputByName("completed").setChecked(false);
		form.getButtonByName("edit_task_submit").click();
		
		verify(taskService, times(1)).updateTask(task, "new description", false);
	}
	
	@Test
	void test_EditTaskPage_UpdateTask_WithEmptyDescriptionShouldNotUpdate() throws Exception {
		Task task = new Task(1L, "task", new Project(1L, "project", emptyList()));
		when(taskService.getTaskById(1L)).thenReturn(task);
		
		HtmlPage page = this.webClient.getPage(EDIT_TASK + "/1");
		
		final HtmlForm form = page.getFormByName("edit_task_form");
		form.getInputByName("description").setValueAttribute("");
		form.getInputByName("completed").setChecked(true);
		form.getButtonByName("edit_task_submit").click();
		
		verify(taskService, times(0)).updateTask(task, "new description", true);
	}
	
	@Test
	void test_EditTaskPage_UpdateTask_WithNotExistingIdShouldNotUpdate() throws Exception {
		when(taskService.getTaskById(1L)).thenReturn(new Task(1L, "task")).thenThrow(new NonExistingTaskException(1L));
		
		HtmlPage page = this.webClient.getPage(EDIT_TASK + "/1");
		
		final HtmlForm form = page.getFormByName("edit_task_form");
		form.getInputByName("description").setValueAttribute("new description");
		form.getInputByName("completed").setChecked(true);
		form.getButtonByName("edit_task_submit").click();
		
		InOrder inOrder = inOrder(taskService, projectService);
		inOrder.verify(taskService, times(2)).getTaskById(1L);
		inOrder.verify(projectService, times(1)).getAllProjects();
		inOrder.verifyNoMoreInteractions();
	}
}
