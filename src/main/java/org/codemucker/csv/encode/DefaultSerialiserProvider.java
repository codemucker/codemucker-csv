package org.codemucker.csv.encode;

import org.codemucker.csv.Serialiser;

/**
 * Use our own decoder instead of apache commons codec as Android includes an
 * older version causing issues, We can't use the new jva.util.Base64 either as
 * that is only available in java 1.8+. Make a best effort to find one
 */
public class DefaultSerialiserProvider {

	private static Serialiser INSTANCE;

	static {
		autoDetectSerialiser();
	}

	private static void autoDetectSerialiser() {
		Appendable appender = new StringBuilder();
		try {
			Serialiser s = new Jdk8Base64Encoder();
			s.toString(new byte[] { 1, 2, 3 }, appender);
			INSTANCE = s;
			return;
		} catch (Exception e) {
		} catch (NoClassDefFoundError e) {
		}
		try {
			Serialiser s = new JaxBBase64Encoder();
			s.toString(new byte[] { 1, 2, 3 }, appender);
			INSTANCE = s;
			return;
		} catch (Exception e) {
		} catch (NoClassDefFoundError e) {
		}
		INSTANCE = null;
	}

	public static void setDefaultSerialiser(Serialiser s) {
		if (s == null) {
			autoDetectSerialiser();
		} else {
			INSTANCE = s;
		}
	}

	public static Serialiser getSerialiser() {
		if (INSTANCE == null) {
			throw new RuntimeException(
					"no serialiser set and could not auto discover one");
		}
		return INSTANCE;
	}

	private DefaultSerialiserProvider() {
	}

}
