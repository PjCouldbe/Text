package model;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import opennlp.tools.stemmer.PorterStemmer;
import service.TextTokenizer;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class PseudoText {
	//private List<Mor>
	private Token[] tokens;
	private int[] punctsPoses;
	private double entropy;
	
	public class WordSquad {
		private int endTokenNumber;
		private int size;
		
		public WordSquad(int endTokenNumber, int size) {
			this.endTokenNumber = endTokenNumber;
			this.size = size;
		}
		
		public int getEndTokenNumber() {
			return endTokenNumber;
		}
		
		public int size() {
			return size;
		}
	}
	
	public PseudoText(String string) {
		if ( isNullOrEmpty(string) ) {
			throw new IllegalArgumentException(" Text string can't be either null or empty! ");
		}
		
		this.tokens = TextTokenizer.tokenize(string);
		this.punctsPoses = TextTokenizer.getDelimsPositions(tokens);
		this.entropy = calculateEntropy();
	}
	
	private double calculateEntropy() {
		PorterStemmer stemmer = new PorterStemmer();
		
		Multiset<String> set = HashMultiset.create();
		for (Token t : tokens) {
			String w = t.getWord();
			if (w.length() == 0 
					|| w.indexOf('(') >= 0 
					|| w.indexOf('"') >= 0
					|| w.indexOf('\'') >= 0
					|| w.indexOf('-') >= 0)
			{
				continue;
			}
			
			set.add( stemmer.stem(w.toLowerCase()).toString() );
		}
		
		double res = 0.0;
		for (String s : set.elementSet()) {
			res += (set.count(s) / (double)tokens.length) 
					* Math.log10( set.count(s) / (double)tokens.length ) / Math.log10(2);
		}
		
		return -res;
	}
	
	public int findTokenIndexByDelimiter(char delim) {
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].getDelimAfter() == delim) return i;
		}
		
		return -1;
	}
	
	public Token findTokenByDelimiter(char delim) {
		return tokens[ findTokenIndexByDelimiter(delim) ];
	}
	
	public WordSquad[] findPares() {
		List<WordSquad> res = new ArrayList<>();
		
		int lastPos = 0;
		for (int i : punctsPoses) {
			int j = (i - lastPos);
			if ( (j & 0b1) == 0) {  //if is even
				j--;
				res.add( new WordSquad(lastPos + j, 1) );   //far right squad
				
				while (j > 1) {
					res.add( new WordSquad(lastPos + j, 2) );
					j -= 2;
				}
				
				res.add( new WordSquad(lastPos + j, 1) );	//far left squad		
			} else {                            //if is odd
				while (j > 1) {
					res.add( new WordSquad(lastPos + j, 2) );
					j -= 2;
				}
				
				res.add( new WordSquad(lastPos + j, 1) );	//far left squad
			}
			
			lastPos = i + 1;
		}
		
		return res.toArray( new WordSquad[res.size()] );
	}
	
	public WordSquad[] unionWordSquads(WordSquad[] srcSquads, boolean ignorePuncts) {
		List<WordSquad> res = new ArrayList<>();
		
		List<Integer> borderSquadsIndexes = new ArrayList<>();    //if (ignorePuncts == true) there 
		                                                          //will be only points, else all puncts
		int lastPos = 0;
		for (int i : punctsPoses) {
			if (!ignorePuncts || tokens[i].delimsByPoint()) {
				while (lastPos < srcSquads.length && srcSquads[lastPos].getEndTokenNumber() != i) {
					lastPos++;
				}
				
				borderSquadsIndexes.add(lastPos);
				lastPos++;
			}
		}
		
		lastPos = 0;
		for (int i : borderSquadsIndexes) {
			int j = (i - lastPos);
			if ( (j & 0b1) == 0) {  //if is even
				 //far right squad
				res.add( new WordSquad(srcSquads[lastPos + j].endTokenNumber, srcSquads[lastPos + j].size) ); 
				j--;
				
				while (j > 1) {
					res.add( new WordSquad(srcSquads[lastPos + j].endTokenNumber, srcSquads[lastPos + j].size) );
					res.add( new WordSquad(srcSquads[lastPos + j - 1].endTokenNumber, srcSquads[lastPos + j - 1].size) );
					j -= 2;
				}
				
				//far left squad
				res.add( new WordSquad(srcSquads[lastPos + j].endTokenNumber, srcSquads[lastPos + j].size) );		
			} else {                            //if is odd
				while (j > 1) {
					res.add( new WordSquad(srcSquads[lastPos + j].endTokenNumber, 
								srcSquads[lastPos + j].size) );
					res.add( new WordSquad(srcSquads[lastPos + j - 1].endTokenNumber, 
								srcSquads[lastPos + j - 1].size) );
					j -= 2;
				}
				
				//far left squad
				res.add( new WordSquad(srcSquads[lastPos + j].endTokenNumber, srcSquads[lastPos + j].size) );
			}
			
			lastPos = i + 1;
		}
		
		return res.toArray( new WordSquad[ res.size() ] );
	}
	
	public String[] getTokenGroups(WordSquad[] squads) {
		String[] res = new String[squads.length];
		
		int lastIndex = 0;
		int i;
		for (int k = 0; k < squads.length; k++) {
			StringBuilder sb = new StringBuilder();
			for (i = lastIndex; i < squads[k].endTokenNumber; i++) {
				sb.append( tokens[i].toPlainString() );
			}

			if (tokens[i].getDelimAfter() != '\u0000') {
				sb.deleteCharAt(sb.length() - 1);
			}
			res[k] = sb.toString();
		}
		
		return res;
	}
	
	public String[] getAffluentWords() {
		//TODO: the first implementation was wrong.
		
		return null;
	}
	
	public String[] getSubstantialWords(String[] stubs, Set<String> stopWords) {
		//TODO
		return null;
	}
	
	public String[] getSubstantialWords(Set<String> stopWords) {
		String[] stubs = getAffluentWords();
		//TODO
		return null;
	}
	
	
	//Getters
	public String getText() {
		StringBuilder res = new StringBuilder();
		
		for (Token t : tokens) {
			res.append(t.getWord());
			res.append(t.getDelimAfter());
		}
		
		return res.toString();
	}
	
	public Token[] getTokens() {
		return tokens;
	}
	
	public double getEntropy() {
		return entropy;
	}
}