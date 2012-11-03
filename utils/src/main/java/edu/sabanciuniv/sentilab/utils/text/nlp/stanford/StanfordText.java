package edu.sabanciuniv.sentilab.utils.text.nlp.stanford;

import org.apache.commons.lang3.Validate;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.utils.CannedMessages;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.ILinguisticProcessor;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticSentence;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticText;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticToken;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

public class StanfordText extends LinguisticText {
	
	private Annotation document;
	
	public StanfordText(ILinguisticProcessor processor, Annotation text) {
		super(processor);
		
		this.document = Validate.notNull(text, CannedMessages.NULL_ARGUMENT, "text");
	}

	@Override
	public Iterable<LinguisticSentence> getSentences() {
		return Iterables.transform(document.get(SentencesAnnotation.class), new Function<CoreMap, LinguisticSentence>() {
			@Override
			public LinguisticSentence apply(CoreMap input) {
				return new StanfordSentence(processor, input);
			}
		});
	}

	@Override
	public Iterable<LinguisticToken> getTokens() {
		return Iterables.transform(document.get(TokensAnnotation.class), new Function<CoreLabel, LinguisticToken>() {
			@Override
			public LinguisticToken apply(CoreLabel input) {
				return new StanfordToken(processor, input);
			}
		});
	}

	@Override
	public String getText() {
		return this.document.get(TextAnnotation.class);
	}
}