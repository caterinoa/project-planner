package it.unifi.projectplanner.bdd;

import org.openqa.selenium.By;

import io.cucumber.java.en.When;

public class ButtonsSteps {

	@When("The user clicks on the {string} button")
	public void the_user_clicks_on_the_button(String buttonText) {
		String buttonName = "";
		if (buttonText.equals("New Project")) {
			buttonName = "new_project_submit";
		} else if (buttonText.equals("Delete Project")) {
			buttonName = "delete_project" + ProjectPlannerAppE2E.PROJECT_FIXTURE_1_ID;
		} else if (buttonText.equals("View Tasks")) {
			buttonName = "view_project_tasks" + ProjectPlannerAppE2E.PROJECT_FIXTURE_1_ID;
		} else if (buttonText.equals("New Task")) {
			buttonName = "new_task_submit";
		} else if (buttonText.equals("Delete Task")) {
			buttonName = "delete_task" + ProjectPlannerAppE2E.TASK_FIXTURE_1_ID;
		}
		ProjectPlannerAppE2E.webDriver.findElement(By.id(buttonName)).click();
	}

}
