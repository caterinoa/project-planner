package it.unifi.projectplanner.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class NonExistingProjectException extends Exception {

	private static final long serialVersionUID = 1L;

	public NonExistingProjectException(Long id) {
		super("The project with id=" + id + " does not exist");
	}

	public NonExistingProjectException(String name) {
		super("The project with name = " + name + " does not exist");
	}
}
