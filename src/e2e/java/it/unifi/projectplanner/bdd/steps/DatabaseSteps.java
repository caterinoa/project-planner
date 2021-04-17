package it.unifi.projectplanner.bdd.steps;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import it.unifi.projectplanner.bdd.ProjectPlannerAppE2E;

public class DatabaseSteps {

	@Given("The database contains a few projects")
	public void the_database_contains_a_few_projects() throws JSONException {
		addTestProjectToTheDB(ProjectPlannerAppE2E.PROJECT_FIXTURE_1_NAME);
		addTestProjectToTheDB(ProjectPlannerAppE2E.PROJECT_FIXTURE_2_NAME);
	}

	@Given("The database contains a few tasks for a selected project")
	public void the_database_contains_a_few_tasks_for_a_selected_project() {
		// Write code here that turns the phrase above into concrete actions
		throw new io.cucumber.java.PendingException();
	}

	@When("In the meantime the project has been removed from the database")
	public void in_the_meantime_the_project_has_been_removed_from_the_database() {
		// Write code here that turns the phrase above into concrete actions
		throw new io.cucumber.java.PendingException();
	}

	@When("In the meantime the task has been removed from the database")
	public void in_the_meantime_the_task_has_been_removed_from_the_database() {
		// Write code here that turns the phrase above into concrete actions
		throw new io.cucumber.java.PendingException();
	}

	private String addTestProjectToTheDB(String name) throws JSONException {
		JSONObject body = new JSONObject();
		body.put("name", name);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
		
		System.out.println(ProjectPlannerAppE2E.baseURL + "/api/projects/new");
		
		ResponseEntity<String> response = new RestTemplate()
				.postForEntity("" + ProjectPlannerAppE2E.baseURL + "/api/projects/new", entity, String.class);
		
		JSONObject JsonResponse = new JSONObject(response.getBody());
		return JsonResponse.get("id").toString();
	}

}
