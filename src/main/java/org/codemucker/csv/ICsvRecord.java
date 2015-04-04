package org.codemucker.csv;

import java.util.Date;

import org.codemucker.lang.annotation.NotThreadSafe;
import org.joda.time.DateTime;

@NotThreadSafe
public interface ICsvRecord {

	boolean readBool(int idx);
	boolean readBool(int idx, boolean defaultVal);
	Boolean readBoolOrNull(int idx);

	char readChar(int idx);
	char readChar(int idx, char defaultVal);
	Character readCharOrNull(int idx);

	short readShort(int idx);
	short readShort(int idx, short defaultVal);
	Short readShortOrNull(int idx);

	byte readByte(int idx);
	byte readByte(int idx, byte defaultVal);
	Byte readByteOrNull(int idx);

	int readInt(int idx);
	int readInt(int idx, int defaultVal);
	Integer readIntOrNull(int idx);

	double readDouble(int idx);
	double readDouble(int idx, double defaultVal);
	Double readDoubleOrNull(int idx);
	
	float readFloat(int idx);
	float readFloat(int idx, float defaultVal);
	Float readFloatOrNull(int idx);
	
	long readLong(int idx);
	long readLong(int idx, long defaultVal);
	Long readLongOrNull(int idx);
	
	String readString(int idx);
	String readString(int idx, String defaultVal);

	byte[] readBytes(int idx);
	byte[] readBytes(int idx, byte[] defaultVal);

	DateTime readDateTime(int idx);
	DateTime readDateTime(int idx, DateTime defaultVal);

	Date readDate(int idx);
	Date readDate(int idx, Date defaultVal);
	
	public String[] getData();

	public void setData(String[] fields);

	IllegalArgumentException newInvalidValue(int idx, Object val, String expect);

	public int getTotalNumFields();
	/**
	 * Return the number of fields left after the offset
	 * @return
	 */
	public int getRemainingNumFields();
	public int getOffset();
	
	public void setOffset(int offset);
	
	public ICsvRecord nextRecord() throws CsvException;

}
