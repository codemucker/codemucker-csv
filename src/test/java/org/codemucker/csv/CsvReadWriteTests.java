package org.codemucker.csv;

import org.codemucker.jmatch.AList;
import org.codemucker.jmatch.Expect;
import org.junit.Test;

public class CsvReadWriteTests {

	@Test
	public void correctlyReadsWrites() throws Exception {

		checkParse(new String[] { "a", "b", "c" }, "a,b,c");
		checkParse(new String[] { "a", "b,", "c" }, "a,\"b,\",c");
		checkParse(new String[] { "a", "\"b\"", "c" }, "a,\"\"\"b\"\"\",c");
		checkParse(new String[] { "a", "b\nc", "d" }, "a,\"b\nc\",d");

	}

	private void checkParse(String[] expect, String line) throws CsvException {
		String[] actual;
		try {
			actual = new CsvReader(line).readNext();
		} catch (CsvException e) {
			throw new CsvException("error while reading line:" + line, e);
		}
		CsvWriter w = new CsvWriter();
		for (String field : expect) {
			w.write(field);
		}
		String backToLine = w.toString();

		Expect.that(actual).is(AList.inOrder().withOnly().items(expect));
		Expect.that(backToLine).isEqualTo(line);
	}

}
