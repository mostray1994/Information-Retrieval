package Indexing;

import java.io.*;
import java.util.*;

import Classes.Path;


public class MyIndexWriter {
	// I suggest you to write very efficient code here, otherwise, your memory cannot hold our corpus...
	private OutputStream out_no2id;
	private BufferedWriter bf_no2id;
	private BufferedWriter bf_block2disk;
	private LinkedHashMap<String, Map<Integer, Integer>> mapIndex;
	private LinkedHashMap<String, Integer> mapDic;

	private String StoragePath;
	private int doc_id = 0;
	private int blocknum = 0;
	private int blockid;
	private final int MAX_IN_BLOCK = 30000;

	public MyIndexWriter(String type) throws IOException {
		// This constructor should initiate the FileWriter to output your index files
		// remember to close files if you finish writing the index
		if (type.equals("trectext"))
			StoragePath = Path.IndexTextDir;
		else
			StoragePath = Path.IndexWebDir;

		new File(StoragePath).mkdir();

		File no2id = new File(StoragePath + ".no2id");
		out_no2id = new FileOutputStream(no2id);
		bf_no2id = new BufferedWriter(new OutputStreamWriter(out_no2id));

		mapIndex = new LinkedHashMap<>();
		mapDic = new LinkedHashMap<>();
	}

	public void IndexADocument(String docno, String content) throws IOException {
		// you are strongly suggested to build the index by installments
		// you need to assign the new non-negative integer docId to each document, which will be used in MyIndexReader
		//invert content to hashmap including term: [doc_id,freq,positions]
		String[] terms = content.split("\\s");
		for (String term : terms){
			if (!mapIndex.containsKey(term)){
				//mapIndex.put(term, new ArrayList<Integer>(Arrays.asList(doc_id, 1, i)));
				// if the term has't posting list, then add new posting
				Map<Integer, Integer> cur = new HashMap<Integer, Integer>();
				cur.put(doc_id,1);
				mapIndex.put(term,cur);
			}else {
				//mapIndex.get(term).add(i);
				// update document frequency
				Map<Integer, Integer> cur = mapIndex.get(term);
				if (!cur.containsKey(doc_id))
					cur.put(doc_id, 1);
				else
					cur.put(doc_id,cur.get(doc_id)+1);
			}
		}

		blocknum++;
		bf_no2id.write(doc_id + ":" + docno + "\n"); // output docid-docno to file
		doc_id ++;
		if (blocknum == MAX_IN_BLOCK) { // if indexed enough docs, put block into disk
			this.blockToDisk();
		}
	}

	public void Close() throws IOException {
		// close the index writer, and you should output all the buffered content (if any).
		// if you write your index into several files, you need to fuse them here.
		bf_no2id.close();
		// write rest term into block
		this.blockToDisk();
		mapIndex.clear();
		this.fusion();
		this.dictionary();
	}

	/***
	 * This method is used to put a block into disk if block is filled with indexes.
	 * @throws IOException
	 */
	private void blockToDisk() throws IOException{
		File file = new File(StoragePath + ".b2ix" + blockid);
		OutputStream fos = new FileOutputStream(file);
		bf_block2disk = new BufferedWriter(new OutputStreamWriter(fos));

		mapIndex.forEach((k,v) -> {
			try {
				int colFreq = 0;
				Map<Integer, Integer> docFreq = v;
				for (Map.Entry<Integer, Integer> freq: docFreq.entrySet()){
					colFreq += freq.getValue();
					bf_block2disk.write(freq.getKey() + ":" + freq.getValue() + ",");
				}
				bf_block2disk.write("\n");
				docFreq.clear();

				if(!mapDic.containsKey(k))
					mapDic.put(k, colFreq);
				else
					mapDic.put(k, mapDic.get(k) + colFreq);

				mapIndex.put(k,new HashMap<Integer, Integer>());

			}catch (IOException e){}
		});
		blockid ++;
		blocknum = 0;
		bf_block2disk.close();
	}

	/***
	 * This method is used to merge blocks in the disk
	 * @throws IOException
	 */
	private void fusion() throws IOException{
		FileInputStream[] fileis = new FileInputStream[blockid];
		BufferedReader[] bf_rdBlock = new BufferedReader[blockid];
		String[] line = new String[blockid];
		File[] fr = new File[blockid];

		File fw = new File(StoragePath + ".totalix");
		OutputStream fos = new FileOutputStream(fw);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

		for(int i=0; i<blockid; i++){
			fr[i] = new File(StoragePath + ".b2ix" + i);
			fileis[i] = new FileInputStream(fr[i]);
			bf_rdBlock[i] = new BufferedReader(new InputStreamReader(fileis[i]));
			line[i] = bf_rdBlock[i].readLine();
		}

		Iterator<String> termList = mapDic.keySet().iterator();
		// Because linkedhashmap record the sequence of every term in map, so when map writes posting list
		// to the block, the sequence will be same as sequence in map which can help us read same line in every
		// block file and put them together to represent one term's total posting
		while(line[blockid-1] != null){
			bw.write(termList.next() + " ");
			for(int i=0; i<blockid; i++){
				if(line[i] != null){
					bw.write(line[i]);
					line[i] = bf_rdBlock[i].readLine();
				}
			}
			bw.write("\n");
		}

		bw.close();
		for(int i=0; i<blockid; i++){
			// when process of fusing has done, delete previous block file
			fr[i].delete();
			bf_rdBlock[i].close();
		}
	}

	/***
	 * This method is used to build a dictionary for index term
	 * @throws IOException
	 */
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
