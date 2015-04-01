package org.codemucker.csv;

import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.codemucker.csv.encode.DefaultSerialiser;
import org.codemucker.lang.annotation.ThreadSafe;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;


/**
 * Write Csv records. Use:
 * 
 * <pre>
 * 	os = ...getOutputStream()
 * 	w = CsvWriter.with().output(os).threadsafe(false).build();
 * 
 * 	w.beginRecord();
 * 	w.write("abc");
 * 	w.write(1234);
 *  w.endRecord();
 * 
 * </pre>
 */
@ThreadSafe(caveats="only if threadSafe true has been set")
public class CsvWriter implements ICsvWriter {

	private static final String EMPTY_STRING = "\"\"";
	private static final char NL = '\n';
	private static final char DQUOTE = '"';

	private final char commentChar;
	private final char fieldSep;
	private final boolean quoteEmptyStrings;
	private final boolean flushable;

	private final Appendable appender;
	private final Serialiser serialiser;

	private int fieldNum = -1;
	private int recordNumber = 0;

	private final Lock lock;

	private CsvWriter(Appendable appender, Serialiser serialiser, char fieldSep,
			char commentChar, boolean threadsafeRecord, boolean quoteEmptyStrings) {
		this.serialiser = serialiser;
		this.appender = appender;
		this.fieldSep = fieldSep;
		this.commentChar = commentChar;
		this.quoteEmptyStrings = quoteEmptyStrings;
		this.flushable = (appender instanceof Flushable);
		this.lock = threadsafeRecord ? new ReentrantLock() : null;
	}

	public int getRecordNumber() {
		return recordNumber;
	}

	@Override
	public ICsvWriter write(byte[] bytes) throws CsvWriteException {
		nextField();
		if (bytes != null) {
			try {
				print(DQUOTE);
				serialiser.toString(bytes, appender);
				print(DQUOTE);
			} catch (IOException e) {
				throw wrap(e);
			}
		}
		return this;
	}

