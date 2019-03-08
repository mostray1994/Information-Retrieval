package Search;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import Classes.Path;
import Classes.Query;
import PreProcessData.*;

public class ExtractQuery {
	private BufferedReader bf_Reader;
	private String content;
	public ExtractQuery() throws Exception {
		//you should extract the 4 queries from the Path.TopicDir
		//NT: the query content of each topic should be 1) tokenized, 2) to lowercase, 3) remove stop words, 4) stemming
		//NT: you can simply pick up title only for query, or you can also use title + description + narrative for the query content.
		FileInputStream f_Stream = new FileInputStream(Path.TopicDir);
		bf_Reader = new BufferedReader(new InputStreamReader(f_Stream));
	}

	public boolean hasNext() throws Exception {
		if ((content = bf_Reader.readLine())!=null)
			return true;
		else
			bf_Reader.close();
			return false;
	}

	public Query next() throws Exception {
		String tpc_title = "";
		String tpc_title_new = "";
		Query aQuery = new Query();
		WordTokenizer tokenizer_title;
		StopWordRemover swd_Remover;
		WordNormalizer wd_Normal;
		while (!content.equals("<top>"))
			content = bf_Reader.readLine();
		// get topic number
		content = bf_Reader.readLine();
		aQuery.SetTopicId(content.substring(content.indexOf(":")+2));
		// get topic title
		content = bf_Reader.readLine();
		tpc_title = content.substring(content.indexOf(">")+2);
		while (!content.equals("</top>"))
			content = bf_Reader.readLine();
		tokenizer_title = new WordTokenizer(tpc_title.toCharArray());
		swd_Remover = new StopWordRemover();
		wd_Normal = new WordNormalizer();

		char[] word = null;

		// process the document word by word iteratively
		while ((word = tokenizer_title.nextWord()) != null) {
			word = wd_Normal.lowercase(word);
			if (!swd_Remover.isStopword(word))
				tpc_title_new += wd_Normal.stem(word) + " ";
		}
		aQuery.SetQueryContent(tpc_title_new);
		return aQuery;
	}
}
