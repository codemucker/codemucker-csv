package org.codemucker.csv;

import java.util.Date;

import org.joda.time.DateTime;

public interface ICsvWriter {

	/**
	 * Write a comment for the current record.
	 * 
	 * @throws CsvWriteException
	 */
	ICsvWriter writeRecordComment(String s) throws CsvWriteException;

	ICsvWriter write(Object obj) throws CsvWriteException;

	ICsvWriter write(char c) throws CsvWriteException;

	ICsvWriter write(boolean b) throws CsvWriteException;

	ICsvWriter write(int i) throws CsvWriteException;

	ICsvWriter write(float f) throws CsvWriteException;

	ICsvWriter write(double d) throws CsvWriteException;

	ICsvWriter write(byte b) throws CsvWriteException;

	ICsvWriter write(byte[] b) throws CsvWriteException;

	ICsvWriter write(long l) throws CsvWriteException;

	ICsvWriter write(String s) throws CsvWriteException;

	ICsvWriter writeNonEscaped(String s) throws CsvWriteException;

	ICsvWriter write(Boolean b) throws CsvWriteException;

	ICsvWriter write(Float f) throws CsvWriteException;

	ICsvWriter write(Long l) throws CsvWriteException;

	ICsvWriter write(Byte b) throws CsvWriteException;

	ICsvWriter write(Integer i) throws CsvWriteException;

	ICsvWriter write(Double d) throws CsvWriteException;

	ICsvWriter write(Character c) throws CsvWriteException;

	ICsvWriter write(DateTime d) throws CsvWriteException;

	ICsvWriter write(Date d) throws CsvWriteException;

	Appendable getAppender();

	void lock();
	void unlock();

	/**
	 * Start writing the next record
	 * 
	 * @throws CsvWriteException
	 */
	void beginRecord() throws CsvWriteException;
	void endRecord() throws CsvWriteException;
	void flush() throws CsvWriteException;

	ICsvWriter getEmbeddedWriter();

}
