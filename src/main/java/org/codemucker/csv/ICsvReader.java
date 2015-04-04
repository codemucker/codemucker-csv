package org.codemucker.csv;

public interface ICsvReader {

	ICsvRecord readNextRecord() throws CsvException;

	ICsvRecord readNextRecord(int skipLines, int skipRecords)
			throws CsvException;

	ICsvRecord readNextRecord(int skipNumLines) throws CsvException;

	boolean hasMore();

}
