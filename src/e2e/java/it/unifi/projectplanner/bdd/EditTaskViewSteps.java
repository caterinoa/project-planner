package it.unifi.projectplanner.bdd;

import static org.assertj.core.api.Assertions.assertThat;

import org.openqa.selenium.By;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class EditTaskViewSteps {
	
	@Given("The Edit Task View is shown")
	public void the_edit_task_view_is_shown() {
		String url = ProjectPlannerAppE2E.baseURL + "/editTask/" + ProjectPlannerAppE2E.TASK_FIXTURE_1_ID;
		ProjectPlannerAppE2E.webDriver.get(url);
	}
	
	@Given("The Edit Task View is shown containing the id of the task")
	public void the_edit_view_is_shown_containing_the_id_of_the_task() {
		String shouldBePresent = "Edit task " + ProjectPlannerAppE2E.TASK_FIXTURE_1_ID;
		assertThat(ProjectPlannerAppE2E.webDriver.findElement(By.id("page_subtitle")).getText())
			.contains(shouldBePresent);
	}
	
	@Given("The user provides task data in the fields")
	public void the_user_provides_task_data_in_the_fields() {
		ProjectPlannerAppE2E.webDriver.findElement(By.name("description")).clear();
		ProjectPlannerAppE2E.webDriver.findElement(By.name("description")).sendKeys(ProjectPlannerAppE2E.TASK_UPDATED_DESCRIPTION);
		ProjectPlannerAppE2E.webDriver.findElement(By.name("completed")).click();
	}

	@Then("The Task View is shown containing the updated task")
	public void the_updated_task_is_shown_in_the_list() {
		assertThat(ProjectPlannerAppE2E.webDriver.findElement(By.id("tasks_table")).getText())
			.contains(ProjectPlannerAppE2E.TASK_UPDATED_DESCRIPTION);
	}
}
