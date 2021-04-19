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
			buttonName = "delete_project" + ProjectPlannerAppE2E.PROJECT_FIXTURE_1_ID.toString();
		}
		ProjectPlannerAppE2E.webDriver.findElement(By.id(buttonName)).click();
	}

}
