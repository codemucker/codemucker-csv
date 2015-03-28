package org.codemucker.csv;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.codemucker.lang.IBuilder;
import org.codemucker.lang.annotation.NotThreadSafe;

import com.google.common.base.Preconditions;

/**
 * Read Csv records. Use:
 * 
 * <pre>
 * CsvReader.with().defaults().input(...).build();
 * 
 * </pre>
 * @author bert
 *
 */
@NotThreadSafe
public class CsvReader implements Closeable {

	private static final String[] EMPTY_ARRAY = new String[] {};
	private static final char NL = '\n';
	
	private final char[] charBuf = new char[1];//avoid constant reallocation
	
	/**
	 * Indication of the maximum number of fields per record
	 */
	private final int fieldBufSize;
	
	/**
	 * Indication of the maximum number of chars any field will be
	 */
	private final int fieldValueBufSize;
	
	/**
	 * If the field value buffer exceeds this value, trim it
	 */
	private final int fieldValueBufMaxSize;
	/**
	 * If the field buffer exceeds this value, trim it
	 */
	private final int fieldBufMaxSize;
	
	/**
	 * Largest number of chars any single record can contain. Used to protect OOM eerors due to uploading very large csv files
	 */
	private final int maxNumberOfCharsPerRecord;
	
	//reuse these structures so we don't need to reallocate on each record
	private final ArrayList<String> fieldBuf;
	private final StringBuilder fieldValueBuf;
	
	private final char fieldSepChar;//= ',';
	private final char escapeChar;// = '"';
	private final char commentChar;// = '#';
	private boolean commentsEnabled;// = true;
	
	private final Reader reader;
	
	private final boolean closeReader;

	private int recordNumber = 0;
	
	private CsvReader(Reader reader, char fieldSepChar, char escapeChar,
			char commentChar, boolean commentsEnabled,
			int fieldBufSize, int fieldBufMaxSize,
			int fieldCharCount, int fieldValueBufMaxSize,
			int maxNumberOfCharsPerRecord,boolean closeReader) {
		this.reader = reader;
		this.fieldSepChar = fieldSepChar;
		this.escapeChar = escapeChar;
		this.commentChar = commentChar;
		this.commentsEnabled = commentsEnabled;
		
		this.fieldBufSize = fieldBufSize;
		this.fieldBufMaxSize = fieldBufMaxSize;
		
		this.fieldValueBufSize = fieldCharCount;
		this.fieldValueBufMaxSize = fieldValueBufMaxSize;
		
		this.maxNumberOfCharsPerRecord = maxNumberOfCharsPerRecord;
		
		this.fieldBuf = new ArrayList<>(fieldBufSize);
		this.fieldValueBuf = new StringBuilder(fieldCharCount);
		
		this.closeReader = closeReader;
	}

	public String[] readNextRecord() throws  CsvException {
		return readNextRecord(0);
	}	
	
	public String[] readNextRecord(int skipLines,int skipRecords) throws  CsvException {
		String[] record = readNextRecord(skipLines);
		for(int  i = 0; i < skipRecords;i++){
			record = readNextRecord(0);
		}
		return record;	
	}
	
	/**
	 * Blocking read for the next record
	 * 
	 * @return
	 * @throws CsvException
	 */
	public String[] readNextRecord(int skipNumLines) throws  CsvException {
		//we use existing buffer structures so we don't need to reallocate each time
		
		final int RESET_PREV = 0;
		boolean eof = false;
		boolean inEscape = false;
		boolean possibleEndOfField = false;
		char previousChar = RESET_PREV;
		
		try {
			if(skipNumLines > 0){
				for(int lineNum = 0;lineNum < skipNumLines;lineNum++){
					readToEndOfLine();
				}
			}
			for(int numCharsRead = 0;!eof;numCharsRead++) {
				if(numCharsRead > maxNumberOfCharsPerRecord){
					throw new CvsRecordTooLongException("Exceeded " + maxNumberOfCharsPerRecord + " characters");
				}
				int numBytesRead = reader.read(charBuf);
				if (numBytesRead == -1) {//end of stream
					if(inEscape && !possibleEndOfField){
						//expect more chars
						throw new CsvEndOfStreamException("End of stream, expecting more characters. Read " + numCharsRead + " characters for record");
					}
					endField();
					nextRecord();
					eof = true;
					break;
				}	
				char c = charBuf[0];
				if (inEscape) {
					if(c == fieldSepChar){
						if(previousChar == escapeChar){
							endField();
							c = RESET_PREV;
							inEscape = false;
							possibleEndOfField = false;
						} else {
							appendToField(c);
						}
					} else if (c == escapeChar) {
						//possibly end escape, unless next char is also quote
						if(previousChar == escapeChar){
							appendToField(escapeChar);
							c = RESET_PREV;//ignore the previous double quotes if a third comes along
							possibleEndOfField = false;
						} else {
							possibleEndOfField = true;
						}
					} else {
						appendToField(c);
					}
				} else { //normal mode
					if(commentsEnabled && c == commentChar && fieldValueBuf.length() == 0){//comment char at start of line only
						//skip line
						readToEndOfLine();	
					} else if (c == fieldSepChar) {
						endField();
						// reset all
						possibleEndOfField = false;
						c = RESET_PREV;
					} else if (c == '\r') {
						// ignore/drop;
					} else if (c == NL) {
						endField();
						nextRecord();
						possibleEndOfField = false;
						eof = true;
					} else if (c == escapeChar) {
						inEscape = true;
					} else {
						appendToField(c);
					}
				}
				previousChar = c;
			}

			String[] record = fieldBuf.toArray(EMPTY_ARRAY);
			record = onRecord(recordNumber,record);
			return record;
		} catch (IOException e) {
			throw new CsvException("Error reading csv input",e);
		} finally {
			resetBuffers();	
		}
	}
	
