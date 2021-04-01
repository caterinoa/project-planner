package it.unifi.projectplanner.exceptions;

public class ConflictingProjectNameException extends Exception {

	private static final long serialVersionUID = 1L;

	public ConflictingProjectNameException() {
		super("The specified name is already used for another project");
	}

}