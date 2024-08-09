package io.github.lorisdemicheli.hibernate_query.exception;

public class TransformException extends RuntimeException {

	private static final long serialVersionUID = -3558586457630753513L;

	public TransformException() {
		super();
	}

	public TransformException(String message) {
		super(message);
	}

	public TransformException(String message, Throwable cause) {
		super(message, cause);
	}

	public TransformException(Throwable cause) {
		super(cause);
	}

}