package org.codemucker.csv;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import org.codemucker.lang.annotation.NotThreadSafe;

@NotThreadSafe
public class CsvReader implements Closeable {

	private final Reader reader;
	private static final String[] EMPTY = new String[] {};

	private final int recordNumFields  = 15;//higher end number of record fields
	private final int recordFieldLength = 30;//higher end of field length
	
	//reuse these structures so we don't need to reallocate on each record
	private final ArrayList<String> recordBuf = new ArrayList<>(recordNumFields);
	private final StringBuilder fieldBuf = new StringBuilder(recordFieldLength);
	private final int maxRecordSizeChars = (10 * 1000 * 1000 ) / 2;//char is 2 bytes, so memory size ~= X * 2 chars
	
	private char[] buf = new char[1];//avoid constant reallocation
	
	public CsvReader(String s) {
		this(new StringReader(s));
	}

	public CsvReader(InputStream is) {
		this(new InputStreamReader(is));
	}

	public CsvReader(Reader reader) {
		this.reader = reader;
	}

	public String[] readNext() throws  CsvException {
		checkReady();
		
		//we use existing structures so we don't need to reallocate each time
		//recordBuf.clear();
		//fieldBuf.setLength(0);
		//ArrayList<String> recordBuf = new ArrayList<>(recordNumFields);
		//StringBuilder fieldBuf = new StringBuilder(recordFieldLength);
		
		final int RESET_PREV = 0;
		
		boolean endRecord = false;
		boolean inEscape = false;
		boolean possibleEndOfField = false;
		char previousChar = RESET_PREV;
		
		for(int numChars = 0;!endRecord;numChars++) {
			if(numChars > maxRecordSizeChars){
				throw new RecordTooLongException("Exceeded " + maxRecordSizeChars + " characters");
			}
			try {
				int bytesRead = reader.read(buf);
				if (bytesRead == -1) {//end of stream
					if(inEscape && !possibleEndOfField){
						//expect more chars
						throw new EndOfStreamException("End of stream, expecting more characters. Read " + numChars + " characters for record");
					}
					recordBuf.add(fieldBuf.toString());
					fieldBuf.setLength(0);
					endRecord = true;
					break;
				}	
			} catch (IOException e) {
				throw new CsvException("Error reading stream",e);
			}
			
			char c = buf[0];
			if (inEscape) {
				if( c == ','){
					if(previousChar == '"'){
						recordBuf.add(fieldBuf.toString());
						fieldBuf.setLength(0);
						c = RESET_PREV;
						inEscape = false;
						possibleEndOfField = false;
					} else {
						fieldBuf.append(c);
					}
				} else if (c == '"') {
					//possibly end escape, unless next char is also quote
					if(previousChar == '"'){
						fieldBuf.append('"');
						c = RESET_PREV;//ignore the previous double quotes if a third comes along
						possibleEndOfField = false;
					} else {
						possibleEndOfField = true;
					}
				} else {
					fieldBuf.append(c);
				}
			} else { //normal mode
				if (c == ',') {
					// next record
					recordBuf.add(fieldBuf.toString());
					fieldBuf.setLength(0);
					// reset all
					possibleEndOfField = false;
					inEscape = false;
					c = RESET_PREV;
				} else if (c == '\r') {
					// ignore;
				} else if (c == '\n') {
					recordBuf.add(fieldBuf.toString());
					fieldBuf.setLength(0);
					possibleEndOfField = false;
					endRecord = true;
				} else if (c == '"') {
					inEscape = true;
				} else {
					fieldBuf.append(c);
				}
			}
			previousChar = c;
		}
//	
		String[] result = recordBuf.toArray(EMPTY);
		recordBuf.clear();
		fieldBuf.setLength(0);	
		return result;
		
		//return recordBuf.toArray(EMPTY);
	}

	private void checkReady()throws CsvException {
		boolean canRead = false;
		try {
			canRead = reader.ready();
		}catch (IOException e) {
			throw new CsvException("can't read stream",e);
		}
		if(!canRead){
			throw new EndOfStreamException("can't read stream");
		}
	}
	
	@Override
	public void close() throws IOException {
		recordBuf.clear();
		fieldBuf.setLength(0);
		fieldBuf.trimToSize();
		reader.close();
		
	}
}
