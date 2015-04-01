package org.codemucker.csv;

import java.io.IOException;
import java.util.Date;

import org.joda.time.DateTime;

public interface Serialiser {

	public void toString(Object obj, Appendable appender) throws IOException;
	public void toString(boolean b, Appendable appender) throws IOException;
	public void toString(int i, Appendable appender) throws IOException;
	public void toString(byte b, Appendable appender) throws IOException;
	public void toString(byte[] bytes, Appendable appender) throws IOException;
	public void toString(char c, Appendable appender) throws IOException;
	public void toString(long l, Appendable appender) throws IOException;
	public void toString(float f, Appendable appender) throws IOException;
	public void toString(double d, Appendable appender) throws IOException;
	public void toString(Date d, Appendable appender) throws IOException;
	public void toString(DateTime dt, Appendable appender) throws IOException;
	
	public <T> T toObject(String s,Class<T> type);
	public short toShort(String s);
	public int toInt(String s);
	public long toLong(String s);
	public double toDouble(String s);
	public float toFloat(String s);
	public boolean toBool(String s);
	public char toChar(String s);
	public Date toDate(String s);
	public DateTime toDateTime(String s);
	public byte toByte(String s);
	public byte[] toBytes(String s);
	

	
}
