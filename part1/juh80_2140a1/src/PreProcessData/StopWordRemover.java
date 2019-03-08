package PreProcessData;
import Classes.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class StopWordRemover {
	//you can add essential private methods or variables.
	private FileInputStream instream=null;
	private BufferedReader bf_Reader=null;
	private HashSet<String> hs=new HashSet<String >();

	public StopWordRemover( ) throws IOException {
		// load and store the stop words from the fileinputstream with appropriate data structure
		// that you believe is suitable for matching stop words.
		// address of stopword.txt should be Path.java.StopwordDir
		instream=new FileInputStream(Path.StopwordDir);
		bf_Reader=new BufferedReader(new InputStreamReader(instream));
		String line = "";
		while ((line = bf_Reader.readLine()) != null){
			//add stop word to HashSet
			hs.add(line);
		}
		bf_Reader.close();
	}
	
	// YOU MUST IMPLEMENT THIS METHOD
	public boolean isStopword( char[] word ) {
		// judge the word is included in the HashSet or not
		String str=new String(word);
		if (hs.contains(str)){
			return true;
		}
		return false;
	}
}
