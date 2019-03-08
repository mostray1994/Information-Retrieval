package PreProcessData;

import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import Classes.Path;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;


/**
 * This is for INFSCI 2140 in 2018
 *
 */
public class TrectextCollection implements DocumentCollection {
	//you can add essential private methods or variables
	private BufferedReader bf_Reader = null;
	private FileInputStream instream_Collection = null;
	
	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrectextCollection() throws IOException {
		// This constructor should open the file in Path.java.DataTextDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!
		instream_Collection = new FileInputStream(Path.DataTextDir);
		bf_Reader = new BufferedReader(new InputStreamReader(instream_Collection));
	}
	
	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		
		Map<String,Object> oneDoc = new HashMap<String,Object>(); // store corpus
		String line = bf_Reader.readLine();
		String strDocId = "";
		StringBuilder text = new StringBuilder();//we use StringBuilder instead of String to improve the performance.
		if (line != null && line.length() != 0){
			while (!line.equals("<DOC>")){
				line=bf_Reader.readLine();
			}
			if(line.equals("<DOC>")){
				line=bf_Reader.readLine(); // next line is <DOCNO>
				strDocId=line.substring(8,24);//we can get id at substring(8,24).
			}
			while(!line.equals("<TEXT>")) {
				line = bf_Reader.readLine();
			}
			if(line.equals("<TEXT>")){
				line = bf_Reader.readLine(); //the content is below the <TEXT> line.
				while (!line.equals("</TEXT>")) {
					text.append(line);//add a line to one content.
					line = bf_Reader.readLine();
				}
			}
			while(!line.equals("</DOC>")){
				line=bf_Reader.readLine();
			}
			oneDoc.put(strDocId,text.toString().toCharArray());//put id and content to hashmap.
			return oneDoc;
		}
		//close reader.
		if(bf_Reader==null)bf_Reader.close();
		return null;
	}
}
