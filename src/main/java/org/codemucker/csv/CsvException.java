package org.codemucker.csv;

import java.io.IOException;

public class CsvException extends IOException {

	private static final long serialVersionUID = 1L;

	public CsvException() {
		super();
	}

	public CsvException(String message, Throwable cause) {
		super(message, cause);
	}

	public CsvException(String message) {
		super(message);
	}

	public CsvException(Throwable cause) {
		super(cause);
	}

}
