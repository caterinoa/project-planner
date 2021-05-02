package it.unifi.projectplanner.bdd;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;

public class DatabaseSteps {

	@After
	public void teardown() {
		ProjectPlannerAppE2E.webDriver.quit();
	}

	@Given("The database contains a few projects")
	public void the_database_contains_a_few_projects() throws JSONException {
		ProjectPlannerAppE2E.PROJECT_FIXTURE_1_ID = addTestProjectToTheDB(ProjectPlannerAppE2E.PROJECT_FIXTURE_1_NAME);
		ProjectPlannerAppE2E.PROJECT_FIXTURE_2_ID = addTestProjectToTheDB(ProjectPlannerAppE2E.PROJECT_FIXTURE_2_NAME);
	}

	@Given("The database contains a few tasks for a selected project")
	public void the_database_contains_a_few_tasks_for_a_selected_project() throws JSONException {
		ProjectPlannerAppE2E.PROJECT_FIXTURE_1_ID = addTestProjectToTheDB(ProjectPlannerAppE2E.PROJECT_FIXTURE_1_NAME);
		addTestTasksToTheDB(ProjectPlannerAppE2E.TASK_FIXTURE_1_DESCRIPTION);
		addTestTasksToTheDB(ProjectPlannerAppE2E.TASK_FIXTURE_2_DESCRIPTION);
	}

	@Given("A project has been removed from the database")
	public void in_the_meantime_the_project_has_been_removed_from_the_database() {
		String deleteURL = ProjectPlannerAppE2E.baseURL + "/api/projects/" + ProjectPlannerAppE2E.PROJECT_FIXTURE_1_ID;
		new RestTemplate().delete(deleteURL);
	}

	@Given("A task has been removed from the database")
	public void in_the_meantime_the_task_has_been_removed_from_the_database() {
		// Write code here that turns the phrase above into concrete actions
		throw new io.cucumber.java.PendingException();
	}

	private Long addTestProjectToTheDB(String name) throws JSONException {
		JSONObject body = new JSONObject();
		body.put("name", name);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
		
		ResponseEntity<String> response = new RestTemplate()
				.postForEntity(ProjectPlannerAppE2E.baseURL + "/api/projects/new", entity, String.class);
		
		JSONObject JsonResponse = new JSONObject(response.getBody());
		String id = JsonResponse.get("id").toString();
		return Long.parseLong(id);
	}
	
	private void addTestTasksToTheDB(String description) throws JSONException {
		JSONObject body = new JSONObject();
		body.put("description", description);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
		
		Long id = ProjectPlannerAppE2E.PROJECT_FIXTURE_1_ID;
		ResponseEntity<String> response = new RestTemplate()
				.postForEntity(ProjectPlannerAppE2E.baseURL + "/api/projects/"+id+"/newtask", entity, String.class);
	}

}