	private void nextRecord(){
		recordNumber++;
	}

	private void endField(){
		String fieldVal = fieldValueBuf.toString();
		fieldVal = onField(recordNumber,fieldBuf.size(), fieldVal);
		fieldBuf.add(fieldVal);
		fieldValueBuf.setLength(0);
	}

	private void appendToField(char c){
		fieldValueBuf.append(c);
	};

	private String onField(int recordNumber, int fieldNumber, String fieldVal){
		//TODO:user callbacks
		return fieldVal;
	}

	private String[] onRecord(int recordNumber, String[] record){
		//TODO:user callbacks
		return record;
	}

	private void resetBuffers() {
		if(fieldBuf.size() > fieldBufMaxSize){
			fieldBuf.clear();
			fieldBuf.trimToSize();
			fieldBuf.ensureCapacity(fieldBufSize);
		} else {
			fieldBuf.clear();	
		}
		if(fieldValueBuf.length() > fieldValueBufMaxSize){
			fieldValueBuf.setLength(fieldValueBufSize);
			fieldValueBuf.trimToSize();
		}
		fieldValueBuf.setLength(0);
	}

	private void readToEndOfLine() throws IOException{
		while(reader.read(charBuf) != -1){
			if(charBuf[0] == NL){
				return;
			}	
		}
	}
	@Override
	public void close() throws IOException {
		fieldBuf.clear();
		fieldValueBuf.setLength(0);
		fieldValueBuf.trimToSize();
		if(closeReader){
			reader.close();
		}
	}
	
	/**
	 * Usage:
	 * 
	 * <pre>
	 * with().defaults().read(...).build();
	 * 
	 * </pre>
	 * @return
	 */
	public static Builder with(){
		return new Builder();
	}
	
	public static class Builder implements IBuilder<CsvReader> {
		private int fieldBufSize;
		private int fieldValueBufSize;
		private int fieldValueBufMaxSize;
		private int fieldBufMaxSize;

		private int maxNumberOfCharsPerRecord;
		private char fieldSepChar;
		private char escapeChar;
		private char commentChar;
		private boolean commentsEnabled;
		private boolean closeReader;
		// char is 2 bytes, so memory size ~= X * 2 chars
		private static final int SIZE_1_MEG = (1 * 1000 * 1000) / 2;
		private static final int SIZE_5_MEG = SIZE_1_MEG * 5;
		private static final int SIZE_10_MEG = SIZE_1_MEG * 10;

		private Reader reader;

		public Builder() {
			defaults();
		}

		public CsvReader build() {
			Preconditions.checkNotNull(reader,
					"expect reader, string or input stream");

			return new CsvReader(reader, fieldSepChar, escapeChar, commentChar,
					commentsEnabled, fieldBufSize, fieldBufMaxSize,
					fieldValueBufSize, fieldValueBufMaxSize,
					maxNumberOfCharsPerRecord,closeReader);
		}

		public Builder defaults() {
			fieldBufSize = 15;
			fieldBufMaxSize = fieldBufSize * 3;

			fieldValueBufSize = 100;
			fieldValueBufMaxSize = fieldValueBufSize * 5;

			fieldSepChar = ',';
			escapeChar = '"';
			commentChar = '#';
			commentsEnabled = true;

			maxNumberOfCharsPerRecord = SIZE_10_MEG;

			closeReader = true;
			return this;
		}

		public Builder input(String s) {
			input(new StringReader(s));
			return this;
		}

		public Builder input(InputStream is) {
			input(new InputStreamReader(is));
			return this;
		}

		public Builder inputBuffered(Reader r) {
			input(new BufferedReader(r));
			return this;
		}

		public Builder input(Reader r) {
			this.reader = r;
			return this;
		}

		public Builder fieldBufSize(int fieldBufSize) {
			this.fieldBufSize = fieldBufSize;
			return this;
		}

		public Builder fieldValueBufSize(int fieldValueBufSize) {
			this.fieldValueBufSize = fieldValueBufSize;
			return this;
		}

		public Builder fieldValueBufMaxSize(int fieldValueBufMaxSize) {
			this.fieldValueBufMaxSize = fieldValueBufMaxSize;
			return this;
		}

		public Builder fieldBufMaxSize(int fieldBufMaxSize) {
			this.fieldBufMaxSize = fieldBufMaxSize;
			return this;
		}

		public Builder maxNumberOfCharsPerRecord(
				int maxNumberOfCharsPerRecord) {
			this.maxNumberOfCharsPerRecord = maxNumberOfCharsPerRecord;
			return this;
		}

		public Builder fieldSeparator(char fieldSepChar) {
			this.fieldSepChar = fieldSepChar;
			return this;
		}

		public Builder escapChar(char escapeChar) {
			this.escapeChar = escapeChar;
			return this;
		}

		public Builder commentChar(char commentChar) {
			this.commentChar = commentChar;
			return this;
		}

		public Builder commentsEnabled(boolean commentsEnabled) {
			this.commentsEnabled = commentsEnabled;
			return this;
		}

	}
}
