package org.codemucker.csv;


public class EndOfStreamException extends CsvException {

	private static final long serialVersionUID = 1L;

	public EndOfStreamException() {
		super();
	}

	public EndOfStreamException(String message, Throwable cause) {
		super(message, cause);
	}

	public EndOfStreamException(String message) {
		super(message);
	}

	public EndOfStreamException(Throwable cause) {
		super(cause);
	}

}
