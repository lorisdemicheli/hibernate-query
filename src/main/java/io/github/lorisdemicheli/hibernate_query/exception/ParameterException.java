package io.github.lorisdemicheli.hibernate_query.exception;

public class ParameterException extends RuntimeException {

	private static final long serialVersionUID = -3558586457630753513L;

	public ParameterException() {
		super();
	}

	public ParameterException(String message) {
		super(message);
	}

	public ParameterException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParameterException(Throwable cause) {
		super(cause);
	}

}
