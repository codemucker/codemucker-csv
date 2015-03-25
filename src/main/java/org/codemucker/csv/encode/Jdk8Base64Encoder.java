package org.codemucker.csv.encode;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.codemucker.csv.Serialiser;

class Jdk8Base64Encoder implements Serialiser {

	@Override
	public String encodeBase64(byte[] data) {
		return new String(Base64.getEncoder().encode(data), StandardCharsets.UTF_8);
	}

	@Override
	public byte[] decodeBase64(String s) {
		return Base64.getDecoder().decode(s.getBytes(StandardCharsets.UTF_8));
	}
}
