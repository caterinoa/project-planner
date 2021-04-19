package it.unifi.projectplanner;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
import it.unifi.projectplanner.repositories.ProjectRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class ProjectWebControllerIT {

	@Autowired
	private ProjectRepository projectRepository;

	@LocalServerPort
	private int port;

	private WebDriver webDriver;

	private String baseURL;

	private static final String SAVED_PROJECT = "saved project";
	private static final String NEW_PROJECT = "new project";

	@BeforeEach
	public void setup() {
		baseURL = "http://localhost:" + port;
		webDriver = new HtmlUnitDriver();
		projectRepository.deleteAll();
		projectRepository.flush();
	}

	@AfterEach
	public void teardown() {
		webDriver.quit();
	}

	@Test
	void test_HomePage() {
		Project project = projectRepository.save(new Project(SAVED_PROJECT, emptyList()));
		webDriver.get(baseURL);
		Long id = project.getId();
		assertThat(webDriver.findElement(By.id("projects_table")).getText()).contains(id.toString(), SAVED_PROJECT, "0%");
	}

	@Test
	void test_HomePage_NewProject() {
		webDriver.get(baseURL);
		
		webDriver.findElement(By.name("name")).sendKeys(NEW_PROJECT);
		webDriver.findElement(By.name("new_project_submit")).click();
		
		Optional<Project> retrievedProject = projectRepository.findByName(NEW_PROJECT);
		assertTrue(retrievedProject.isPresent());
		assertThat(webDriver.findElement(By.id("projects_table")).getText()).contains(retrievedProject.get().getId().toString(), NEW_PROJECT, "0%");
	}
	
	@Test
	void test_HomePage_NewProjectWithExistingName_ShouldShowErrorMessage() {
		projectRepository.save(new Project(SAVED_PROJECT, emptyList()));
		
		webDriver.get(baseURL);
		webDriver.findElement(By.name("name")).sendKeys(SAVED_PROJECT);
		webDriver.findElement(By.name("new_project_submit")).click();
		
		assertThat(webDriver.findElement(By.id("error")).getText()).isEqualTo("The name '" + SAVED_PROJECT + "' is already used for another project");
	}
	
	@Test
	void test_HomePage_DeleteProject() {
		Project saved = projectRepository.save(new Project(SAVED_PROJECT, emptyList()));
		projectRepository.save(new Project("second project", emptyList()));
		String id = saved.getId().toString();
		webDriver.get(baseURL + "/delete/" + id);
		
		Optional<Project> retrievedProject = projectRepository.findByName(SAVED_PROJECT);
		assertFalse(retrievedProject.isPresent());
		assertThat(webDriver.findElement(By.id("projects_table")).getText()).doesNotContain(id, SAVED_PROJECT);
	}
	
	@Test
	void test_HomePage_DeleteNonExistingProject_ShouldShowErrorMessage() {
		webDriver.get(baseURL + "/delete/1");
		assertThat(webDriver.findElement(By.id("error")).getText()).isEqualTo("The project with id=1 does not exist");
	}
}
