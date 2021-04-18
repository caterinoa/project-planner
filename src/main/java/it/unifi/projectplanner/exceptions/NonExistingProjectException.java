package it.unifi.projectplanner.exceptions;

public class NonExistingProjectException extends Exception {

	private static final long serialVersionUID = 1L;

	public NonExistingProjectException() {
		super("The specified project does not exist");
	}
}
