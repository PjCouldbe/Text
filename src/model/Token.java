package model;

import com.google.common.base.Strings;

public class Token {
	private String word;
	private char delimAfter;
	
	public Token(String word, char delim) {
		if ( Strings.isNullOrEmpty(word) ) {
			throw new IllegalArgumentException(" A word can't be null or empty! ");
		}
		this.word = word;
		
		this.delimAfter = delim;
	}
	
	public boolean hasNullDelim() {
		return delimAfter == '\u0000';
	}
	
	public boolean delimsByPoint() {
		return delimAfter == '.';
	}
	
	public boolean delimsByHyphen() {
		return delimAfter == '-';
	}
	
	public boolean delimsByDash() {
		return delimAfter == '–';
	}
	
	
	//Getters
	public String getWord() {
		return word;
	}
	
	public char getDelimAfter() {
		return delimAfter;
	}
	
	
	//Overriding Object's methods
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		result = prime * result	+ (delimAfter == '0' ? 0 : delimAfter);
		result = prime * result + (word == null ? 0 : word.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		
		Token other = (Token) obj;
		return this.word.equals(other.word) && this.delimAfter == other.delimAfter;
	}

	@Override
	public String toString() {
		String realDelimiter = hasNullDelim() 
							? "none" 
							: (delimsByDash() ? " " + delimAfter + " " : Character.toString(delimAfter));
		
		return "Token (word = " + word + "; "
				+ "delims by = " + realDelimiter + ")";
	}
	
	public String toPlainString() {
		String realDelimiter = hasNullDelim() 
							? "none" 
							: (delimsByDash() ? " " + delimAfter + " " : Character.toString(delimAfter));
		
		return word  + realDelimiter;
	}
}