package Indexing;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import Classes.Path;

public class PreProcessedCorpusReader {

	private BufferedReader bfReader=null;
	
	public PreProcessedCorpusReader(String type) throws IOException {
		// This constructor opens the pre-processed corpus file, Path.ResultHM1 + type
		// You can use your own version, or download from http://crystal.exp.sis.pitt.edu:8080/iris/resource.jsp
		// Close the file when you do not use it any more
		if (type.equals("trecweb")){
			FileInputStream fileStream = new FileInputStream(Path.DataWebDir);
			bfReader = new BufferedReader(new InputStreamReader(fileStream));
		}
		if (type.equals("trectext")){
			FileInputStream file_Stream = new FileInputStream(Path.DataTextDir);
			bfReader = new BufferedReader(new InputStreamReader(file_Stream));
		}
	}
	

	public Map<String, String> NextDocument() throws IOException {
		// read a line for docNo, put into the map with <"DOCNO", docNo>
		// read another line for the content , put into the map with <"CONTENT", content>
		Map<String,String> document=new HashMap<String,String>();
		String docId="",docTxt="";
		if((docId=bfReader.readLine())!=null) {
			docTxt=bfReader.readLine();
			document.put(docId, docTxt);
			return document;
		}
		bfReader.close();
		return null;
	}
	

}
