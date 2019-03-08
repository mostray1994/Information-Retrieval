package PseudoRFSearch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Classes.Document;
import Classes.Query;
import IndexingLucene.MyIndexReader;
import SearchLucene.QueryRetrievalModel;

public class PseudoRFRetrievalModel {

	MyIndexReader ixreader;
	
	//The Dirichlet Prior Smoothing factor
	private double miu = 500.0;
	//set collection size
	private long col_size;
	//store query results
	private Map<Integer, HashMap<String, Integer>> query_Result;
	//store collection frequency to compute Dirichlet Prior Smoothing
	private Map<String, Long> col_freq;
		
	public PseudoRFRetrievalModel(MyIndexReader ixreader)
	{
		this.ixreader=ixreader;
	}
	
	/**
	 * Search for the topic with pseudo relevance feedback in 2017 spring assignment 4. 
	 * The returned results (retrieved documents) should be ranked by the score (from the most relevant to the least).
	 * 
	 * @param aQuery The query to be searched for.
	 * @param TopN The maximum number of returned document
	 * @param TopK The count of feedback documents
	 * @param alpha parameter of relevance feedback model
	 * @return TopN most relevant document, in List structure
	 */
	public List<Document> RetrieveQuery( Query aQuery, int TopN, int TopK, double alpha) throws Exception {	
		// this method will return the retrieval result of the given Query, and this result is enhanced with pseudo relevance feedback
		// (1) you should first use the original retrieval model to get TopK documents, which will be regarded as feedback documents
		// (2) implement GetTokenRFScore to get each query token's P(token|feedback model) in feedback documents
		// (3) implement the relevance feedback model for each token: combine the each query token's original retrieval score P(token|document) with its score in feedback documents P(token|feedback model)
		// (4) for each document, use the query likelihood language model to get the whole query's new score, P(Q|document)=P(token_1|document')*P(token_2|document')*...*P(token_n|document')
		
		
		//get P(token|feedback documents)
		HashMap<String,Double> TokenRFScore=GetTokenRFScore(aQuery,TopK);
		
		
		// sort all retrieved documents from most relevant to least, and return TopN
		List<Document> results=new ArrayList<Document>();
		
		//***//
	
		// get retrieval doc score and store tokens in an array
		String[] tokens=aQuery.GetQueryContent().split(" ");

		
		// store query likelihood model result [docid, score], by multiply all probability to get score.
		HashMap<Integer,Double> docScore=new HashMap<>();
		query_Result.forEach((docid,term_tf)->{
			int doclen=0;
			double score=1.0;
			try {
				doclen=ixreader.docLength(docid);
			}catch(Exception e) {};
			// Dirichlet piror smoothing
			// p(w|D) = (c(w,D) + MIU*p(w|REF) ) / (|D| + MIU), score  = mulitiply(p(w|D))
			for (String token : tokens) {
				long ctf=col_freq.get(token);
				if (ctf==0) continue;
				int tf=term_tf.getOrDefault(token, 0); //c(w|D)
				double p_ref = (double)ctf / col_size; // p(w|REF)
				score*=alpha*(((double)tf+miu*p_ref)/((double)doclen+miu))+(1-alpha)*TokenRFScore.get(token);
			}
			docScore.put(docid,score);
		});

		//sort doc score.
		List<Map.Entry<Integer,Double>> listScore = new ArrayList<>(docScore.entrySet());
		Collections.sort(listScore,new Comparator<Map.Entry<Integer,Double>>(){
					@Override
			public int compare(Map.Entry<Integer,Double> o1,Map.Entry<Integer,Double> o2) {
				int temp=o2.getValue().compareTo(o1.getValue());
				if(temp==0){
					return o2.getKey().compareTo(o1.getKey());
				}
				return temp;
			}
		});

		//put documents from listScore list to result list
		for (int rank=0;rank<TopN;rank++) {
			Document doc=null;
			Map.Entry<Integer, Double>item=listScore.get(rank);
			try{
				int id=item.getKey();
				doc=new Document(Integer.toString(id),ixreader.getDocno(id), item.getValue());
			} catch(Exception e) {};
			results.add(doc);
		}

		return results;		
	}
	
	public HashMap<String,Double> GetTokenRFScore(Query aQuery,  int TopK) throws Exception
	{
		// for each token in the query, you should calculate token's score in feedback documents: P(token|feedback documents)
		// use Dirichlet smoothing
		// save <token, score> in HashMap TokenRFScore, and return it
		HashMap<String,Double> TokenRFScore=new HashMap<String,Double>();
		
		/***/
		
		//store tokens in an array.
		String[] tokens=aQuery.GetQueryContent().split(" ");
		//get feedback documents
		List<Document> fbDoc=new QueryRetrievalModel(ixreader).retrieveQuery(aQuery, TopK);

		col_freq=new HashMap<>();
		query_Result=new HashMap<>();

		//search each token
		for (String token:tokens){
			//get times of a token appears in the collection.
			long ctf = ixreader.CollectionFreq(token);
			col_freq.put(token, ctf);
			if(ctf==0) {
				System.out.println(token + " is not found.");
				continue;
			}

			int[][] postArray = ixreader.getPostingList(token);
			for(int[] post : postArray) {
				if(!query_Result.containsKey(post[0])) {
					HashMap<String, Integer> temp=new HashMap<>();
					temp.put(token, post[1]);
					query_Result.put(post[0], temp);
				}
				else
					query_Result.get(post[0]).put(token, post[1]);
			}
		}

		//each of feedback documents is a big pseudo document
		//pseudo document stores token and its term frequency
		Map<String, Integer> pseudo_Doc=new HashMap<>();
		int doclen=0;
		for (Document doc:fbDoc){
			query_Result.get(Integer.parseInt(doc.docid())).forEach((term,freq)->{
				if (pseudo_Doc.containsKey(term)){
					pseudo_Doc.put(term,freq + pseudo_Doc.get(term));
				}
				else pseudo_Doc.put(term,freq);
			});
			doclen+=ixreader.docLength(Integer.parseInt(doc.docid()));
		}

		//calculate token score
		final int doc_len = doclen;
		pseudo_Doc.forEach((token, freq) -> {
			double score = 1.0;
			//same as the query_result.
			long ctf=col_freq.get(token);

			double pref =(double)ctf/col_size; // p(w|REF)
			score=((double)freq+miu*pref)/((double)doc_len + miu);
			TokenRFScore.put(token,score);
		});

				
		return TokenRFScore;
	}
	
	
}