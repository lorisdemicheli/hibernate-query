package io.github.lorisdemicheli.hibernate_query.exception;

public class FunctionException extends RuntimeException {

	private static final long serialVersionUID = -3558586457630753513L;

	public FunctionException() {
		super();
	}

	public FunctionException(String message) {
		super(message);
	}

	public FunctionException(String message, Throwable cause) {
		super(message, cause);
	}

	public FunctionException(Throwable cause) {
		super(cause);
	}

}
