package org.codemucker.csv;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import org.joda.time.DateTime;

public abstract class AbstractSerialiser implements Serialiser {

	private static final String[] TRUES = new String[]{"t","1", "true", "on","enabled","yes","on"};
	private static final String[] FALSES = new String[]{"f","0","false","off","disabled","no","off"};
	
	@Override
	public void toString(boolean b, Appendable appender)  throws IOException {
		appender.append(b?"t":"f");
	}

	@Override
	public void toString(int i, Appendable appender)  throws IOException {
		appender.append(Integer.toString(i));
	}

	@Override
	public void toString(byte b, Appendable appender)  throws IOException {
		appender.append(Byte.toString(b));
	}

	@Override
	public void toString(char c, Appendable appender)  throws IOException {
		appender.append(c);
	}

	@Override
	public void toString(long l, Appendable appender)  throws IOException {
		appender.append(Long.toString(l));
	}

	@Override
	public void toString(float f, Appendable appender)  throws IOException {
		appender.append(Float.toString(f));
	}

	@Override
	public void toString(double d, Appendable appender)  throws IOException {
		appender.append(Double.toString(d));
	}

	@Override
	public void toString(Date d, Appendable appender)  throws IOException {
		appender.append(Long.toString(d.getTime()));
	}

	@Override
	public void toString(DateTime dt, Appendable appender) throws IOException {
		appender.append(Long.toString(dt.getMillis()));
	}

	@Override
	public int toInt(String s)  {
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			throw wrap(s, "int", e);
		}
	}
	@Override
	public long toLong(String s)  {
		try {
			return Long.parseLong(s);
		} catch (NumberFormatException e) {
			throw wrap(s, "long", e);
		}
	}

	@Override
	public double toDouble(String s)  {
		try {
			return Double.parseDouble(s);
		} catch (NumberFormatException e) {
			throw wrap(s, "double", e);
		}
	}

	@Override
	public float toFloat(String s)  {
		try {
			return Float.parseFloat(s);
		} catch (NumberFormatException e) {
			throw wrap(s, "float", e);
		}
	}

	@Override
	public byte toByte(String s)  {
		try {
			return Byte.parseByte(s);
		} catch (NumberFormatException e) {
			throw wrap(s, "byte", e);
		}
	}
	
	@Override
	public short toShort(String s)  {
		try {
			return Short.parseShort(s);
		} catch (NumberFormatException e) {
			throw wrap(s, "short", e);
		}
	}
	
	@Override
	public boolean toBool(String s)  {
		for(int i = 0; i < TRUES.length;i++){
			if(TRUES[i].equals(s)){
				return true;
			}
		}
		for(int i = 0; i < FALSES.length;i++){
			if(FALSES[i].equals(s)){
				return true;
			}
		}
		throw new IllegalArgumentException("expect one of " + Arrays.toString(TRUES) + " or " + Arrays.toString(FALSES) + ". Instead got " + s);
	}

	@Override
	public Date toDate(String s)  {
		try {
			long msSinceEpoch = Long.parseLong(s);
			return new Date(msSinceEpoch);
		} catch (NumberFormatException e) {
			throw wrap(s, "date", e);
		}
	}

	@Override
	public DateTime toDateTime(String s)  {
		try {
			long msSinceEpoch = Long.parseLong(s);
			return new DateTime(msSinceEpoch);
		} catch (NumberFormatException e) {
			throw wrap(s, "datetime", e);
		}
	}

	@Override
	public char toChar(String s)  {
		if(s.length() == 1){
			return s.charAt(0);
		}
		throw new IllegalArgumentException("expect a singe char, instead got '" + s + "'");
	}

	private IllegalArgumentException wrap(String s, String type, Exception e){
		return new IllegalArgumentException("Error parsing '" + s + "' as " + type,e);
	}
	

}
