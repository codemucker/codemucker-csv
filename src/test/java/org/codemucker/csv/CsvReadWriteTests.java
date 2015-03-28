package org.codemucker.csv;

import org.codemucker.jmatch.AList;
import org.codemucker.jmatch.Expect;
import org.junit.Test;

public class CsvReadWriteTests {

	@Test
	public void handlesComments() throws Exception {
		checkRead("#\na,b,c", new String[] { "a", "b", "c" });
		checkRead("#\n#\na,b,c", new String[] { "a", "b", "c" });
		checkRead("#\r\n#\r\na,b,c", new String[] { "a", "b", "c" });
		checkRead("###\na,b,c", new String[] { "a", "b", "c" });
	}
	
	@Test
	public void correctlyReadsWrites() throws Exception {
		checkReadWrite("a,b,c", new String[] { "a", "b", "c" });
		checkReadWrite("a,\"b,\",c", new String[] { "a", "b,", "c" });
		checkReadWrite("a,\"\"\"b\"\"\",c", new String[] { "a", "\"b\"", "c" });
		checkReadWrite("a,\"b\nc\",d", new String[] { "a", "b\nc", "d" });
	}

	private void checkReadWrite(String line, String[] expect) throws CsvException {
		checkRead(line, expect);
		CsvWriter w = new CsvWriter();
		for (String field : expect) {
			w.write(field);
		}
		String backToLine = w.toString();
		Expect.that(backToLine).isEqualTo(line);
	}

	private void checkRead(String line,String[] expect) throws CsvException {
		String[] actual;
		try {
			actual = CsvReader.with().defaults().input(line).build().readNextRecord();
		} catch (CsvException e) {
			throw new CsvException("error while reading line:" + line, e);
		}
		Expect.that(actual).is(AList.inOrder().withOnly().items(expect));
	}
	
}
