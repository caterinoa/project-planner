package it.unifi.projectplanner.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import it.unifi.projectplanner.dto.TaskDTO;
import it.unifi.projectplanner.exceptions.EmptyMandatoryInputException;
import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.exceptions.NonExistingTaskException;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.services.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskRestController {

	@Autowired
	private TaskService taskService;
	
	@GetMapping(value = "/project/{projectId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Task> allProjectTasks(@PathVariable Long projectId) throws NonExistingProjectException {
		return taskService.getAllProjectTasks(projectId);
	}
	
	@DeleteMapping(value = "/{taskId}")
	public void deleteProjectTask(@PathVariable Long taskId) throws NonExistingTaskException {
		taskService.deleteProjectTaskById(taskId);
	}
	
	@PostMapping(value = "/{taskId}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Task updateTask(@RequestBody TaskDTO taskDTO, @PathVariable Long taskId) throws NonExistingTaskException, EmptyMandatoryInputException {
		Task task = taskService.getTaskById(taskId);
		String description = taskDTO.getDescription();
		String completed = taskDTO.getCompleted();
		if(description.isEmpty()) {
			throw new EmptyMandatoryInputException("description");
		}
		return taskService.updateTask(task, description, completed.equals("1"));
	}

}
