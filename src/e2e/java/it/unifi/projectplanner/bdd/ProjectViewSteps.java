package it.unifi.projectplanner.bdd;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class ProjectViewSteps {

	@After
	public void teardown() {
		ProjectPlannerAppE2E.webDriver.quit();
	}

	@Given("The Project View is shown")
	public void the_project_view_is_shown() {
		ProjectPlannerAppE2E.webDriver.get(ProjectPlannerAppE2E.baseURL);		
	}

	@Given("A list is shown containing the projects that are stored in the database")
	public void a_list_is_shown_containing_the_projects_that_are_stored_in_the_database() {
		assertThat(ProjectPlannerAppE2E.webDriver.findElement(By.id("projects_table")).getText())
			.contains(ProjectPlannerAppE2E.PROJECT_FIXTURE_1_NAME);
		assertThat(ProjectPlannerAppE2E.webDriver.findElement(By.id("projects_table")).getText())
			.contains(ProjectPlannerAppE2E.PROJECT_FIXTURE_2_NAME);
	}

	@Given("The user provides project name in the text field")
	public void the_user_provides_project_name_in_the_text_field() {
		ProjectPlannerAppE2E.webDriver.findElement(By.name("name")).sendKeys(ProjectPlannerAppE2E.NEW_PROJECT_FIXTURE_NAME);
	}

	@Then("The list contains the new project")
	public void the_list_contains_the_new_project() {
		assertThat(ProjectPlannerAppE2E.webDriver.findElement(By.id("projects_table")).getText())
			.contains(ProjectPlannerAppE2E.NEW_PROJECT_FIXTURE_NAME);
	}

	@Then("The project is removed from the list")
	public void the_project_is_removed_from_the_list() {
		assertThat(ProjectPlannerAppE2E.webDriver.findElement(By.id("projects_table")).getText())
		.doesNotContain(ProjectPlannerAppE2E.PROJECT_FIXTURE_1_NAME);
	}

	@Then("An error is shown containing the id of the selected project")
	public void an_error_is_shown_containing_the_name_of_the_selected_project() {
		assertThat(ProjectPlannerAppE2E.webDriver.findElement(By.id("error")).getText())
		.isEqualTo("The project with id=" + ProjectPlannerAppE2E.PROJECT_FIXTURE_1_ID + " does not exist");
	}

}
