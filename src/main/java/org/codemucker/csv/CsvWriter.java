package org.codemucker.csv;

import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.codemucker.csv.encode.DefaultSerialiserProvider;
import org.codemucker.lang.annotation.NotThreadSafe;
import org.joda.time.DateTime;

import com.google.common.base.Preconditions;

@NotThreadSafe
public class CsvWriter {

	private static class DeferToSerialiserWriter extends AbstractCsvWriter {

		public DeferToSerialiserWriter(Appendable w, Serialiser serialiser,
				char fieldSep, char commentChar, boolean threadsafeRecord) {
			super(w, serialiser, fieldSep, commentChar, threadsafeRecord);
		}

		@Override
		public ICsvWriter write(Boolean b) throws CsvWriteException {
			nextField();
			if (b != null) {
				try {
					serialiser.toString(b, writer);
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
				serialiser.toString(b, writer);
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
					serialiser.toString(c, writer);
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
				serialiser.toString(c, writer);
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
					serialiser.toString(f, writer);
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
				serialiser.toString(f, writer);
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
					serialiser.toString(l, writer);
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
				serialiser.toString(l, writer);
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
					serialiser.toString(b, writer);
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
				serialiser.toString(b, writer);
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
					serialiser.toString(i, writer);
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
				serialiser.toString(i, writer);
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
					serialiser.toString(d, writer);
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
				serialiser.toString(d, writer);
			} catch (IOException e) {
				throw wrap(e);
			}

			return this;
		}
	}

	private static class DirectPrimitiveWriter extends AbstractCsvWriter {

		public DirectPrimitiveWriter(Appendable w, Serialiser serialiser,
				char fieldSep, char commentChar,boolean threadsafeRecord) {
			super(w, serialiser, fieldSep, commentChar,threadsafeRecord);
		}

		@Override
		public ICsvWriter write(Boolean b) throws CsvWriteException {
			nextField();
			if (b != null) {
				print(b ? 't' : 'f');
			}
			return this;
		}

		@Override
		public ICsvWriter write(boolean b) throws CsvWriteException {
			nextField();
			print(b ? 't' : 'f');
			return this;
		}

		@Override
		public ICsvWriter write(Character c) throws CsvWriteException {
			nextField();
			if (c != null) {
				print(c.charValue());
			}
			return this;
		}

		@Override
		public ICsvWriter write(char c) throws CsvWriteException {
			nextField();
			print(c);
			return this;
		}

		@Override
		public ICsvWriter write(Float f) throws CsvWriteException {
			nextField();
			if (f != null) {
				print(f.toString());
			}
			return this;
		}

		@Override
		public ICsvWriter write(float f) throws CsvWriteException {
			nextField();
			print(Float.toString(f));
			return this;
		}

		@Override
		public ICsvWriter write(Long l) throws CsvWriteException {
			nextField();
			if (l != null) {
				print(l.toString());
			}
			return this;
		}

		@Override
		public ICsvWriter write(long l) throws CsvWriteException {
			nextField();
			print(Long.toString(l));
			return this;
		}

		@Override
		public ICsvWriter write(Byte b) throws CsvWriteException {
			nextField();
			if (b != null) {
				print(b.toString());
			}
			return this;
		}

		@Override
		public ICsvWriter write(byte b) throws CsvWriteException {
			nextField();
			print(Byte.toString(b));
			return this;
		}

		@Override
		public ICsvWriter write(Integer i) throws CsvWriteException {
			nextField();
			if (i != null) {
				print(i.toString());
			}
			return this;
		}

		@Override
		public ICsvWriter write(int i) throws CsvWriteException {
			nextField();
			print(Integer.toString(i));
			return this;
		}

		@Override
		public ICsvWriter write(Double d) throws CsvWriteException {
			nextField();
			if (d != null) {
				print(d.toString());
			}
			return this;
		}

		@Override
		public ICsvWriter write(double d) throws CsvWriteException {
			nextField();
			print(Double.toString(d));
			return this;
		}
	}

	private abstract static class AbstractCsvWriter implements ICsvWriter {
		private final char commentChar;
		private final char fieldSep;
		private static final String EMPTY_STRING = "\"\"";
		private static final char NL = '\n';
		private static final char DQUOTE = '"';

		final Appendable writer;
		final Serialiser serialiser;

