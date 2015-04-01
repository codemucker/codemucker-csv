package org.codemucker.csv.encode;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.codemucker.csv.AbstractSerialiser;

class Jdk8Base64Encoder extends AbstractSerialiser {

	@Override
	public void toString(byte[] data,Appendable appender) throws IOException {
		appender.append(new String(Base64.getEncoder().encode(data), StandardCharsets.UTF_8));
	}

	@Override
	public byte[] toBytes(String s) {
		try {
			return Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8));
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
