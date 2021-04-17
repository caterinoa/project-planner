package it.unifi.projectplanner.bdd;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import io.cucumber.java.After;
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

	@Before
	public static void setupScenario() {
		WebDriverManager.chromedriver().setup();
		webDriver = new ChromeDriver();
		baseURL = "http://localhost:" + ProjectPlannerAppE2E.port;
	}

	@After
	public void teardown() {
		webDriver.quit();
	}

}