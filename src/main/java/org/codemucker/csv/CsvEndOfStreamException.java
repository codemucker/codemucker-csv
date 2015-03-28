package org.codemucker.csv;


public class CsvEndOfStreamException extends CsvException {

	private static final long serialVersionUID = 1L;

	public CsvEndOfStreamException() {
		super();
	}

	public CsvEndOfStreamException(String message, Throwable cause) {
		super(message, cause);
	}

	public CsvEndOfStreamException(String message) {
		super(message);
	}

	public CsvEndOfStreamException(Throwable cause) {
		super(cause);
	}

}
