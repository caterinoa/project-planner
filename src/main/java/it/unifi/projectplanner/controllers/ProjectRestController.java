package it.unifi.projectplanner.controllers;

import java.util.ArrayList;
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

import it.unifi.projectplanner.dto.ProjectDTO;
import it.unifi.projectplanner.dto.TaskDTO;
import it.unifi.projectplanner.exceptions.ConflictingProjectNameException;
import it.unifi.projectplanner.exceptions.EmptyMandatoryInputException;
import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.model.Task;
import it.unifi.projectplanner.services.ProjectService;

@RestController
@RequestMapping("/api/projects")
public class ProjectRestController {

	@Autowired
	private ProjectService projectService;
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody List<Project> allProjects() {
		return projectService.getAllProjects();
	}
	
	@PostMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Project newProject(@RequestBody ProjectDTO projectDTO) throws ConflictingProjectNameException, EmptyMandatoryInputException {
		String name = projectDTO.getName();
		if(name.isEmpty()) {
			throw new EmptyMandatoryInputException("name");
		}
		Project project = new Project(name, new ArrayList<>());
		return projectService.insertNewProject(project);
	}
	
	@PostMapping(value = "/{projectId}/newtask", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody Project newProjectTask(@RequestBody TaskDTO taskDTO, @PathVariable Long projectId) throws NonExistingProjectException, EmptyMandatoryInputException {
		String description = taskDTO.getDescription();
		if(description.isEmpty()) {
			throw new EmptyMandatoryInputException("description");
		}
		Project project = projectService.getProjectById(projectId);
		return projectService.insertNewTaskIntoProject(new Task(description, project), project);
	}
	
	@DeleteMapping(value = "/{id}")
	public void deleteProject(@PathVariable Long id) throws NonExistingProjectException {
		projectService.deleteProjectById(id);
	}
	
	@DeleteMapping(value = "/deleteall")
	public void deleteAll() {
		projectService.deleteAllProjects();
	}
}
