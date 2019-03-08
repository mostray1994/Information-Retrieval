package PreProcessData;

import java.util.ArrayList;
/**
 * This is for INFSCI 2140 in 2018
 * 
 * TextTokenizer can split a sequence of text into individual word tokens.
 */
public class WordTokenizer {
	//you can add essential private methods or variables
	private ArrayList<String> token = new ArrayList<String>();
	private int wordIndex = 0;

	// YOU MUST IMPLEMENT THIS METHOD
	public WordTokenizer( char[] texts ) {
		// this constructor will tokenize the input texts (usually it is a char array for a whole document)
		String str2None = "(\\.)|(\t)"; // match apostrophe
		String str2Space = "([[^a-z]&&[^A-Z]&&[^\\']]+)"; // match non_letter, non_number character like whitespace and dashes,etc
		// eliminate unnecessary symbals and whitespaces
		String strText = new String(texts).replaceAll(str2None, "").replaceAll(str2Space, " ").replaceAll("( )+", " ");
		String[] text2token = strText.split(" ");
		for (int i = 0; i < text2token.length;i++){
			token.add(text2token[i]);
		}
	}
	
	// YOU MUST IMPLEMENT THIS METHOD
	public char[] nextWord() {
		// read and return the next word of the document
		// or return null if it is the end of the document
		if (wordIndex < token.size()){
			return token.get(wordIndex++).toCharArray();
		}
		return null;
	}
	
}
