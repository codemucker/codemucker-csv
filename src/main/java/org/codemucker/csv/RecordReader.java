package org.codemucker.csv;

import org.codemucker.csv.encode.SerialiserFactory;

public class RecordReader {
	
	private final Serialiser serialiser;
	
	public RecordReader(){	
		this(SerialiserFactory.getSerialiser());
	}
	
	public RecordReader(Serialiser serialiser){
		this.serialiser = serialiser;
	}
	
	
	public boolean readBool(String[] record, int idx, boolean defaultVal) {
		String val = readString(record, idx, defaultVal ? "t" : "f").toLowerCase();
		switch (val) {
		case "t":
		case "1":
			return true;
		case "f":
		case "0":
			return false;
		case "true":
		case "enabled":
		case "on":
		case "yes":
			return true;
		case "false":
		case "disabled":
		case "off":
		case "no":
		case "":
			return false;
		default:
			throw newInvalidValue(record, idx, val,
					"expect one of:t,1,true,yes,enabled,on,f,false,0,off,disabled,no,,");
		}
	}

	public char readChar(String[] record, int idx, String defaultVal) {
		String val = readString(record, idx, defaultVal);
		if (val.length() != 1) {
			throw newInvalidValue(record, idx, val,
					"expect a string of lenght 1");
		}
		return val.charAt(0);
	}
	
	public int readInt(String[] record, int idx, int defaultVal) {
		return Integer.parseInt(readString(record, idx, Integer.toString(defaultVal)));
	}

	public long readLong(String[] record, int idx, long defaultVal) {
		return Long.parseLong(readString(record, idx,Long.toString(defaultVal)));
	}

	public byte[] readBytes(String[] record, int idx) {
		String val = readString(record, idx, null);
		if (val == null) {
			return null;
		}
		byte[] data = serialiser.decodeBase64(val);
		return data;
	}

	public String readString(String[] record, int idx) {
		return readString(record, idx, null);
	}

	public String readString(String[] record, int idx, String defaultVal) {
		if (idx >= record.length) {
			return defaultVal;
		}
		String val = record[idx];
		return val.length() == 0?null:val;
	}

	public IllegalArgumentException newInvalidValue(String[] record,
			int idx, Object val, String expect) {
		return new IllegalArgumentException("Invalid value '" + val
				+ "' at field # " + idx + ", " + expect + ". CSV record:"
				+ toCsvLine(record));
	}
	
	private String toCsvLine(String[] record) {
		CsvWriter w = new CsvWriter();
		for (String field : record) {
			w.write(field);
		}
		return w.toString();
	}
}