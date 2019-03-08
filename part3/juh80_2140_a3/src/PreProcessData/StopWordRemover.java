package PreProcessData;
import Classes.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class StopWordRemover {
	//you can add essential private methods or variables.
	private FileInputStream instream_Stopword = null;
	private BufferedReader bf_Reader = null;
	private HashSet<String> hst_Stopwrod = new HashSet<String >();

	public StopWordRemover( ) throws IOException {
		// load and store the stop words from the fileinputstream with appropriate data structure
		// that you believe is suitable for matching stop words.
		// address of stopword.txt should be Path.java.StopwordDir
		instream_Stopword = new FileInputStream(Path.StopwordDir);
		bf_Reader = new BufferedReader(new InputStreamReader(instream_Stopword));
		String strLine = "";
		while ((strLine = bf_Reader.readLine()) != null){
			hst_Stopwrod.add(strLine); // add stopword to hashset, prepare for isStopword function
		}
		bf_Reader.close();
	}
	
	// YOU MUST IMPLEMENT THIS METHOD
	public boolean isStopword( char[] word ) {
		// return true if the input word is a stopword, or false if not
		String strWord = new String(word);
		if (hst_Stopwrod.contains(strWord)){
			return true;
		}
		return false;
	}
}
