package Indexing;

import Classes.Path;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class PreProcessedCorpusReader {
	private BufferedReader bf_Reader=null;
	public PreProcessedCorpusReader(String type) throws IOException {
		// This constructor opens the pre-processed corpus file, Path.ResultHM1 + type
		// You can use your own version, or download from http://crystal.exp.sis.pitt.edu:8080/iris/resource.jsp
		// Close the file when you do not use it any more
		if (type.equals("trecweb")){
			FileInputStream file_Stream = new FileInputStream(Path.DataWebDir);
			bf_Reader = new BufferedReader(new InputStreamReader(file_Stream));
		}
		if (type.equals("trectext")){
			FileInputStream file_Stream = new FileInputStream(Path.DataTextDir);
			bf_Reader = new BufferedReader(new InputStreamReader(file_Stream));
		}
	}
	

	public Map<String, String> NextDocument() throws IOException {
		// read a line for docNo and a line for content, put into the map with <docNo, content>
		Map<String, String> oneDoc = new HashMap<String, String>();
		String docId = "";
		String docTxt = "";
		if ((docId = bf_Reader.readLine())!=null){
			docTxt = bf_Reader.readLine();
			oneDoc.put(docId,docTxt); //put docNo-content pairs into map
			return oneDoc;
		}

		bf_Reader.close();

		return null;
	}

}
