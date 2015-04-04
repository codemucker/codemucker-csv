package org.codemucker.csv;

import java.util.Date;

import org.joda.time.DateTime;

public class DelegateCsvWriter implements ICsvWriter {
	
	private final ICsvWriter delegate;
	
	public DelegateCsvWriter(ICsvWriter parent) {
		super();
		this.delegate = parent;
	}
	
	protected ICsvWriter getDelegate(){
		return delegate;
	}

	@Override
	public void beginRecord() throws CsvWriteException {
		delegate.beginRecord();
	}

	@Override
	public void endRecord() throws CsvWriteException {
		delegate.endRecord();
	}
	
	
	public ICsvWriter writeRecordComment(String s) throws CsvWriteException {
		return delegate.writeRecordComment(s);
	}

	@Override
	public ICsvWriter write(Object obj) throws CsvWriteException {
		return delegate.write(obj);
	}

	@Override
	public ICsvWriter write(char c) throws CsvWriteException {
		return delegate.write(c);
	}

	@Override
	public ICsvWriter write(boolean b) throws CsvWriteException {
		return delegate.write(b);
	}

	@Override
	public ICsvWriter write(int i) throws CsvWriteException {
		return delegate.write(i);
	}

	@Override
	public ICsvWriter write(float f) throws CsvWriteException {
		return delegate.write(f);
	}

	@Override
	public ICsvWriter write(double d) throws CsvWriteException {
		return delegate.write(d);
	}

	@Override
	public ICsvWriter write(byte b) throws CsvWriteException {
		return delegate.write(b);
	}

	@Override
	public ICsvWriter write(byte[] b) throws CsvWriteException {
		return delegate.write(b);
	}

	@Override
	public ICsvWriter write(long l) throws CsvWriteException {
		return delegate.write(l);
	}

	@Override
	public ICsvWriter write(String s) throws CsvWriteException {
		return delegate.write(s);
	}

	@Override
	public ICsvWriter writeNonEscaped(String s) throws CsvWriteException {
		return delegate.writeNonEscaped(s);
	}

	@Override
	public ICsvWriter write(Boolean b) throws CsvWriteException {
		return delegate.write(b);
	}

	@Override
	public ICsvWriter write(Float f) throws CsvWriteException {
		return delegate.write(f);
	}

	@Override
	public ICsvWriter write(Long l) throws CsvWriteException {
		return delegate.write(l);
	}

	@Override
	public ICsvWriter write(Byte b) throws CsvWriteException {
		return delegate.write(b);
	}

	@Override
	public ICsvWriter write(Integer i) throws CsvWriteException {
		return delegate.write(i);
	}

	@Override
	public ICsvWriter write(Double d) throws CsvWriteException {
		return delegate.write(d);
	}

	@Override
	public ICsvWriter write(Character c) throws CsvWriteException {
		return delegate.write(c);
	}

	@Override
	public ICsvWriter write(DateTime d) throws CsvWriteException {
		return delegate.write(d);
	}

	@Override
	public ICsvWriter write(Date d) throws CsvWriteException {
		return delegate.write(d);
	}

	@Override
	public void flush() throws CsvWriteException {
		delegate.flush();
	}

	@Override
	public Appendable getAppender() {
		return delegate.getAppender();
	}

	public ICsvWriter getEmbeddedWriter() {
		return delegate.getEmbeddedWriter();
	}

	@Override
	public void lock() {
		delegate.lock();
	}

	@Override
	public void unlock() {
		delegate.unlock();
	}
}
