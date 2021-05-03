package it.unifi.projectplanner.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.unifi.projectplanner.dto.TaskDTO;
import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.exceptions.NonExistingTaskException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.services.ProjectService;
import it.unifi.projectplanner.services.TaskService;

@Controller
public class TaskWebController {

	private static final String INDEX = "index";
	private static final String PROJECT_TASKS = "projectTasks";
	private static final String REDIRECT_PROJECT_TASKS = "redirect:/projectTasks";
	private static final String PROJECTS_ATTRIBUTE = "projects";
	private static final String PROJECT_ID_ATTRIBUTE = "project_id";
	private static final String PROJECT_TASKS_ATTRIBUTE = "tasks";
	private static final String ERROR_ATTRIBUTE = "error";
	private static final String MESSAGE_ATTRIBUTE = "message";

	@Autowired
	private ProjectService projectService;
	@Autowired
	private TaskService taskService;
	
	@GetMapping("/projectTasks/{projectId}")
	public String viewProjectTasks(@PathVariable Long projectId, Model model) {
		String page = INDEX;
		List<Task> allProjectTasks;
		try {
			allProjectTasks = taskService.getAllProjectTasks(projectId);
			model.addAttribute(PROJECT_TASKS_ATTRIBUTE, allProjectTasks);
			model.addAttribute(PROJECT_ID_ATTRIBUTE, projectId);
			model.addAttribute(MESSAGE_ATTRIBUTE, allProjectTasks.isEmpty() ? "No tasks" : "");
			page = PROJECT_TASKS;
		} catch (NonExistingProjectException e) {
			model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
			List<Project> allProjects = projectService.getAllProjects();
			model.addAttribute(PROJECTS_ATTRIBUTE, allProjects);
		}
		return page;
	}
	
	@PostMapping("/projectTasks/{projectId}/savetask")
	public String saveTaskIntoProject(@PathVariable("projectId") Long projectId,
			@ModelAttribute("description") TaskDTO taskDTO, Model model) {
		String description = taskDTO.getDescription();
		String page = REDIRECT_PROJECT_TASKS + "/" + projectId;
		Project project;
		try {
			project = projectService.getProjectById(projectId);
		} catch (NonExistingProjectException e) {
			model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
			model.addAttribute(PROJECTS_ATTRIBUTE, projectService.getAllProjects());
			return INDEX;
		}
		if (description == null) {
			model.addAttribute(ERROR_ATTRIBUTE, "The task description should not be empty");
			model.addAttribute(PROJECT_ID_ATTRIBUTE, projectId);
			model.addAttribute(PROJECT_TASKS_ATTRIBUTE, project.getTasks());
			page = PROJECT_TASKS;
		} else {
			projectService.insertNewTaskIntoProject(new Task(description, project), project);
		}
		return page;
	}
	
	@GetMapping("/projectTasks/{projectId}/deletetask/{taskId}")
	public String deleteTask(@PathVariable Long projectId, @PathVariable Long taskId, Model model) {
		String page = REDIRECT_PROJECT_TASKS + "/" + projectId;
		Project project;
		try {
			project = projectService.getProjectById(projectId);
		} catch (NonExistingProjectException e) {
			model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
			model.addAttribute(PROJECTS_ATTRIBUTE, projectService.getAllProjects());
			return INDEX;
		}
		try {
			taskService.deleteTaskById(taskId);
		} catch (NonExistingTaskException e) {
			model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
			model.addAttribute(PROJECT_TASKS_ATTRIBUTE, project.getTasks());
			page = PROJECT_TASKS;
		}
		return page;
	}

}
