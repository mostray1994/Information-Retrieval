package Indexing;

import Classes.Path;

import java.io.IOException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;


public class MyIndexReader {
	//you are suggested to write very efficient code here, otherwise, your memory cannot hold our corpus...
	private BufferedReader bf_dic;
	private BufferedReader bf_no2id;
	private Map<String, String> map_Dic;
	private Map<String, String> map_Id2no;
	private String StoragePath;

	public MyIndexReader( String type ) throws IOException {
		//read the index files you generated in task 1
		//remember to close them when you finish using them
		//use appropriate structure to store your index
		if (type.equals("trectext"))
			StoragePath = Path.IndexTextDir;
		else
			StoragePath = Path.IndexWebDir;

		String str = "";
		InputStream isDict = new FileInputStream(StoragePath + ".dict");
		bf_dic = new BufferedReader(new InputStreamReader(isDict));
		//this map will be used to get posting list through totalix file
		map_Dic = new HashMap<String, String>();
		while ((str = bf_dic.readLine()) != null) {
			String[] s = str.split(":");
			map_Dic.put(s[0], s[1]);
		}

		InputStream isIdno = new FileInputStream(StoragePath + ".no_id");
		bf_no2id = new BufferedReader(new InputStreamReader(isIdno));
		//this map will be used to get docid or docno through each one.
		map_Id2no = new HashMap<String, String>();
		while ((str = bf_no2id.readLine()) != null) {
			String[] s = str.split(":");
			map_Id2no.put(s[0], s[1]);
			map_Id2no.put(s[1], s[0]);
		}
	}
	
	//get the non-negative integer dociId for the requested docNo
	//If the requested docno does not exist in the index, return -1
	public int GetDocid( String docno ) throws IOException{
		return Integer.parseInt(map_Id2no.getOrDefault(docno, "-1"));
	}

	// Retrieve the docno for the integer docid
	public String GetDocno( int docid ) throws IOException{
		return map_Id2no.getOrDefault(String.valueOf(docid),"NULL");
	}

	/***
	 * This method is used to get posting list through one term in the index file
	 * posting list can be used in later function.
	 * @param token
	 * @return
	 * @throws IOException
	 */
	public String PostingList(String token) throws IOException{
		// pointer is used as an position to find token which in the Index file
		String pointerList = map_Dic.getOrDefault(token, null);
		int pointer;
		if (pointerList!=null)
			pointer = Integer.parseInt(pointerList.split(",")[1]);
		else
			return null;

		String posting = "";
		InputStream isIndex = new FileInputStream(StoragePath + ".totalix");
		BufferedReader brIndex = new BufferedReader(new InputStreamReader(isIndex));
		for (int i = 0; i < pointer; i++) {
			brIndex.readLine();
		}
		posting = brIndex.readLine().split("\\s")[1];
		brIndex.close();
		return posting;
	}
	/**
	 * Get the posting list for the requested token.
	 * 
	 * The posting list records the documents' docids the token appears and corresponding frequencies of the term, such as:
	 *  
	 *  [docid]		[freq]
	 *  1			3
	 *  5			7
	 *  9			1
	 *  13			9
	 * 
	 * ...
	 * 
	 * In the returned 2-dimension array, the first dimension is for each document, and the second dimension records the docid and frequency.
	 * 
	 * For example:
	 * array[0][0] records the docid of the first document the token appears.
	 * array[0][1] records the frequency of the token in the documents with docid = array[0][0]
	 * ...
	 * 
	 * NOTE that the returned posting list array should be ranked by docid from the smallest to the largest. 
	 * 
	 * @param token
	 * @return
	 */
	public int[][] GetPostingList( String token ) throws IOException {
		String posting = PostingList(token);
		if (posting!=null){
			String[] docs = posting.split(",");
			int[][] pt = new int[docs.length][2];
			int i = 0;
			for (String doc:docs){
				if (doc.length()!=0) {
					pt[i][0] = Integer.parseInt(doc.split(":")[0]);
					//pt[i][1] = Integer.parseInt(doc.split(":")[1].split("#")[0]);
					pt[i][1] = Integer.parseInt(doc.split(":")[1]);
					i++;
				}
			}
			return pt;
		}
		return null;
	}

	// Return the number of documents that contains the token.
	public int GetDocFreq( String token ) throws IOException {
		String posting = PostingList(token);
		if (posting!=null){
			String[] docs = posting.split(",");
			int docFreq = docs.length;
			return docFreq;
		}
		return 0;
	}
	
	// Return the total number of times the token appears in the collection.
	public long GetCollectionFreq( String token ) throws IOException {
		String pointerList = map_Dic.getOrDefault(token, null);
		if (pointerList!=null)
			return Integer.parseInt(pointerList.split(",")[0]);
		else
			return 0;
	}
	
	public void Close() throws IOException {
		bf_dic.close();
		bf_no2id.close();
		map_Dic.clear();
		map_Id2no.clear();
	}
	
}