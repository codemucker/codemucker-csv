package org.codemucker.csv.encode;

import org.codemucker.csv.Serialiser;

class JaxBBase64Encoder implements Serialiser {

	@Override
	public String encodeBase64(byte[] data) {
		return javax.xml.bind.DatatypeConverter.printBase64Binary(data);
	}

	@Override
	public byte[] decodeBase64(String s) {
		return javax.xml.bind.DatatypeConverter.parseBase64Binary(s);
	}
}
