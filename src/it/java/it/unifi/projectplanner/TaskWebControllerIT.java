package it.unifi.projectplanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.repositories.ProjectRepository;
import it.unifi.projectplanner.repositories.TaskRepository;
import it.unifi.projectplanner.services.ProjectService;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TaskWebControllerIT {

	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private ProjectService projectService;

	@LocalServerPort
	private int port;

	private WebDriver webDriver;

	private String projectTasksURL;

	private static final String SAVED_PROJECT = "saved project";
	private static final String SAVED_TASK = "first task";
	private static final String SAVED_TASK_2 = "second task";
	private static final String NEW_TASK = "new task";
	

	@BeforeEach
	public void setup() {
		projectTasksURL = "http://localhost:" + port + "/projectTasks";
		webDriver = new HtmlUnitDriver();
		projectRepository.deleteAll();
		projectRepository.flush();
	}

	@AfterEach
	public void teardown() {
		webDriver.quit();
	}

	@Test
	void test_ProjectTasksPage_ViewTasks() {
		Project project = projectRepository.save(new Project(SAVED_PROJECT, new ArrayList<>()));
		Task first = new Task(SAVED_TASK, project);
		Task second = new Task(SAVED_TASK_2, project);
		project = projectService.insertNewTaskIntoProject(first, project);
		projectService.insertNewTaskIntoProject(second, project);
		
		webDriver.get(projectTasksURL + "/" + project.getId());
		Long firstTaskId = project.getTasks().iterator().next().getId();
		Long secondTaskId = project.getTasks().iterator().next().getId();
		assertThat(webDriver.findElement(By.id("tasks_table")).getText()).contains(firstTaskId.toString(), SAVED_TASK, "No");
		assertThat(webDriver.findElement(By.id("tasks_table")).getText()).contains(secondTaskId.toString(), SAVED_TASK_2, "No");
	}
	
	@Test
	void test_ProjectTasksPage_ViewTasksOfANonExistingProject_ShouldShowErrorMessage() {
		webDriver.get(projectTasksURL + "/1");
		assertThat(webDriver.findElement(By.id("error")).getText()).isEqualTo("The project with id=1 does not exist");
	}
	
	@Test
	void test_ProjectTasksPage_NewProjectTask() {
		Project project = projectRepository.save(new Project(SAVED_PROJECT, new ArrayList<>()));
		webDriver.get(projectTasksURL + "/" + project.getId());
		
		webDriver.findElement(By.name("description")).sendKeys(NEW_TASK);
		webDriver.findElement(By.name("new_task_submit")).click();
		
		Task retrievedTask = taskRepository.findByProject(project).iterator().next();
		assertThat(retrievedTask.getDescription()).isEqualTo(NEW_TASK);
		assertThat(webDriver.findElement(By.id("tasks_table")).getText()).contains(retrievedTask.getId().toString(), NEW_TASK, "No");
	}
	
	@Test
	void test_HomePage_DeleteTask_WithExistingTaskIdShouldDelete() {
		Project savedProject = projectRepository.save(new Project(SAVED_PROJECT, new ArrayList<>()));
		savedProject = projectService.insertNewTaskIntoProject(new Task(SAVED_TASK, savedProject), savedProject);
		Long projectId = savedProject.getId();
		Long taskId = savedProject.getTasks().iterator().next().getId();
		
		webDriver.get(projectTasksURL + "/" + projectId + "/deletetask/" + taskId);
		Optional<Task> retrievedTask = taskRepository.findById(taskId);
		assertFalse(retrievedTask.isPresent());
	}
	
	@Test
	void test_HomePage_DeleteProject_WithNonExistingProjectIdShouldShowErrorMessage() {
		Project savedProject = projectRepository.save(new Project(SAVED_PROJECT, new ArrayList<>()));
		webDriver.get(projectTasksURL + "/" + savedProject.getId() + "/deletetask/1");
		assertThat(webDriver.findElement(By.id("error")).getText()).isEqualTo("The task with id=1 does not exist");
	}
}
