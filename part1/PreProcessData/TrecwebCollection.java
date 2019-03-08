package PreProcessData;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import Classes.Path;

/**
 * This is for INFSCI 2140 in 2018
 *
 */
public class TrecwebCollection implements DocumentCollection {
	// Essential private methods or variables can be added.
	private BufferedReader bf_Reader = null;
	private FileInputStream instream_Collection = null;
	// YOU SHOULD IMPLEMENT THIS METHOD.
	public TrecwebCollection() throws IOException {
		// 1. Open the file in Path.DataWebDir.
		// 2. Make preparation for function nextDocument().
		// NT: you cannot load the whole corpus into memory!!
		instream_Collection = new FileInputStream(Path.DataTextDir);
		bf_Reader = new BufferedReader(new InputStreamReader(instream_Collection));
		
	}
	
	// YOU SHOULD IMPLEMENT THIS METHOD.
	public Map<String, Object> nextDocument() throws IOException {
		// 1. When called, this API processes one document from corpus, and returns its doc number and content.
		// 2. When no document left, return null, and close the file.
		// 3. the HTML tags should be removed in document content.
		Map<String,Object> map=new HashMap<String,Object>();
		if (bf_Reader == null) {
			if (bf_Reader == null) bf_Reader.close();
			return null;
		}
		
		String line=bf_Reader.readLine();
		//StringBuilder id=new StringBuilder();
		String id="";
		StringBuilder content=new StringBuilder();
		if(line==null||line.length()==0) {
			if (bf_Reader == null) bf_Reader.close();
			return null;
		}
		
		while(line!="<DOC>")line=bf_Reader.readLine();
		line=bf_Reader.readLine();
		//id.append(line.substring(8).substring(beginIndex, endIndex));
		line=line.substring(8);
		//id.append(line.substring(line.length()-9, line.length()));
		id=line.substring(line.length()-9, line.length());
		id=id.replace(" ", "");
		
		while(line!="</DOCHDR")line=bf_Reader.readLine();
		line=bf_Reader.readLine();
		while(line!="</DOC>") {
			line=bf_Reader.readLine();
			content.append(line);
			content.append(" ");
		}
		map.put(id, content.toString().toCharArray());
		bf_Reader.close();
		return map;
		//return null;
	}
	
}
