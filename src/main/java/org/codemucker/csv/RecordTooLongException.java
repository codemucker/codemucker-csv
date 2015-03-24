package org.codemucker.csv;


public class RecordTooLongException extends CsvException {

	private static final long serialVersionUID = 1L;

	public RecordTooLongException(String message, Throwable cause) {
		super(message, cause);
	}

	public RecordTooLongException(String message) {
		super(message);
	}

	public RecordTooLongException(Throwable cause) {
		super(cause);
	}

}
