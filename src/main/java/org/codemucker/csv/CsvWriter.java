package org.codemucker.csv;

import org.apache.commons.codec.binary.Base64;
import org.codemucker.lang.annotation.NotThreadSafe;

@NotThreadSafe
public class CsvWriter {
	private static final boolean ESCAPE = true;
	private static final boolean NOESCAPE = false;

	private final int initSize = 100;
	private final int trimToSize = 1000;
	private final StringBuilder sb = new StringBuilder(initSize);
	
	public CsvWriter write(byte[] bytes) {
		String val = Base64.encodeBase64String(bytes);
		write(val,ESCAPE);
		return this;
	}
	
	public CsvWriter write(boolean b) {
		appendSep();
		sb.append(b?'t':'f');
		return this;
	}
	
	public CsvWriter write(short s) {
		appendSep();
		sb.append(s);
		return this;
	}
	
	public CsvWriter write(char c) {
		appendSep();
		sb.append(c);
		return this;
	}
	
	public CsvWriter write(float f) {
		appendSep();
		sb.append(f);
		return this;
	}
	
	public CsvWriter write(long l) {
		appendSep();
		sb.append(l);
		return this;
	}
	
	public CsvWriter write(int i) {
		appendSep();
		sb.append(i);			
		return this;
	}
	
	public CsvWriter write(String s) {
		write(s,ESCAPE);
		return this;
	}

	/**
	 * Write a string which is safe, this will not be csv escaped
	 * @param s
	 * @return
	 */
	public CsvWriter writeNonEscaped(String s) {
		write(s,NOESCAPE);
		return this;
	}
	
	public CsvWriter write(String s, boolean escape) {
		appendSep();
		if (s != null) {
			if (escape) {
				boolean doEscape = false;
				for(int i = 0;i < s.length();i++){
					char c = s.charAt(i);
					if(c == '"' || c == ',' || c == '\n' ){
						doEscape = true;
						break;
					}
				}
				if(doEscape){
					sb.append('"');
					for(int i = 0;i < s.length();i++){
						char c = s.charAt(i);
						if(c == '"'){
							sb.append('"').append('"');
						} else {
							sb.append(c);
						}
					}	
					sb.append('"');
					
				} else {
					sb.append(s);
				}
			} else {
				sb.append(s);
			}
		}
		return this;
	}
	
	private void appendSep(){
		if(sb.length() > 0){
			sb.append(',');
		}
	}
	
	public void clear(){
		sb.setLength(0);
		if(sb.capacity() > trimToSize){
			sb.trimToSize();
			sb.ensureCapacity(initSize);
		}
	}

	@Override
	public String toString() {
		return sb.toString();
	}
}