		private int fieldNum = -1;
		private int recordNumber = 0;

		private final boolean quoteEmptyStrings = true;
		private final boolean flushable;
		private final boolean threadsafeRecord;
		private final Lock lock;

		public AbstractCsvWriter(Appendable w, Serialiser serialiser,
				char fieldSep, char commentChar,boolean threadsafeRecord) {
			this.serialiser = serialiser;
			this.writer = w;
			this.fieldSep = fieldSep;
			this.commentChar = commentChar;
			this.threadsafeRecord = threadsafeRecord;
			this.flushable = (w instanceof Flushable);
			this.lock = threadsafeRecord ? new ReentrantLock():null;
		}

		public int getRecordNumber() {
			return recordNumber;
		}

		@Override
		public ICsvWriter write(Object obj) throws CsvWriteException {
			nextField();
			if (obj != null) {
				try {
					serialiser.toString(obj, writer);
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
					serialiser.toString(d, writer);
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
					serialiser.toString(d, writer);
				} catch (IOException e) {
					throw wrap(e);
				}
			}
			return this;
		}

		@Override
		public ICsvWriter write(byte[] bytes) throws CsvWriteException {
			nextField();
			if (bytes != null) {
				try {
					print(DQUOTE);
					serialiser.toString(bytes, writer);
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
						writer.append('"');
						for (int i = 0; i < s.length(); i++) {
							char c = s.charAt(i);
							if (c == '"') {
								writer.append('"').append('"');
							} else {
								writer.append(c);
							}
						}
						writer.append('"');

					} else {
						writer.append(s);
					}
				}
			} catch (IOException e) {
				throw wrap(e);
			}
			return;
		}

		protected void print(String s) throws CsvWriteException {
			try {
				if (s == null) {
					// print nothing
				} else if (s.length() == 0) {
					if (quoteEmptyStrings) {
						writer.append(EMPTY_STRING);
					}
				} else {
					writer.append(s);
				}
			} catch (IOException e) {
				throw wrap(e);
			}
		}

		protected void print(char c) throws CsvWriteException {
			try {
				writer.append(c);
			} catch (IOException e) {
				throw wrap(e);
			}
		}

		protected void println() throws CsvWriteException {
			try {
				writer.append(NL);
			} catch (IOException e) {
				throw wrap(e);
			}
		}

		@Override
		public void flush() throws CsvWriteException {
			try {
				((Flushable) writer).flush();
			} catch (IOException e) {
				throw wrap(e);
			}
		}

		protected CsvWriteException wrap(IOException e) {
			return new CsvWriteException(
					"stream threw an error while writing record", e);
		}

		@Override
		public Appendable getAppender() {
			return writer;
		}
	}
	
	public static Builder with() {
		return new Builder();
	}

	public static class Builder {
		private char commentChar = '#';
		private char fieldSep = ',';
		private boolean threadsafe = false;
		private boolean customPrimitives = false;
		private Serialiser serialiser;
		private Appendable writer;

		public ICsvWriter build() {
			Preconditions.checkNotNull(writer, "expect output");
			Serialiser ser = serialiser == null ? DefaultSerialiserProvider.getSerialiser() : serialiser;
			if(customPrimitives){
				return new DirectPrimitiveWriter(writer, ser, fieldSep, commentChar, threadsafe);					
			} else {
				return new DeferToSerialiserWriter(writer, ser, fieldSep, commentChar, threadsafe);				
			}
		}

		public Builder  defaults(){
			//nothing for now
			return this;
		}
		
		public Builder output(OutputStream os) {
			output(new OutputStreamWriter(os));
			return this;
		}

		public Builder output(Appendable w) {
			this.writer = w;
			return this;
		}

		public Builder commentChar(char commentChar) {
			this.commentChar = commentChar;
			return this;
		}

		public Builder fieldSep(char fieldSep) {
			this.fieldSep = fieldSep;
			return this;
		}

		public Builder threadsafe(boolean threadsafe) {
			this.threadsafe = threadsafe;
			return this;
		}

		public Builder customPrimitives(boolean customPrimitives) {
			this.customPrimitives = customPrimitives;
			return this;
		}

		public Builder serialiser(Serialiser serialiser) {
			this.serialiser = serialiser;
			return this;
		}

	}
}