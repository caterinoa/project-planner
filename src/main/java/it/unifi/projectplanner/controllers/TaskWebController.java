package it.unifi.projectplanner.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
	private static final String EDIT_TASK = "editTask";
	private static final String REDIRECT_PROJECT_TASKS = "redirect:/projectTasks";
	private static final String PROJECTS_ATTRIBUTE = "projects";
	private static final String PROJECT_ID_ATTRIBUTE = "project_id";
	private static final String PROJECT_TASKS_ATTRIBUTE = "tasks";
	private static final String TASK_ID_ATTRIBUTE = "task_id";
	private static final String TASK_DESCRIPTION_ATTRIBUTE = "task_description";
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
	public String saveTaskIntoProject(@PathVariable("projectId") Long projectId, TaskDTO taskDTO, Model model) {
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
		if (description.isEmpty()) {
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
	public String deleteTaskFromProject(@PathVariable Long projectId, @PathVariable Long taskId, Model model) {
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
			taskService.deleteProjectTaskById(taskId);
		} catch (NonExistingTaskException e) {
			model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
			model.addAttribute(PROJECT_ID_ATTRIBUTE, projectId);
			model.addAttribute(PROJECT_TASKS_ATTRIBUTE, project.getTasks());
			page = PROJECT_TASKS;
		}
		return page;
	}
	
	@GetMapping("/editTask/{taskId}")
	public String editTask(@PathVariable("taskId") Long taskId, Model model) {
		model.addAttribute(TASK_ID_ATTRIBUTE, taskId);
		Task task;
		try {
			task = taskService.getTaskById(taskId);
			model.addAttribute(TASK_DESCRIPTION_ATTRIBUTE, task.getDescription());
		} catch (NonExistingTaskException e) {
			model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
			model.addAttribute(PROJECTS_ATTRIBUTE, projectService.getAllProjects());
			return INDEX;
		}
		return EDIT_TASK;
	}
	
	@PostMapping("/editTask/{taskId}")
	public String updateTask(@PathVariable("taskId") Long taskId, TaskDTO taskDTO, Model model) {
		Task task;
		try {
			task = taskService.getTaskById(taskId);
		} catch (NonExistingTaskException e) {
			model.addAttribute(ERROR_ATTRIBUTE, e.getMessage());
			model.addAttribute(PROJECTS_ATTRIBUTE, projectService.getAllProjects());
			return INDEX;
		}
		String page = REDIRECT_PROJECT_TASKS + "/" + task.projectId();
		String description = taskDTO.getDescription();
		String completed = (taskDTO.getCompleted() != null) ? taskDTO.getCompleted() : "0";
		if (description.isEmpty()) {
			model.addAttribute(ERROR_ATTRIBUTE, "The task description should not be empty");
			page = EDIT_TASK;
		} else {
			taskService.updateTask(task, description, completed.equals("1"));
		}
		return page;
	}
}
