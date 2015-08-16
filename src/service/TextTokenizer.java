package service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import model.Token;

import com.google.common.base.Strings;

public class TextTokenizer {
	private static final String DELIMITERS = "( – )|[\\p{Punct}\\x20]";
	
	public static Token[] tokenize(String text) {
		if ( Strings.isNullOrEmpty(text) ) return new Token[0];
		
		StringBuilder copy = new StringBuilder(text);
		List<Token> tokens = new ArrayList<>(text.length() / 7); //tet.length() / 7 = avg amount of words in text
		
		Matcher m = Pattern.compile(DELIMITERS).matcher(text);
		while (m.find()) {
			String c = m.group();
			
			int pos = copy.indexOf(c);
			char delim = c.charAt(0);
			if (c.length() > 1) {   // if m.group() matches " – "
				delim = c.charAt(1);
			}
			
			String w = copy.substring(0, pos);
			tokens.add( new Token(w, delim) );
			
			int i = tokens.size() - 2;
			StringBuilder oneMoreTokenWord = new StringBuilder(w);
			switch (delim) {
			case ')':
				while (i >= 0 && tokens.get(i).getDelimAfter() != '(') {
					oneMoreTokenWord.append( tokens.get(i).getDelimAfter() );
					oneMoreTokenWord.append( tokens.get(i).getWord() );
					i--;
				}
				break;
			case '"':
				while (i >= 0 && tokens.get(i).getDelimAfter() != '"') {
					oneMoreTokenWord.append( tokens.get(i).getDelimAfter() );
					oneMoreTokenWord.append( tokens.get(i).getWord() );
					i--;
				}
			case '\'':
				while (i >= 0 && tokens.get(i).getDelimAfter() != '\'') {
					oneMoreTokenWord.append( tokens.get(i).getDelimAfter() );
					oneMoreTokenWord.append( tokens.get(i).getWord() );
					i--;
				}
				break;
			}

			if (i == tokens.size() - 2) {       //if oneMoreTokenWord has not changed
				while (i >= 0 && tokens.get(i).getDelimAfter() == '-') {
					oneMoreTokenWord.append('-');
					oneMoreTokenWord.append( tokens.get(i).getWord() );
					i--;
				}
				if (i != tokens.size() - 2) {   //if oneMoreTokenWord after all changed
					tokens.add( new Token(oneMoreTokenWord.toString(), delim) );
				}
			}
			
			copy.delete(0, pos + c.length());   //supposed 1 (default) or 3 (if matches " – ")
		}
		
		if (copy.length() > 0) {
			tokens.add( new Token(copy.toString(), '\u0000') );
		}
		
		return tokens.toArray( new Token[ tokens.size() ] );
	}
	
	public static int[] getDelimsPositions(Token[] tokens) {
		List<Integer> res = new ArrayList<>();
		
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].getDelimAfter() != ' ') {
				res.add(i);
			}
		}
		
		int[] resArray = new int[res.size()];
		for (int i = 0; i < resArray.length; i++) {
			resArray[i] = (int)res.get(i);
		}
		
		return resArray;
	}
}