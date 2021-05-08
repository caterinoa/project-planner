package it.unifi.projectplanner.bdd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;

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
		ProjectPlannerAppE2E.TASK_FIXTURE_1_ID = addTestTasksToTheDB(ProjectPlannerAppE2E.TASK_FIXTURE_1_DESCRIPTION).get(0).getId();
		ProjectPlannerAppE2E.TASK_FIXTURE_2_ID = addTestTasksToTheDB(ProjectPlannerAppE2E.TASK_FIXTURE_2_DESCRIPTION).get(1).getId(); 
	}

	@Given("A project has been removed from the database")
	public void in_the_meantime_the_project_has_been_removed_from_the_database() {
		String deleteURL = ProjectPlannerAppE2E.baseURL + "/api/projects/" + ProjectPlannerAppE2E.PROJECT_FIXTURE_1_ID;
		new RestTemplate().delete(deleteURL);
	}

	@Given("A task has been removed from the database")
	public void in_the_meantime_the_task_has_been_removed_from_the_database() {
		String deleteURL = ProjectPlannerAppE2E.baseURL + "/api/tasks/" + ProjectPlannerAppE2E.TASK_FIXTURE_1_ID;
		new RestTemplate().delete(deleteURL);
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
	
	private List<Task> addTestTasksToTheDB(String description) throws JSONException {
		JSONObject body = new JSONObject();
		body.put("description", description);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
		
		Long projectId = ProjectPlannerAppE2E.PROJECT_FIXTURE_1_ID;
		ResponseEntity<Project> response = new RestTemplate()
				.postForEntity(ProjectPlannerAppE2E.baseURL + "/api/projects/"+projectId+"/newtask", entity, Project.class);
		
		Collection<Task> tasks = response.getBody().getTasks();
		return new ArrayList<Task>(tasks);
	}

}
