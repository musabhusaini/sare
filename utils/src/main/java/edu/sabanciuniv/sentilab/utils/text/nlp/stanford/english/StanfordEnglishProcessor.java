package edu.sabanciuniv.sentilab.utils.text.nlp.stanford.english;

import edu.sabanciuniv.sentilab.utils.text.nlp.annotations.LinguisticProcessorInfo;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticProcessor;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticText;
import edu.sabanciuniv.sentilab.utils.text.nlp.stanford.StanfordText;

/**
 * The Stanford natural language processor for the English language.
 * @author Mus'ab Husaini
 */
@LinguisticProcessorInfo(
	name = "Stanford Core NLP",
	language = "en",
	canTag = true,
	canParse = true
)
public class StanfordEnglishProcessor
	extends LinguisticProcessor {

	@Override
	public LinguisticText decompose(String text) {
		return new StanfordText(this, StanfordCoreNLPWrapper.getBasic().annotate(text));
	}
	
	@Override
	public LinguisticText tag(String text) {
		return new StanfordText(this, StanfordCoreNLPWrapper.getTagger().annotate(text));
	}

	@Override
	public LinguisticText parse(String text) {
		return new StanfordText(this, StanfordCoreNLPWrapper.getParser().annotate(text));
	}
}