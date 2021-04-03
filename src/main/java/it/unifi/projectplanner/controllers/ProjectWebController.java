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
	private static final String ERROR = "error";
	private static final String MESSAGE = "message";
	@Autowired
	private ProjectService projectService;

	@GetMapping("/")
	public String index(Model model) {
		List<Project> allProjects = projectService.getAllProjects();
		model.addAttribute("projects", allProjects);
		model.addAttribute(MESSAGE, allProjects.isEmpty() ? "No projects" : "");
		return INDEX;
	}

	@PostMapping("/save")
	public String saveProject(@ModelAttribute("name") ProjectDTO projectDTO, Model model) {
		try {
			projectService.insertNewProject(new Project(projectDTO.getName(), new ArrayList<>()));
		} catch (ConflictingProjectNameException e) {
			model.addAttribute(ERROR, e.getMessage());
			return INDEX;
		}
		return REDIRECT;
	}

}
