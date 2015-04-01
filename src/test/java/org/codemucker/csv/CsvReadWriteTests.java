package org.codemucker.csv;

import java.io.StringWriter;

import org.codemucker.jmatch.AList;
import org.codemucker.jmatch.Expect;
import org.junit.Test;

public class CsvReadWriteTests {

	@Test
	public void happyPathSingleLine() throws Exception {
		checkReadWrite("a,b,c", new String[] { "a", "b", "c" });
	}
	
	@Test
	public void readHappyPathMultiLine() throws Exception {
		CsvReader r = CsvReader.with().input("a,b,c\nd,e\nf").defaults().build();
		
		Expect.that(r.readNextRecord().getData()).is(AList.inOrder().withOnly().items(new String[]{"a","b","c"}));
		Expect.that(r.readNextRecord().getData()).is(AList.inOrder().withOnly().items(new String[]{"d","e"}));
		Expect.that(r.readNextRecord().getData()).is(AList.inOrder().withOnly().items(new String[]{"f"}));
	}

	@Test
	public void handlesNulls() throws Exception {
		checkReadWrite("", new String[] {null});
		checkReadWrite(",", new String[] {null,null});		
		checkReadWrite(",a", new String[] {null,"a"});
		checkReadWrite("a,", new String[] {"a",null});
		checkReadWrite("a,,c", new String[] { "a", null, "c" });
	}

	@Test
	public void handlesEmpty() throws Exception {
		checkReadWrite("a,\"\",c", new String[] { "a", "", "c" });
		
	}
	
	@Test
	public void handlesEscapes() throws Exception {
		checkReadWrite(stod("a,'b,',c"), new String[] { "a", "b,", "c" });
		checkReadWrite(stod("a,'''b''',c"), new String[] { "a", stod("'b'"), "c" });
		checkReadWrite(stod("a,'b\nc',d"), new String[] { "a", "b\nc", "d" });	
		
		checkReadWrite(stod("a,b,'c'"), new String[] { "a", "b", "c" }, "a,b,c");
		checkReadWrite(stod("a,b,'c,'"), new String[] { "a", "b", "c," });
		checkReadWrite(stod("''"), new String[] { "" }, stod("''"));
		
	}
	
	@Test
	public void handlesDoubleEscapes() throws Exception {
		checkReadWrite(stod("a,'''',c"), new String[] { "a", stod("'"), "c" });
		checkReadWrite(stod("a,'''''',c"), new String[] { "a", stod("''"), "c" });
	}

	@Test
	public void handlesComments() throws Exception {
		checkRead("#\na,b,c", new String[] { "a", "b", "c" });
		checkRead("#\n#\na,b,c", new String[] { "a", "b", "c" });
		checkRead("#\r\n#\r\na,b,c", new String[] { "a", "b", "c" });
		checkRead("###\na,b,c", new String[] { "a", "b", "c" });
	}
	/**
	 * Single to double quotes
	 * @param s
	 * @return
	 */
	private static String stod(String s){
		return s.replace('\'', '"');
	}
	
	private void checkReadWrite(String line, String[] expect) throws CsvException {
		checkReadWrite(line, expect, line);
	}
	
	private void checkReadWrite(String line, String[] expect, String expectCsvLine) throws CsvException {
		checkRead(line, expect);
		StringWriter roundTripLine = new StringWriter();
		ICsvWriter w = CsvWriter.with().output(roundTripLine).build();
		
		for (String field : expect) {
			w.write(field);
		}
		
		Expect.that(roundTripLine.toString()).isEqualTo(expectCsvLine);
	
	}

	private void checkRead(String line,String[] expect) throws CsvException {
		ICsvRecord actual;
		try {
			actual = CsvReader.with().input(line).build().readNextRecord();
		} catch (CsvException e) {
			throw new CsvException("error while reading line:" + line, e);
		}
		Expect.that(actual.getData()).is(AList.inOrder().withOnly().items(expect));
	}
	
}
