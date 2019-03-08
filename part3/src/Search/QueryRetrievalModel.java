package Search;

import java.io.IOException;
import java.util.*;

import Classes.Query;
import Classes.Document;
import IndexingLucene.MyIndexReader;

public class QueryRetrievalModel {
	protected MyIndexReader indexReader;
	//The Dirichlet Prior Smoothing factor
	private double MIU = 500.0;
	// set collection size
	private long COLLECTION_SIZE;

	public QueryRetrievalModel(MyIndexReader ixreader) throws Exception {
		indexReader = ixreader;
		// get collection size
		COLLECTION_SIZE = indexReader.collect_length();
	}

	/**
	 * Search for the topic information.
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * TopN specifies the maximum number of results to be returned.
	 *
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @return
	 */

	public List<Document> retrieveQuery( Query aQuery, int TopN ) throws IOException {
		// NT: you will find our IndexingLucene.Myindexreader provides method: docLength()
		// implement your retrieval model here, and for each input query, return the topN retrieved documents
		// sort the docs based on their relevance score, from high to low
		List<Document> results = new ArrayList<>();

		// store the result of query with the format: [docid, [term, term_freq]]
		Map<Integer, HashMap<String, Integer>> query_Result = new HashMap<>();
		// store the collection_frep to compute Dirichlet Prior Smoothing
		Map<String, Long> collect_termFreq = new HashMap<>();
		// store tokens in aQuery into String array
		String[] tokens = aQuery.GetQueryContent().split(" ");
		//search for each token then calculate the corresponding scores
		for (String token:tokens){
			//get the total number of times the token appears in the collection.
			long ctf = indexReader.CollectionFreq(token);
			collect_termFreq.put(token, ctf);
			if (ctf == 0) {
				System.out.println(token + " is not found in the collection!");
				continue;
			}

			int[][] postingList = indexReader.getPostingList(token);
			for(int[] posting : postingList) {
				if(!query_Result.containsKey(posting[0])) {
					HashMap<String, Integer> temp = new HashMap<>();
					temp.put(token, posting[1]);
					query_Result.put(posting[0], temp);
				}
				else
					query_Result.get(posting[0]).put(token, posting[1]);
			}
		}

		// query likelihood model, calculate the probability of
		// each document model generating each query terms
		// then multiply all probability to get score
		// store query likelihood model result [docid, score]
		HashMap<Integer,Double> doc_score = new HashMap<>();
		query_Result.forEach((docid, term_tf) -> {
			int doclen = 0;
			double score = 1.0;
			try {
				doclen = indexReader.docLength(docid);
				// System.out.println("doclen: "+doclen);
			} catch(Exception e) {};
			// Dirichlet piror smoothing
			// p(w|D) = (c(w,D) + MIU*p(w|REF) ) / (|D| + MIU)
			// score  = mulitiply(p(w|D))
			for (String token : tokens) {
				long ctf = collect_termFreq.get(token);
				if (ctf == 0) continue;
				int tf = term_tf.getOrDefault(token, 0); //c(w|D)

				//if (tf == 0) continue;
				double p_ref = (double)ctf / COLLECTION_SIZE; // p(w|REF)
				//System.out.println("tf: "+tf +" p_ref: "+p_ref+" doclen: "+doclen);
				score *= ((double)tf + MIU*p_ref) / ((double)doclen + MIU);
			}
			doc_score.put(docid,score);
		});

		//sort doc_score by score
		List<Map.Entry<Integer,Double>> list_doc_score = new ArrayList<Map.Entry<Integer,Double>>(doc_score.entrySet());
		Collections.sort(list_doc_score,new Comparator<Map.Entry<Integer,Double>>(){
			@Override
			public int compare(Map.Entry<Integer, Double> o1,
							   Map.Entry<Integer, Double> o2) {
				int flag = o2.getValue().compareTo(o1.getValue());
				if(flag==0){
					return o2.getKey().compareTo(o1.getKey());
				}
				return flag;
			}
		});

		// put all documents into result list
		for (int rank = 0; rank < TopN; rank++) {
			Document doc = null;
			Map.Entry<Integer, Double> item = list_doc_score.get(rank);
			try {
				int id = item.getKey();
				doc = new Document(Integer.toString(id), indexReader.getDocno(id), item.getValue());
			} catch(Exception e) {};
			results.add(doc);
		}
		return results;
	}

}