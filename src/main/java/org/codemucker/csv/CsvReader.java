package org.codemucker.csv;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.codemucker.csv.encode.DefaultSerialiserProvider;
import org.codemucker.lang.IBuilder;
import org.codemucker.lang.annotation.NotThreadSafe;
import org.codemucker.lang.annotation.ThreadSafe;

import com.google.common.base.Preconditions;

/**
 * Read Csv records. Use:
 * 
 * <pre>
 * CsvReader r = CsvReader.with().input(...).threadsafe(false).build();
 * 
 * ICsvRecord rec = r.readNextRecord();
 * ...
 * rec = r.readNextRecord();
 * </pre>
 */
@ThreadSafe(caveats="only if threadSafe true has been set")
public class CsvReader implements Closeable,ICsvReader {

	private static final String[] EMPTY_ARRAY = new String[] {};
	
	private static final boolean EMPTY_TO_NULL = true;
	private static final boolean EMPTY_IF_BLANK = false;
	
	private static final char NL = '\n';

	private final char[] charBuf = new char[1];// avoid constant reallocation

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
	 * Largest number of chars any single record can contain. Used to protect
	 * OOM eerors due to uploading very large csv files
	 */
	private final int maxNumberOfCharsPerRecord;

	// reuse these structures so we don't need to reallocate on each record
	private final ArrayList<String> fieldBuf;
	private final StringBuilder fieldValueBuf;

	private final char fieldSepChar;// = ',';
	private final char escapeChar;// = '"';
	private final char commentChar;// = '#';
	private boolean commentsEnabled;// = true;

	private final Reader reader;

	private final boolean closeReader;
	private final boolean reuseRecord;

	private int recordNumber = 0;
	
	private final Lock lock;
	
	private boolean hasMore = true;

	private int conseqNumEscapes= 0;
	boolean inEscape = false;
	
	/**
	 * Reuse a record (if enabled) to reduce object allocation
	 */
	private CsvRecord record;
	private Serialiser serialiser;

	private CsvReader(Reader reader, char fieldSepChar, char escapeChar,
			char commentChar, boolean commentsEnabled, int fieldBufSize,
			int fieldBufMaxSize, int fieldCharCount, int fieldValueBufMaxSize,
			int maxNumberOfCharsPerRecord, boolean closeReader, boolean threadSafe, boolean reuseRecord,Serialiser serialiser) {
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
		this.lock = threadSafe?new ReentrantLock():null;
		
		this.reuseRecord = reuseRecord;
		this.serialiser = serialiser;
		
		this.record = reuseRecord?new CsvRecord(serialiser):null;
	}

	@Override
	public ICsvRecord readNextRecord() throws CsvException {
		lock();
		try {
			return internalReadNextRecord(0);
		} finally {
			unlock();
		}
	}

	@Override
	public ICsvRecord readNextRecord(int skipLines, int skipRecords)
			throws CsvException {
		lock();
		try {
			ICsvRecord record = internalReadNextRecord(skipLines);
			for (int i = 0; i < skipRecords; i++) {
				record = internalReadNextRecord(0);
			}
			return record;
		} finally {
			unlock();
		}
	}

	@Override
	public ICsvRecord readNextRecord(int skipNumLines) throws CsvException {
		lock();
		try {	
			return internalReadNextRecord(skipNumLines);
		} finally {
			unlock();
		}
	}

