package PreProcessData;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

import Classes.*;

public class StopWordRemover {
	// Essential private methods or variables can be added.
	private FileInputStream instream_Stopword = null;
	private BufferedReader bf_Reader = null;
	private HashSet<String> hst_Stopwrod = new HashSet<String>();
	// YOU SHOULD IMPLEMENT THIS METHOD.
	public StopWordRemover( ) throws Exception{
		// Load and store the stop words from the fileinputstream with appropriate data structure.
		// NT: address of stopword.txt is Path.StopwordDir
		instream_Stopword = new FileInputStream(Path.StopwordDir);
		bf_Reader = new BufferedReader(new InputStreamReader(instream_Stopword));
		String strLine = "";
		while ((strLine = bf_Reader.readLine()) != null){
			hst_Stopwrod.add(strLine); // add stopword to hashset, prepare for isStopword function
		}
		bf_Reader.close();
		
	}

	// YOU SHOULD IMPLEMENT THIS METHOD.
	public boolean isStopword( char[] word ) {
		// Return true if the input word is a stopword, or false if not.
		String strWord = new String(word);
		if (hst_Stopwrod.contains(strWord)){
			return true;
		}
		return false;
	}
}
