package it.unifi.projectplanner.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import it.unifi.projectplanner.dto.ProjectDTO;
import it.unifi.projectplanner.exceptions.ConflictingProjectNameException;
import it.unifi.projectplanner.exceptions.NonExistingProjectException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.services.ProjectService;

@RestController
@RequestMapping("/api/projects")
public class ProjectRestController {

	@Autowired
	private ProjectService projectService;
	
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Project> allProjects() {
		return projectService.getAllProjects();
	}
	
	@PostMapping(value = "/new", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ExceptionHandler(ConflictingProjectNameException.class)
	public @ResponseBody Project newProject(@RequestBody ProjectDTO projectDTO) throws ConflictingProjectNameException {
		Project project = new Project(projectDTO.getName(), new ArrayList<>());
		return projectService.insertNewProject(project);
	}
	
	@DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ExceptionHandler(NonExistingProjectException.class)
	public @ResponseBody Project deleteProject(@PathVariable long id) throws NonExistingProjectException {
		return projectService.deleteProjectById(id);
	}
	
	@DeleteMapping(value = "/deleteall")
	public void deleteAll() {
		projectService.deleteAllProjects();
	}
}
