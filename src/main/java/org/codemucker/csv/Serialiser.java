package org.codemucker.csv;

public interface Serialiser {

	public String encodeBase64(byte[] data);
	public byte[] decodeBase64(String s);
	
}
