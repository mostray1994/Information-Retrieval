package Search;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import Classes.Path;
import Classes.Query;
import PreProcessData.StopWordRemover;
import PreProcessData.WordNormalizer;
import PreProcessData.WordTokenizer;

public class ExtractQuery {
	
	private BufferedReader bf_Reader;
	private String content;
	
	public ExtractQuery() throws Exception {
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
		FileInputStream fileStream = new FileInputStream(Path.TopicDir);
		bf_Reader = new BufferedReader(new InputStreamReader(fileStream));
	}
	
	public boolean hasNext() throws Exception
	{
		if ((content=bf_Reader.readLine())!=null)
			return true;
		else
			bf_Reader.close();
			return false;
	}
	
	public Query next() throws Exception
	{
		String topTitle="";
		String topTitleNew="";
		Query query=new Query();
		WordTokenizer tokenizerTitle;
		StopWordRemover swRemover;
		WordNormalizer wdNormal;
		while (!content.equals("<top>"))
			content=bf_Reader.readLine();
		// get topic number
		content=bf_Reader.readLine();
		query.SetTopicId(content.substring(content.indexOf(":")+2));
		// get topic title
		content=bf_Reader.readLine();
		topTitle=content.substring(content.indexOf(">")+2);
		while (!content.equals("</top>"))
			content=bf_Reader.readLine();
		tokenizerTitle=new WordTokenizer(topTitle.toCharArray());
		swRemover=new StopWordRemover();
		wdNormal=new WordNormalizer();

		char[] word=null;

		// process the document word by word iteratively
		while ((word=tokenizerTitle.nextWord())!=null) {
			word=wdNormal.lowercase(word);
			if (!swRemover.isStopword(word))
				topTitleNew+=wdNormal.stem(word)+ " ";
		}
		query.SetQueryContent(topTitleNew);
		return query;
	}
}
