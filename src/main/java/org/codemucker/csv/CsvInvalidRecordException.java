package org.codemucker.csv;

public class CsvInvalidRecordException extends CsvException {

	private static final long serialVersionUID = 1L;

	public CsvInvalidRecordException(String message, Throwable cause) {
		super(message, cause);
	}

	public CsvInvalidRecordException(String message) {
		super(message);
	}

	public CsvInvalidRecordException(Throwable cause) {
		super(cause);
	}

}
