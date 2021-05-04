package it.unifi.projectplanner.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class ConflictingProjectNameException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConflictingProjectNameException(String projectName) {
		super("The name '" + projectName + "' is already used for another project");
	}

}