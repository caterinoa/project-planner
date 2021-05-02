package it.unifi.projectplanner.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.services.TaskService;

@RestController
@RequestMapping("/api/projects")
public class TaskRestController {

	@Autowired
	private TaskService taskService;
	
	@GetMapping(value = "/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Task> allProjectTasks(@PathVariable Long projectId) throws NonExistingProjectException {
		return taskService.getAllProjectTasks(projectId);
	}

}
