package it.unifi.projectplanner.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class EmptyMandatoryInputException extends Exception {

	private static final long serialVersionUID = 1L;

	public EmptyMandatoryInputException(String fieldName) {
		super("Invalid input: mandatory field '" + fieldName + "' was empty.");
	}
}
