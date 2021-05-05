package it.unifi.projectplanner.exceptions;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class NonExistingTaskException extends Exception {

	private static final long serialVersionUID = 1L;

	public NonExistingTaskException(Long id) {
		super("The task with id=" + id + " does not exist");
	}
}