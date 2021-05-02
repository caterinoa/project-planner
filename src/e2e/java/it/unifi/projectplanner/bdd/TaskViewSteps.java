package it.unifi.projectplanner.bdd;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class TaskViewSteps {

	@Given("The Task View is shown")
	public void the_task_view_is_shown() {
		String url = ProjectPlannerAppE2E.baseURL + "/projectTasks/" + ProjectPlannerAppE2E.PROJECT_FIXTURE_1_ID;
		ProjectPlannerAppE2E.webDriver.get(url);
	}

	@Given("The id of the project is shown")
	public void the_id_of_the_project_is_shown() {
		String shouldBePresent = "Project " + ProjectPlannerAppE2E.PROJECT_FIXTURE_1_ID + " tasks";
		assertThat(ProjectPlannerAppE2E.webDriver.findElement(By.id("page_subtitle")).getText())
			.contains(shouldBePresent);
	}

	@Given("A list is shown containing the tasks that are stored in the database for the project")
	public void a_list_is_shown_containing_the_tasks_that_are_stored_in_the_database_for_the_project() {
		assertThat(ProjectPlannerAppE2E.webDriver.findElement(By.id("tasks_table")).getText())
			.contains(ProjectPlannerAppE2E.TASK_FIXTURE_1_DESCRIPTION);
		assertThat(ProjectPlannerAppE2E.webDriver.findElement(By.id("tasks_table")).getText())
			.contains(ProjectPlannerAppE2E.TASK_FIXTURE_2_DESCRIPTION);
	}

	@Given("The user provides task description in the text field")
	public void the_user_provides_task_description_in_the_text_field() {
		ProjectPlannerAppE2E.webDriver.findElement(By.name("description")).sendKeys(ProjectPlannerAppE2E.NEW_TASK_FIXTURE_NAME);
	}

	@Then("The list contains the new task")
	public void the_list_contains_the_new_task() {
		assertThat(ProjectPlannerAppE2E.webDriver.findElement(By.id("tasks_table")).getText())
			.contains(ProjectPlannerAppE2E.NEW_TASK_FIXTURE_NAME);
	}

	@Then("The task is removed from the list")
	public void the_task_is_removed_from_the_list() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}

	@Then("An error is shown containing the id of the selected task")
	public void an_error_is_shown_containing_the_name_of_the_selected_task() {
	    // Write code here that turns the phrase above into concrete actions
	    throw new io.cucumber.java.PendingException();
	}
}