	/**
	 * Blocking read for the next record
	 * 
	 * @return
	 * @throws CsvException
	 */
	private ICsvRecord internalReadNextRecord(int skipNumLines)
			throws CsvException {
		// we use existing buffer structures so we don't need to reallocate each
		// time
		try {
			if (skipNumLines > 0) {
				for (int lineNum = 0; lineNum < skipNumLines; lineNum++) {
					readToEndOfLine();
				}
			}
			for (int numCharsRead = 0; ; numCharsRead++) {
				next();
				if (numCharsRead > maxNumberOfCharsPerRecord) {
					throw new CvsRecordTooLongException("Exceeded "
							+ maxNumberOfCharsPerRecord + " characters");
				}
				char c = read();
				if(isEndStream()){
					if(inEscape){
						if(isEndEscape()){
							appendEscapes();
							endEscapedField();
						} else {
							throw new CsvEndOfStreamException(
									"End of stream, expecting more characters for escape sequence. Read "
											+ numCharsRead
											+ " characters for record");		
						}
					} else {
						endNonEscapedField();
					}
					break;
				}else if (inEscape) {
					if(c==escapeChar){
						conseqNumEscapes++;
					} else if (c == fieldSepChar) {
						if(isEndEscape()){
							appendEscapes();
							endEscapedField();
							inEscape = false;
						} else {
							appendEscapes();
							appendToField(c);
						}
						conseqNumEscapes = 0;
					} else {
						if(isEndEscape()){
							throw new CsvInvalidRecordException(
									"Unexpected character, invalid number of escape '" + escapeChar + "' characters. Read "
											+ numCharsRead
											+ " characters for record " + recordNumber);
						} else {
							appendEscapes();
							appendToField(c);
						}
						conseqNumEscapes = 0;
					}
				} else {
					if (commentsEnabled && c == commentChar
							&& fieldValueBuf.length() == 0) {// comment char at
																// start of line
																// only
						// skip line
						readToEndOfLine();
					} else if (c == fieldSepChar) {
						endNonEscapedField();
					} else if (c == '\r') {
						// ignore/drop;
					} else if (c == NL) {
						endNonEscapedField();
						nextRecord();
						break;
					} else if (c == escapeChar) {
						conseqNumEscapes++;
						inEscape = true;
					} else {
						appendToField(c);
					}
				}
			}

			String[] fields = fieldBuf.toArray(EMPTY_ARRAY);
			fields = onRecord(recordNumber, fields );
			return newRecord(fields);
		} catch (IOException e) {
			throw new CsvException("Error reading csv input", e);
		} finally {
			resetBuffers();
		}
	}

	private boolean next() throws IOException{
		if(hasMore){
			int i = reader.read(charBuf);
			if( i == -1){
				hasMore = false;
			}
		}
		return hasMore;
	}
	
	private char read(){
		return charBuf[0];
	}
	
	private boolean isEndStream(){
		return !hasMore;
	}
	
	private void readToEndOfLine() throws IOException {
		while (reader.read(charBuf) != -1) {
			if (charBuf[0] == NL) {
				return;
			}
		}
	}

	private boolean isEndEscape() {
		boolean even = isEvenEscapes();
		if(isStartOfField()){
			return even;// e.g. "", or """"
		} else {
			return !even;//  odd num. e.g.  "foo"  or "foo"""
		}
	}

	private boolean isEvenEscapes(){
		return conseqNumEscapes % 2 == 0;
	}

	private boolean isStartOfField(){
		return fieldValueBuf.length() ==0;
	}

	private void appendEscapes(){
		if(conseqNumEscapes ==0){
			return;
		}
		if(isStartOfField()){
			//only odd number escapes
			if(conseqNumEscapes > 2){
				appendEscapes((conseqNumEscapes-1)/2);
			}
		} else { //even num escapes
			appendEscapes(conseqNumEscapes/2);
		}
	}
	
	private void appendEscapes(int num){
		for(int i = 0 ; i < num;i++){
			appendToField(escapeChar);
		}
	}

	private void endEscapedField(){
		endField(EMPTY_IF_BLANK);
	}

	private void endNonEscapedField(){
		endField(EMPTY_TO_NULL);
	}
	
	private void endField(boolean emptyIsNull) {
		String fieldVal = fieldValueBuf.toString();
		if(fieldVal.length() ==0 && emptyIsNull){
			fieldVal = null;
		}
		fieldVal = onField(recordNumber, fieldBuf.size(), fieldVal);
		fieldBuf.add(fieldVal);
		fieldValueBuf.setLength(0);
	}

