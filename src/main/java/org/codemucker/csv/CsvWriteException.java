package org.codemucker.csv;


public class CsvWriteException extends CsvException {

	private static final long serialVersionUID = 1L;

	public CsvWriteException() {
		super();
	}

	public CsvWriteException(String message, Throwable cause) {
		super(message, cause);
	}

	public CsvWriteException(String message) {
		super(message);
	}

	public CsvWriteException(Throwable cause) {
		super(cause);
	}

}
