package Indexing;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import Classes.Path;

public class MyIndexWriter {
	// I suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
	private String StoragePath;
	private OutputStream out_id;
	private BufferedWriter bf_id;
	private BufferedWriter bf_disk;
	private LinkedHashMap<String, Map<Integer, Integer>> mapIndex;
	private LinkedHashMap<String, Integer> mapDic;
	
	private int doc_id=1;
	private int blocknum=0;
	private int blockid;
	private final int MAX_BLOCK=30000;
	
	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index
		if (type.equals("trectext"))
			StoragePath=Path.IndexTextDir;
		else
			StoragePath=Path.IndexWebDir;
		new File(StoragePath).mkdir();
		
		
		File no_id=new File(StoragePath + ".no_id");
		out_id=new FileOutputStream(no_id);
		bf_id=new BufferedWriter(new OutputStreamWriter(out_id));

		mapIndex=new LinkedHashMap<>();
		mapDic=new LinkedHashMap<>();
		
		
	}
	
	public void IndexADocument(String docno, String content) throws IOException {
		// you are strongly suggested to build the index by installments
		// you need to assign the new non-negative integer docId to each document, which will be used in MyIndexReader
		String[] terms=content.split("\\s");
		for(String term:terms) {
			if(!mapIndex.containsKey(term)) {
				Map<Integer, Integer> cur=new HashMap<Integer, Integer>();
				cur.put(doc_id,1);
				mapIndex.put(term,cur);
			}else {
				Map<Integer, Integer> cur=mapIndex.get(term);
				if (!cur.containsKey(doc_id))
					cur.put(doc_id, 1);
				else
					cur.put(doc_id,cur.get(doc_id)+1);
			}
		}
		blocknum++;
		bf_id.write(doc_id + ":" + docno + "\n"); // output docid-docno to file
		doc_id ++;
		if (blocknum==MAX_BLOCK) { // if indexed enough docs, put block into disk
			this.blockToDisk();
		}
	}
	private void blockToDisk() throws IOException{
		File file=new File(StoragePath + ".b2ix" + blockid);
		OutputStream out=new FileOutputStream(file);
		bf_disk=new BufferedWriter(new OutputStreamWriter(out));

		mapIndex.forEach((k,v) -> {
			try {
				int colFreq = 0;
				Map<Integer, Integer> docFreq = v;
				for (Map.Entry<Integer, Integer> freq: docFreq.entrySet()){
					colFreq += freq.getValue();
					bf_disk.write(freq.getKey() + ":" + freq.getValue() + ",");
				}
				bf_disk.write("\n");
				docFreq.clear();

				if(!mapDic.containsKey(k))
					mapDic.put(k, colFreq);
				else
					mapDic.put(k, mapDic.get(k) + colFreq);

				mapIndex.put(k,new HashMap<Integer, Integer>());

			}catch (IOException e){}
		});
		blockid ++;
		blocknum=0;
		bf_disk.close();
	}
	
	public void Close() throws IOException {
		// close the index writer, and you should output all the buffered content (if any).
		// if you write your index into several files, you need to fuse them here.
		bf_id.close();
		// write rest term into block
		this.blockToDisk();
		mapIndex.clear();
		this.fusion();
		this.dictionary();
	}
	private void fusion() throws IOException{
		FileInputStream[] fileis=new FileInputStream[blockid];
		BufferedReader[] bf_rdBlock=new BufferedReader[blockid];
		String[] line=new String[blockid];
		File[] fr=new File[blockid];

		File fw=new File(StoragePath + ".totalix");
		OutputStream fos=new FileOutputStream(fw);
		BufferedWriter bw=new BufferedWriter(new OutputStreamWriter(fos));

		for(int i=0; i<blockid; i++){
			fr[i]=new File(StoragePath + ".b2ix" + i);
			fileis[i]=new FileInputStream(fr[i]);
			bf_rdBlock[i]=new BufferedReader(new InputStreamReader(fileis[i]));
			line[i]=bf_rdBlock[i].readLine();
		}

		Iterator<String> termList=mapDic.keySet().iterator();
		while(line[blockid-1]!=null){
			bw.write(termList.next() + " ");
			for(int i=0; i<blockid; i++){
				if(line[i]!=null){
					bw.write(line[i]);
					line[i]=bf_rdBlock[i].readLine();
				}
			}
			bw.write("\n");
		}

		bw.close();
		for(int i=0;i<blockid;i++){
			// when process of fusing has done, delete previous block file
			fr[i].delete();
			bf_rdBlock[i].close();
		}
	}
	private void dictionary() throws IOException{
		File fdic = new File(StoragePath + ".dict");
		OutputStream osdic = new FileOutputStream(fdic);
		BufferedWriter bwdic = new BufferedWriter(new OutputStreamWriter(osdic));

		int pointer = 0;

		for (Map.Entry<String, Integer> dic: mapDic.entrySet()){
			bwdic.write(dic.getKey() + ":" + dic.getValue() + "," + pointer++ + "\n");
		}

		mapDic.clear();
		bwdic.close();
	}
	
	
}
