package edu.sabanciuniv.sentilab.utils.text.nlp.stanford.english;

import java.util.List;
import java.util.Properties;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import edu.sabanciuniv.sentilab.core.controllers.IController;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

/**
 * Wraps some basic functionality of the Stanford Core NLP engine.
 * @author Mus'ab Husaini
 */
public class StanfordCoreNLPWrapper
	implements IController {
	
	private static List<String> basicAnnotators = ImmutableList.of(StanfordCoreNLP.STANFORD_TOKENIZE, StanfordCoreNLP.STANFORD_SSPLIT);
	private static StanfordCoreNLPWrapper basic;
	private static StanfordCoreNLPWrapper tagger;
	private static StanfordCoreNLPWrapper parser;

	/**
	 * Gets an instance of {@link StanfordCoreNLPWrapper} that can detect sentences and tokenize.
	 * @return a basic {@link StanfordCoreNLPWrapper} instance.
	 */
	public static StanfordCoreNLPWrapper getBasic() {
		return StanfordCoreNLPWrapper.getBasic(false);
	}
	
	/**
	 * Gets an instance of {@link StanfordCoreNLPWrapper} that can detect sentences and tokenize.
	 * @param reinitialize a flag indicating whether to re-initialize the instance or use the old one.
	 * @return a basic {@link StanfordCoreNLPWrapper} instance.
	 */
	public static StanfordCoreNLPWrapper getBasic(boolean reinitialize) {
		if (reinitialize || StanfordCoreNLPWrapper.basic == null || StanfordCoreNLPWrapper.basic.pipeline == null) {
			StanfordCoreNLPWrapper.basic = new StanfordCoreNLPWrapper();
		}
		
		return StanfordCoreNLPWrapper.basic;
	}
	
	/**
	 * Gets an instance of {@link StanfordCoreNLPWrapper} that can find POS tags.
	 * @return a tagger {@link StanfordCoreNLPWrapper} instance.
	 */
	public static StanfordCoreNLPWrapper getTagger() {
		return StanfordCoreNLPWrapper.getTagger(false);
	}

	/**
	 * Gets an instance of {@link StanfordCoreNLPWrapper} that can find POS tags. 
	 * @param reinitialize a flag indicating whether to re-initialize the instance or use the old one.
	 * @return a tagger {@link StanfordCoreNLPWrapper} instance.
	 */
	public static StanfordCoreNLPWrapper getTagger(boolean reinitialize) {
		if (reinitialize || StanfordCoreNLPWrapper.tagger == null || StanfordCoreNLPWrapper.tagger.pipeline == null) {
			StanfordCoreNLPWrapper.tagger = new StanfordCoreNLPWrapper().makeTagger();
		}
		return StanfordCoreNLPWrapper.tagger;
	}

	/**
	 * Gets an instance of {@link StanfordCoreNLPWrapper} that can parse texts.
	 * @return a parser {@link StanfordCoreNLPWrapper} instance.
	 */
	public static StanfordCoreNLPWrapper getParser() {
		return StanfordCoreNLPWrapper.getParser(false);
	}
	
	/**
	 * Gets an instance of {@link StanfordCoreNLPWrapper} that can parse texts.
	 * @param reinitialize a flag indicating whether to re-initialize the instance or use the old one.
	 * @return a parser {@link StanfordCoreNLPWrapper} instance.
	 */
	public static StanfordCoreNLPWrapper getParser(boolean reinitialize) {
		if (reinitialize || StanfordCoreNLPWrapper.parser == null || StanfordCoreNLPWrapper.parser.pipeline == null) {
			StanfordCoreNLPWrapper.parser = new StanfordCoreNLPWrapper().makeParser();
		}
		
		return StanfordCoreNLPWrapper.parser;
	}
	
	private List<String> currentAnnotators;
	private StanfordCoreNLP pipeline;
	
	/**
	 * Creates an instance of {@link StanfordCoreNLPWrapper}.
	 */
	protected StanfordCoreNLPWrapper() {
		this(StanfordCoreNLPWrapper.basicAnnotators);
	}

	private StanfordCoreNLPWrapper(Iterable<String> annotators) {
		this.preparePipeline(annotators);
	}
	
	private void preparePipeline(Iterable<String> annotators) {
		this.currentAnnotators = Lists.newArrayList(annotators);
	}
	
	private StanfordCoreNLP createPipeline() {
		Properties props = new Properties();
	    props.put("annotators", Joiner.on(", ").join(this.currentAnnotators));
	    return this.pipeline = new StanfordCoreNLP(props);
	}
	
	private StanfordCoreNLPWrapper addAnnotators(String annotators) {
		String[] annotatorsArr = annotators.split(",");
		for(String annotator : annotatorsArr) {
			if (!this.currentAnnotators.contains(annotator)) {
				this.getPipeline().addAnnotator(StanfordCoreNLP.getExistingAnnotator(annotator.trim()));
			    this.currentAnnotators.add(annotator);
			}
		}
		
		return this;
	}
	
	private StanfordCoreNLP getPipeline() {
		if (this.pipeline == null) {
			this.createPipeline();
		}
		
		return this.pipeline;
	}

	private StanfordCoreNLPWrapper makeTagger() {
		return this.addAnnotators(StanfordCoreNLP.STANFORD_POS)
			.addAnnotators(StanfordCoreNLP.STANFORD_LEMMA);
	}
	
	private StanfordCoreNLPWrapper makeParser() {
		return this.addAnnotators(StanfordCoreNLP.STANFORD_PARSE)
			.addAnnotators(StanfordCoreNLP.STANFORD_LEMMA);
	}

	/**
	 * Annotates a text using the current annotators.
	 * @param text the text to annotate.
	 * @return the annotated document.
	 */
	public Annotation annotate(String text) {
		Annotation document = new Annotation(text);
		this.getPipeline().annotate(document);
		document.compact();
		return document;
	}

	/**
	 * Gets all the sentences in a text.
	 * @param text the text to detect sentences in.
	 * @return a list of sentences.
	 */
	public Iterable<CoreMap> getSentences(String text) {
		Annotation document = this.annotate(text);
		return document.get(SentencesAnnotation.class);
	}
	
	/**
	 * Gets all the tokens in a sentence.
	 * @param sentence the sentence to tokenize.
	 * @return the tokens in this sentence.
	 */
	public Iterable<CoreLabel> getTokens(String sentence) {
		Annotation document = this.annotate(sentence);
		return document.get(TokensAnnotation.class);
	}
	
	/**
	 * Closes this instance and releases resources.
	 */
	public void close() {
		this.pipeline = null;
	}
}