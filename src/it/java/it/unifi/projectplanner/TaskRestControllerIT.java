package it.unifi.projectplanner;

import static io.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import io.restassured.RestAssured;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.repositories.ProjectRepository;
import it.unifi.projectplanner.repositories.TaskRepository;
import it.unifi.projectplanner.services.ProjectService;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TaskRestControllerIT {

	@Container
	public static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:8.0.23")
			.withDatabaseName("projectplanner")
			.withExposedPorts(3306);

	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private TaskRepository taskRepository;
	@Autowired
	private ProjectService projectService;
	
	@LocalServerPort
	private int port;

	@DynamicPropertySource
	static void databaseProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
		registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQL8Dialect");
		registry.add("spring.datasource.url", MY_SQL_CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.username", MY_SQL_CONTAINER::getUsername);
		registry.add("spring.datasource.password", MY_SQL_CONTAINER::getPassword);
	}

	@BeforeEach
	void setup() {
		RestAssured.port = port;
		projectRepository.deleteAll();
		projectRepository.flush();
	}

	@Test
	void test() {
		assertTrue(MY_SQL_CONTAINER.isRunning());
	}

	@Test
	void test_AllProjectTasks() throws Exception {
		Project savedProject = projectRepository.save(new Project("project", new ArrayList<>()));
		Long projectId = savedProject.getId();
		Task firstTask = new Task("first", savedProject);
		Task secondTask = new Task("second", savedProject);
		savedProject = projectService.insertNewTaskIntoProject(firstTask, savedProject);
		projectService.insertNewTaskIntoProject(secondTask, savedProject);

		List<Task> retrievedTasks = asList(given().when().get("/api/projects/" + projectId).as(Task[].class));

		assertThat(retrievedTasks).containsAll(asList(firstTask, secondTask));
	}
	
	@Test
	void test_DeleteProjectTask() throws Exception {
		Project savedProject = projectRepository.save(new Project("saved", new ArrayList<>()));
		Task savedTask = taskRepository.save(new Task("saved task", savedProject));
		Long taskId = savedTask.getId();
		given().contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(taskId).when().delete("/api/tasks/" + taskId);

		assertFalse(savedProject.getTasks().contains(savedTask));
		assertFalse(taskRepository.findById(taskId).isPresent());
	}
}