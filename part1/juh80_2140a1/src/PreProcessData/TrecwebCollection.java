package PreProcessData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import Classes.Path;

import java.util.HashMap;

/**
 * This is for INFSCI 2140 in 2018
 *
 */
public class TrecwebCollection implements DocumentCollection {
	//you can add essential private methods or variables
	private BufferedReader bf_Reader = null;
	private FileInputStream instream_Collection = null;
	// match html tag
	private static final String htmlTag = "(<(.*?)>)|(\\[(.*?)\\])";

	// YOU SHOULD IMPLEMENT THIS METHOD
	public TrecwebCollection() throws IOException {
		// This constructor should open the file in Path.DataWebDir
		// and also should make preparation for function nextDocument()
		// you cannot load the whole corpus into memory here!!
		instream_Collection = new FileInputStream(Path.DataWebDir);
		bf_Reader = new BufferedReader(new InputStreamReader(instream_Collection));
	}
	
	// YOU SHOULD IMPLEMENT THIS METHOD
	public Map<String, Object> nextDocument() throws IOException {
		
		
		Map<String,Object> oneDoc = new HashMap<String,Object>(); // store corpus
		String line=bf_Reader.readLine();
		String id="";
		//use StringBuilder to improve performance.
		StringBuilder text=new StringBuilder();
		if (line!=null &&line.length() != 0){
			while(!line.equals("<DOC>")){
				line=bf_Reader.readLine();
			}
			if(line.equals("<DOC>")){
				line=bf_Reader.readLine(); // next line is <DOCNO>
				id=line.substring(7,24);//we can get id at substring(7,24).
			}
			while(!line.equals("</DOCHDR>")) {
				line=bf_Reader.readLine();
			}
			if(line.equals("</DOCHDR>")){
				//the content is below "</DOCHDR">
				line = bf_Reader.readLine();
				while(!line.equals("</DOC>")) {
					// add line to content
					text.append(line);
					//text.append(" ");
					line=bf_Reader.readLine();
				}
			}
			//put id and content to hashmap
			oneDoc.put(id,text.toString().replaceAll(htmlTag," ").toCharArray());
			return oneDoc;
		}
		//close buffer reader
		if (bf_Reader==null) bf_Reader.close();
		return null;
	}
	
}
