package org.codemucker.csv;


/**
 * Thrown to indicate that a csv record was too long to be read. This is used to
 * protect a server process from running out of memory when reading a record
 *
 */
public class CvsRecordTooLongException extends CsvException {

	private static final long serialVersionUID = 1L;

	public CvsRecordTooLongException(String message, Throwable cause) {
		super(message, cause);
	}

	public CvsRecordTooLongException(String message) {
		super(message);
	}

	public CvsRecordTooLongException(Throwable cause) {
		super(cause);
	}

}
