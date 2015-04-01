package org.codemucker.csv.encode;

import java.io.IOException;

import org.codemucker.csv.AbstractSerialiser;

class JaxBBase64Encoder extends AbstractSerialiser {

	@Override
	public void toString(byte[] data, Appendable appender) throws IOException {
		String val = javax.xml.bind.DatatypeConverter.printBase64Binary(data);
		appender.append(val);
	}

	@Override
	public byte[] toBytes(String s){
		try {
			return javax.xml.bind.DatatypeConverter.parseBase64Binary(s);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("Error base64 decoding '" + s
					+ "'", e);
		}
	}

	@Override
	public void toString(Object obj, Appendable appender) throws IOException {
		throw new UnsupportedOperationException("Dont know how to convert " + obj + " to string");
	}

	@Override
	public <T> T toObject(String s, Class<T> type) {
		throw new UnsupportedOperationException("Dont know how to convert string into object type" + type.getName());
	}
}