	@Override
	public ICsvWriter writeRecordComment(String s) throws CsvWriteException {
		if (fieldNum != -1) {
			throw new CsvWriteException(
					"within a record. Can't write comment. Needs to be written before a field is written for the current record");
		}
		println();
		print(commentChar);

		int start = 0;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (c == '\n') {
				print(s.substring(start, i));
				start = i + 1;
				println();
				print(commentChar);
			}
		}
		// if no newlines or content after last newline
		if (start != 0 && start < s.length()) {
			print(s.substring(start));
		}
		return this;
	}

	/**
	 * Write a string which is safe, this will not be csv escaped
	 * 
	 * @param s
	 * @return
	 */
	public ICsvWriter writeNonEscaped(String s) throws CsvWriteException {
		nextField();
		print(s);
		return this;
	}

	@Override
	public ICsvWriter write(String s) throws CsvWriteException {
		nextField();
		printEscaped(s);
		return this;
	}


	@Override
	public ICsvWriter write(Object obj) throws CsvWriteException {
		nextField();
		if (obj != null) {
			try {
				serialiser.toString(obj, appender);
			} catch (IOException e) {
				throw wrap(e);
			}
		}
		return this;
	}

	@Override
	public ICsvWriter write(DateTime d) throws CsvWriteException {
		nextField();
		if (d != null) {
			try {
				serialiser.toString(d, appender);
			} catch (IOException e) {
				throw wrap(e);
			}
		}
		return this;
	}

	@Override
	public ICsvWriter write(Date d) throws CsvWriteException {
		nextField();
		if (d != null) {
			try {
				serialiser.toString(d, appender);
			} catch (IOException e) {
				throw wrap(e);
			}
		}
		return this;
	}


	@Override
	public ICsvWriter write(Boolean b) throws CsvWriteException {
		nextField();
		if (b != null) {
			try {
				serialiser.toString(b.booleanValue(), appender);
			} catch (IOException e) {
				throw wrap(e);
			}
		}
		return this;
	}

	@Override
	public ICsvWriter write(boolean b) throws CsvWriteException {
		nextField();
		try {
			serialiser.toString(b, appender);
		} catch (IOException e) {
			throw wrap(e);
		}
		return this;
	}

	@Override
	public ICsvWriter write(Character c) throws CsvWriteException {
		nextField();
		if (c != null) {
			try {
				serialiser.toString(c.charValue(), appender);
			} catch (IOException e) {
				throw wrap(e);
			}
		}
		return this;
	}

	@Override
	public ICsvWriter write(char c) throws CsvWriteException {
		nextField();
		try {
			serialiser.toString(c, appender);
		} catch (IOException e) {
			throw wrap(e);
		}
		return this;
	}

	@Override
	public ICsvWriter write(Float f) throws CsvWriteException {
		nextField();
		if (f != null) {
			try {
				serialiser.toString(f.floatValue(), appender);
			} catch (IOException e) {
				throw wrap(e);
			}
		}
		return this;
	}

	@Override
	public ICsvWriter write(float f) throws CsvWriteException {
		nextField();
		try {
			serialiser.toString(f, appender);
		} catch (IOException e) {
			throw wrap(e);
		}
		return this;
	}

	@Override
	public ICsvWriter write(Long l) throws CsvWriteException {
		nextField();
		if (l != null) {
			try {
				serialiser.toString(l.longValue(), appender);
			} catch (IOException e) {
				throw wrap(e);
			}
		}
		return this;
	}

	@Override
	public ICsvWriter write(long l) throws CsvWriteException {
		nextField();
		try {
			serialiser.toString(l, appender);
		} catch (IOException e) {
			throw wrap(e);
		}
		return this;
	}

	@Override
	public ICsvWriter write(Byte b) throws CsvWriteException {
		nextField();
		if (b != null) {
			try {
				serialiser.toString(b, appender);
			} catch (IOException e) {
				throw wrap(e);
			}
		}
		return this;
	}

	@Override
	public ICsvWriter write(byte b) throws CsvWriteException {
		nextField();
		try {
			serialiser.toString(b, appender);
		} catch (IOException e) {
			throw wrap(e);
		}
		return this;
	}

	@Override
	public ICsvWriter write(Integer i) throws CsvWriteException {
		nextField();
		if (i != null) {
			try {
				serialiser.toString(i.intValue(), appender);
			} catch (IOException e) {
				throw wrap(e);
			}
		}
		return this;
	}

	@Override
	public ICsvWriter write(int i) throws CsvWriteException {
		nextField();
		try {
			serialiser.toString(i, appender);
		} catch (IOException e) {
			throw wrap(e);
		}

		return this;
	}

	@Override
	public ICsvWriter write(Double d) throws CsvWriteException {
		nextField();
		if (d != null) {
			try {
				serialiser.toString(d.doubleValue(), appender);
			} catch (IOException e) {
				throw wrap(e);
			}
		}
		return this;
	}

	@Override
	public ICsvWriter write(double d) throws CsvWriteException {
		nextField();
		try {
			serialiser.toString(d, appender);
		} catch (IOException e) {
			throw wrap(e);
		}

		return this;
	}

	protected void nextField() throws CsvWriteException {
		fieldNum++;
		if (fieldNum > 0) {
			print(',');
		}
	}

	@Override
	public void beginRecord() throws CsvWriteException {
		if (lock != null) {
			lock.lock();
		}
		fieldNum = -1;
		recordNumber++;
	}

	@Override
	public void endRecord() throws CsvWriteException {
		if (lock != null) {
			lock.lock();
		}
		fieldNum = -1;
		println();
		if (lock != null) {
			lock.unlock();
		}
	}

	public void printEscaped(String s) throws CsvWriteException {
		try {
			if (s == null) {
				// do nothing
			} else if (s.length() == 0) {
				print(EMPTY_STRING);
			} else {
				boolean doEscape = false;
				for (int i = 0; i < s.length(); i++) {
					char c = s.charAt(i);
					if (c == '"' || c == fieldSep || c == NL) {
						doEscape = true;
						break;
					}
				}
				if (doEscape) {
					appender.append('"');
					for (int i = 0; i < s.length(); i++) {
						char c = s.charAt(i);
						if (c == '"') {
							appender.append('"').append('"');
						} else {
							appender.append(c);
						}
					}
					appender.append('"');

				} else {
					appender.append(s);
				}
			}
		} catch (IOException e) {
			throw wrap(e);
		}
		return;
	}

	private void print(String s) throws CsvWriteException {
		try {
			if (s == null) {
				// print nothing
			} else if (s.length() == 0) {
				if (quoteEmptyStrings) {
					appender.append(EMPTY_STRING);
				}
			} else {
				appender.append(s);
			}
		} catch (IOException e) {
			throw wrap(e);
		}
	}

	private void print(char c) throws CsvWriteException {
		try {
			appender.append(c);
		} catch (IOException e) {
			throw wrap(e);
		}
	}

	private void println() throws CsvWriteException {
		try {
			appender.append(NL);
		} catch (IOException e) {
			throw wrap(e);
		}
	}

	@Override
	public void flush() throws CsvWriteException {
		if (flushable) {
			try {
				((Flushable) appender).flush();
			} catch (IOException e) {
				throw wrap(e);
			}
		}
	}

	private CsvWriteException wrap(IOException e) {
		return new CsvWriteException(
				"stream threw an error while writing record", e);
	}

	@Override
	public Appendable getAppender() {
		return appender;
	}

	public static Builder with() {
		return new Builder();
	}

	public static class Builder {
		private char commentChar = '#';
		private char fieldSep = ',';
		private boolean threadsafe = false;
		private boolean quoteEmptyStrings = true;
		
		private Serialiser serialiser;
		private Appendable appender;

		public ICsvWriter build() {
			Preconditions.checkNotNull(appender, "expect output");
			Serialiser ser = serialiser == null ? DefaultSerialiser
					.get() : serialiser;
			return new CsvWriter(appender, ser, fieldSep, commentChar, threadsafe, quoteEmptyStrings);
		}

		public Builder defaults() {
			// nothing for now
			return this;
		}

		public Builder output(OutputStream os) {
			output(new OutputStreamWriter(os));
			return this;
		}

		public Builder output(Appendable w) {
			this.appender = w;
			return this;
		}

		/**
		 * Default is '#'
		 */
		public Builder commentChar(char commentChar) {
			this.commentChar = commentChar;
			return this;
		}

		/**
		 * Default is ','
		 */
		public Builder fieldSep(char fieldSep) {
			this.fieldSep = fieldSep;
			return this;
		}

		/**
		 * Lock the writer between begin/end record to allow multiple write threads. Default is false.
		 */
		public Builder threadsafe(boolean enabled) {
			this.threadsafe = enabled;
			return this;
		}

		public Builder serialiser(Serialiser serialiser) {
			this.serialiser = serialiser;
			return this;
		}

		/**
		 * If empty strings are a thing and need to be quoted (instead of being null). Default is true
		 */
		public Builder quoteEmptyStrings(boolean enabled) {
			this.quoteEmptyStrings = enabled;
			return this;
		}
	}
}