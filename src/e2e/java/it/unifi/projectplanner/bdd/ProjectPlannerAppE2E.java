package it.unifi.projectplanner.bdd;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.web.client.RestTemplate;

import io.cucumber.java.Before;
import io.cucumber.junit.platform.engine.Cucumber;
import io.github.bonigarcia.wdm.WebDriverManager;

@Cucumber
public class ProjectPlannerAppE2E {

	public static int port = Integer.parseInt(System.getProperty("server.port", "8080"));
	public static String baseURL = "http://localhost:" + ProjectPlannerAppE2E.port;

	public static WebDriver webDriver;

	public static final String PROJECT_FIXTURE_1_NAME = "first project";
	public static final String PROJECT_FIXTURE_2_NAME = "second project";
	public static final String NEW_PROJECT_FIXTURE_NAME = "new project";
	public static final String TASK_FIXTURE_1_DESCRIPTION = "first task";
	public static final String TASK_FIXTURE_2_DESCRIPTION = "second task";
	public static final String NEW_TASK_FIXTURE_NAME = "new task";
	public static Long PROJECT_FIXTURE_1_ID;
	public static Long PROJECT_FIXTURE_2_ID;

	@Before
	public void setupScenario() {
		WebDriverManager.chromedriver().setup();
		webDriver = new ChromeDriver();
		baseURL = "http://localhost:" + ProjectPlannerAppE2E.port;
		new RestTemplate().delete(baseURL + "/api/projects/deleteall");
	}

}