	private void appendToField(char c) {
		fieldValueBuf.append(c);
	};

	
	private ICsvRecord newRecord(String[] data){
		if(reuseRecord){
			//reuse record to avoid allocation
			record.setData(data);
			return record;
		}
		return new CsvRecord(serialiser, data);
	}

	private void nextRecord() {
		recordNumber++;
	}
	
	private String onField(int recordNumber, int fieldNumber, String fieldVal) {
		// TODO:user callbacks
		return fieldVal;
	}

	private String[] onRecord(int recordNumber, String[] record) {
		// TODO:user callbacks
		return record;
	}

	private void resetBuffers() {
		if (fieldBuf.size() > fieldBufMaxSize) {
			fieldBuf.clear();
			fieldBuf.trimToSize();
			fieldBuf.ensureCapacity(fieldBufSize);
		} else {
			fieldBuf.clear();
		}
		if (fieldValueBuf.length() > fieldValueBufMaxSize) {
			fieldValueBuf.setLength(fieldValueBufSize);
			fieldValueBuf.trimToSize();
		}
		fieldValueBuf.setLength(0);

		conseqNumEscapes = 0;
		inEscape = false;
	}

	@Override
	public void close() throws IOException {
		fieldBuf.clear();
		fieldValueBuf.setLength(0);
		fieldValueBuf.trimToSize();
		if (closeReader) {
			reader.close();
		}
	}

	private void lock() {

		if (lock != null) {
			lock.lock();
		}
	}

	private void unlock() {
		if (lock != null) {
			lock.unlock();
		}
	}

	/**
	 * Usage:
	 * 
	 * <pre>
	 * with().defaults().read(...).build();
	 * 
	 * </pre>
	 * 
	 * @return
	 */
	public static Builder with() {
		return new Builder();
	}

	@NotThreadSafe
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
		private boolean threadSafe;
		private boolean reuseRecord;
		private Serialiser serialiser;
		
		// char is 2 bytes, so memory size ~= X * 2 chars
		private static final int SIZE_1_MEG = (1 * 1000 * 1000) / 2;

		private Reader reader;

		public Builder() {
			defaults();
		}

		public CsvReader build() {
			Preconditions.checkNotNull(reader,
					"expect reader, string or input stream");

			Preconditions.checkArgument(!(threadSafe && reuseRecord),"can't reuse record if in threadsafe mode");
			Serialiser ser = serialiser==null?DefaultSerialiserProvider.getSerialiser():serialiser;
			
			return new CsvReader(reader, fieldSepChar, escapeChar, commentChar,
					commentsEnabled, fieldBufSize, fieldBufMaxSize,
					fieldValueBufSize, fieldValueBufMaxSize,
					maxNumberOfCharsPerRecord, closeReader, threadSafe, reuseRecord, ser);
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

			maxNumberOfCharsPerRecord = SIZE_1_MEG * 10;

			closeReader = true;
			threadSafe = true;
			reuseRecord = false;
			
			serialiser = DefaultSerialiserProvider.getSerialiser();
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

		public Builder fieldBufMaxSize(int i) {
			this.fieldBufMaxSize = i;
			return this;
		}

		public Builder maxRecordSizeInMegs(int megs) {
			maxRecordSizeInChars(megs*SIZE_1_MEG);
			return this;
		}
		
		public Builder maxRecordSizeInChars(int maxNumberOfCharsPerRecord) {
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

		/**
		 * If true then record reads are thread safe. That is multiple threads can attempt
		 * to read the next record at once. You will also need to set {@link CsvReader#reuseRecord()}
		 * to true.
		 * 
		 * Default is false
		 */
		public Builder threadSafe(boolean threadSafe) {
			this.threadSafe = threadSafe;
			return this;
		}
		
		/**
		 * If true then the returned record is reused when next record is loaded. Default is true.
		 */
		public Builder reuseRecord(boolean reuseRecord) {
			this.reuseRecord = reuseRecord;
			return this;
		}
		
		public Builder serialiser(Serialiser serialiser) {
			this.serialiser = serialiser;
			return this;
		}
	}

}
