package it.unifi.projectplanner;

import static io.restassured.RestAssured.given;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

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
import io.restassured.response.Response;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.repositories.ProjectRepository;

@Testcontainers
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ProjectRestControllerIT {

	@Container
	public static final MySQLContainer<?> MY_SQL_CONTAINER = new MySQLContainer<>("mysql:8.0.23")
			.withDatabaseName("projectplanner")
			.withExposedPorts(3306);

	@Autowired
	private ProjectRepository projectRepository;

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
	void test_AllProjects() throws Exception {
		Project first = projectRepository.save(new Project("first", emptyList()));
		Project second = projectRepository.save(new Project("second", emptyList()));
		List<Project> savedProjects = asList(first, second);

		List<Project> retrievedProjects = asList(given().when().get("/api/projects").as(Project[].class));

		assertThat(retrievedProjects).containsAll(savedProjects);
	}

	@Test
	void test_NewProject() throws Exception {
		Response response = given().contentType(MediaType.APPLICATION_JSON_VALUE)
				.body(new Project("project", emptyList())).when().post("/api/projects/new");

		Project saved = response.getBody().as(Project.class);
		Project retrieved = projectRepository.findById(saved.getId()).get();
		assertThat(saved).isEqualTo(retrieved);
	}

}