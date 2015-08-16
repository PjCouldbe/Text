import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import model.PseudoText;
import service.TextTokenizer;


public class Main {
	public void run(File file) {
		StringBuilder text = new StringBuilder();
		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			while (reader.ready()) {
				text.append( reader.readLine() );
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(text);
		System.out.println();
		System.out.println( arrayToString( TextTokenizer.tokenize(text.toString()) ) );
		
		System.out.println();
		PseudoText ptext = new PseudoText( text.toString() );
		System.out.println( 1.0 / ptext.getEntropy() );
	}
	
	public static void main(String[] args) {
		if (args.length != 1) {
			throw new IllegalArgumentException(" You have to specify one argument - the path to file with text ");
		}
		File file = new File(args[0]);
		
		(new Main()).run(file);
	}
	
	private String arrayToString(Object[] array) {
		StringBuilder res = new StringBuilder('[');
		
		for (Object obj : array) {
			res.append(obj.toString());
			res.append(", \n");
		}
		
		if (res.charAt(res.length() - 2) == ',') {
			res = res.delete(res.length() - 2, res.length());
		}
		res.append(']');
		
		return res.toString();
	}
}
