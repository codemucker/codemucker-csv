package org.codemucker.csv;

import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;

import org.codemucker.csv.encode.DefaultSerialiser;
import org.codemucker.lang.annotation.NotThreadSafe;
import org.joda.time.DateTime;

@NotThreadSafe
public class CsvRecord implements ICsvRecord {

	private final ICsvReader reader;
	private String[] data;
	private int offset;
	
	private final Serialiser serialiser;

	public CsvRecord(ICsvReader reader,String[] data) {
		this(reader,null, data);
	}
	
	public CsvRecord(ICsvReader reader) {
		this(reader,null, null);
	}

	public CsvRecord(ICsvReader reader,Serialiser serialiser) {
		this.serialiser = serialiser==null?DefaultSerialiser.get():serialiser;
		this.reader = reader;
	}

	public CsvRecord(ICsvReader reader,Serialiser serialiser,String[] data) {
		this.serialiser = serialiser==null?DefaultSerialiser.get():serialiser;
		this.data = data;
		this.reader = reader;
	}
	

	@Override
	public int getOffset() {
		return offset;
	}

	@Override
	public void setOffset(int offset) {
		this.offset = offset;
	}

	
	@Override
	public String[] getData() {
		return this.data;
	}

	@Override
	public ICsvRecord nextRecord() throws CsvException {
		return reader.readNextRecord();
	}
	@Override
	public void setData(String[] record) {
		this.data = record;
	}

	@Override
	public boolean readBool(int idx, boolean defaultVal) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toBool(s);
		}
		return defaultVal;
	}

	@Override
	public boolean readBool(int idx) {
		return serialiser.toBool(_readStringOrError(idx));
	}

	@Override
	public Boolean readBoolOrNull(int idx) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toBool(s);
		}
		return null;
	}
	
	@Override
	public byte readByte(int idx) {
		return serialiser.toByte(_readStringOrError(idx));
	}

	@Override
	public byte readByte(int idx, byte defaultVal) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toByte(s);
		}
		return defaultVal;
	}

	@Override
	public Byte readByteOrNull(int idx) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toByte(s);
		}
		return null;
	}

	@Override
	public Character readCharOrNull(int idx) {
		return serialiser.toChar(_readStringOrError(idx));
	}
	
	@Override
	public char readChar(int idx) {
		return serialiser.toChar(_readStringOrError(idx));
	}

	@Override
	public char readChar(int idx, char defaultVal) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toChar(s);
		}
		return defaultVal;
	}

	@Override
	public short readShort(int idx) {
		return serialiser.toShort(_readStringOrError(idx));
	}

	@Override
	public short readShort(int idx, short defaultVal) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toShort(s);
		}
		return defaultVal;
	}

	@Override
	public Short readShortOrNull(int idx) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toShort(s);
		}
		return null;
	}

	@Override
	public int readInt(int idx, int defaultVal) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toInt(s);
		}
		return defaultVal;
	}

	@Override
	public int readInt(int idx) {
		return serialiser.toInt(_readStringOrError(idx));
	}
	
	@Override
	public Integer readIntOrNull(int idx) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toInt(s);
		}
		return null;
	}

	@Override
	public long readLong(int idx) {
		return serialiser.toLong(_readStringOrError(idx));
	}

	@Override
	public long readLong(int idx, long defaultVal) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toLong(s);
		}
		return defaultVal;
	}

	@Override
	public Long readLongOrNull(int idx) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toLong(s);
		}
		return null;
	}

	@Override
	public double readDouble(int idx) {
		return serialiser.toDouble(_readStringOrError(idx));
	}

	@Override
	public double readDouble(int idx, double defaultVal) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toDouble(s);
		}
		return defaultVal;
	}

	@Override
	public Double readDoubleOrNull(int idx) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toDouble(s);
		}
		return null;
	}

	@Override
	public float readFloat(int idx) {
		return serialiser.toFloat(_readStringOrError(idx));
	}

	@Override
	public float readFloat(int idx, float defaultVal) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toFloat(s);
		}
		return defaultVal;
	}

	@Override
	public Float readFloatOrNull(int idx) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toFloat(s);
		}
		return null;
	}
	
	@Override
	public byte[] readBytes(int idx) {
		return serialiser.toBytes(_readStringOrError(idx));
	}

	@Override
	public byte[] readBytes(int idx, byte[] defaultVal) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toBytes(s);
		}
		return defaultVal;
	}

	@Override
	public String readString(int idx) {
		return _readStringOrError(idx);
	}

	@Override
	public String readString(int idx, String defaultVal) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return defaultVal;
		}
		return defaultVal;
	}

	@Override
	public DateTime readDateTime(int idx) {
		return serialiser.toDateTime(_readStringOrError(idx));
	}

	@Override
	public DateTime readDateTime(int idx, DateTime defaultVal) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toDateTime(s);
		}
		return defaultVal;
	}

	@Override
	public Date readDate(int idx) {
		return serialiser.toDate(_readStringOrError(idx));
	}

	@Override
	public Date readDate(int idx, Date defaultVal) {
		String s = _readStringOrNull(idx);
		if (s != null) {
			return serialiser.toDate(s);
		}
		return defaultVal;
	}

	public String _readStringOrError(int idx) {
		idx = offset + idx;
		if (data == null || idx >= data.length) {
			throw new IllegalArgumentException("Expected not null for field " + idx + ", record is " + (data==null?null:Arrays.toString(data)));
		}
		return data[idx];
	}
	
	public String _readStringOrNull(int idx) {
		idx = offset + idx;
		if (data == null || idx >= data.length) {
			return null;
		}
		return data[idx];
	}

	@Override
	public int getRemainingNumFields() {
		return Math.max(0, getTotalNumFields() - offset);
	}
	
	@Override
	public int getTotalNumFields() {
		return data == null?0:data.length;
	}
	
	
	@Override
	public IllegalArgumentException newInvalidValue(int idx, Object val,
			String expect) {
		return new IllegalArgumentException("Invalid value '" + val
				+ "' at field # " + idx + ", " + expect + ". CSV record:"
				+ toCsvLine());
	}

	private String toCsvLine() {
		StringWriter sw = new StringWriter();
		try {
			ICsvWriter w = CsvWriter.with().output(sw).build();
			for (String field : data) {
				w.write(field);
			}
		} catch (CsvWriteException e) {
			// never thrown
			e.printStackTrace();
		}
		return sw.toString();
	}


}
