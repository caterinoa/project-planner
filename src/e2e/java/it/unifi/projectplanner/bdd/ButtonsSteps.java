package it.unifi.projectplanner.bdd.steps;

import org.openqa.selenium.By;

import io.cucumber.java.en.When;
import it.unifi.projectplanner.bdd.ProjectPlannerAppE2E;

public class ButtonsSteps {
	
	@When("The user clicks on the {string} button")
	public void the_user_clicks_on_the_button(String buttonText) {
		String buttonName = "";
		if(buttonText.equals("New Project")) {
			buttonName = "new_project_submit";
		}
		ProjectPlannerAppE2E.webDriver.findElement(By.name(buttonName)).click();
	}
	
}
