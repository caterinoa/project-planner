package it.unifi.projectplanner.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import it.unifi.projectplanner.dto.ProjectDTO;
import it.unifi.projectplanner.exceptions.ConflictingProjectNameException;
import it.unifi.projectplanner.model.Project;
import it.unifi.projectplanner.services.ProjectService;

@Controller
public class ProjectWebController {

	private static final String INDEX = "index";
	private static final String REDIRECT = "redirect:/";
	private static final String PROJECTS = "projects";
	private static final String ERROR = "error";
	private static final String MESSAGE = "message";

	@Autowired
	private ProjectService projectService;

	@GetMapping("/")
	public String index(Model model) {
		List<Project> allProjects = projectService.getAllProjects();
		model.addAttribute(PROJECTS, allProjects);
		model.addAttribute(MESSAGE, allProjects.isEmpty() ? "No projects" : "");
		return INDEX;
	}

	@PostMapping("/save")
	public String saveProject(@ModelAttribute("name") ProjectDTO projectDTO, Model model) {
		String name = projectDTO.getName();
		String page = INDEX;
		if (name == null) {
			model.addAttribute(ERROR, "The project name should not be empty");
			model.addAttribute(PROJECTS, projectService.getAllProjects());
		} else {
			try {
				projectService.insertNewProject(new Project(name, new ArrayList<>()));
				page = REDIRECT;
			} catch (ConflictingProjectNameException e) {
				model.addAttribute(ERROR, e.getMessage());
				model.addAttribute(PROJECTS, projectService.getAllProjects());
			}
		}
		return page;
	}
}
