package it.unifi.projectplanner.exceptions;

public class ConflictingProjectNameException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConflictingProjectNameException(String projectName) {
		super("The name '" + projectName + "' is already used for another project");
	}

}