package edu.sabanciuniv.sentilab.sare.models.document.base;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.models.base.UniquelyIdentifiableObject;
import edu.sabanciuniv.sentilab.utils.CannedMessages;
import edu.sabanciuniv.sentilab.utils.extensions.*;
import edu.sabanciuniv.sentilab.utils.predicates.StringPredicates;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.factory.*;

/**
 * The base class for documents that can be tokenized.
 * @author Mus'ab Husaini
 */
public abstract class TokenizedDocument
	extends UniquelyIdentifiableObject
	implements IDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3501504745659773659L;
	
	private TokenizingOptions tokenizingOptions;
	private String tokenizedContent;
	private Map<LinguisticToken, Double> tokenWeightMap;

	private TokenizedDocument setTokenWeightMap(Map<LinguisticToken, Double> map) {
		this.tokenWeightMap = map;
		return this;
	}
	
	/**
	 * Gets the token weight map for this document.
	 * @param original {@link Boolean} flag indicating whether to get the original version or an unmodifiable version.
	 * @param suppressRetokenization flag indicating whether to suppress retokenization in all cases.
	 * @return the {@link Map} object representing the token weight map.
	 */
	protected Map<LinguisticToken, Double> getTokenWeightMap(boolean original, boolean suppressRetokenization) {
		if (!suppressRetokenization && (this.tokenWeightMap == null || this.tokenizedContent == null || !this.tokenizedContent.equals(this.getContent()))) {
			this.retokenize();
		}
		
		return original ? this.tokenWeightMap : Collections.unmodifiableMap(this.tokenWeightMap);
	}
	
	/**
	 * Gets the token weight map for this document.
	 * @param original {@link Boolean} flag indicating whether to get the original version or an unmodifiable version.
	 * @return the {@link Map} object representing the token weight map.
	 */
	protected Map<LinguisticToken, Double> getTokenWeightMap(boolean original) {
		return this.getTokenWeightMap(original, false);
	}

	/**
	 * Gets the weight of a given token.
	 * @param token the {@link LinguisticToken} object whose weight to get.
	 * @return the weight of the token.
	 */
	protected double getTokenWeight(LinguisticToken token) {
		return this.getTokenWeightMap(true).containsKey(token) ? this.getTokenWeightMap(true).get(token) : 0.0;
	}
	
	/**
	 * Sets the weight of a given token, if it already exists.
	 * @param token the {@link LinguisticToken} object whose weight to set.
	 * @param weight the weight to set.
	 * @return the weight that was set.
	 */
	protected double setTokenWeight(LinguisticToken token, double weight) {
		double current = this.getTokenWeight(token);
		double by = weight - current;
		return this.incrementTokenWeight(token, by);
	}
	
	/**
	 * Increments the weight of a given token by a certain value.
	 * @param token the {@link LinguisticToken} object whose weight is to be incremented.
	 * @param by the value to increment by.
	 * @return the final weight of the token.
	 */
	protected double incrementTokenWeight(LinguisticToken token, double by) {
		MapsExtensions.increment(this.getTokenWeightMap(true), token, by, false);
		return this.getTokenWeight(token);
	}
	
	/**
	 * Gets a copy of the tokenizing options.
	 * @return the {@link TokenizingOptions} object representing a copy of the tokenizing options.
	 */
	public TokenizingOptions getTokenizingOptions() {
		return this.tokenizingOptions.clone();
	}

	/**
	 * Sets the tokenizing options.
	 * @param tokenizingOptions the {@link TokenizingOptions} object to set.
	 * @return the {@code this} object.
	 */
	public TokenizedDocument setTokenizingOptions(TokenizingOptions tokenizingOptions) {
		this.tokenizingOptions = tokenizingOptions.clone();
		this.tokenizedContent = null;
		return this;
	}
	
	/**
	 * Gets an unmodifiable version of the token weight map.
	 * @return an unmodifiable version of the token weight map.
	 */
	public Map<LinguisticToken, Double> getTokenWeightMap() {
		return this.getTokenWeightMap(false);
	}
	
	/**
	 * Gets the total token weight.
	 * @return the total token weights.
	 */
	public double getTotalTokenWeight() {
		return IterablesExtensions.sum(this.getTokenWeightMap(true).values());
	}
	
	/**
	 * Recalculates the tokens and their weights for this document. 
	 * @return the {@code this} object.
	 */
	public TokenizedDocument retokenize() {
		Validate.notNull(this.getContent(), CannedMessages.NULL_ARGUMENT, "this.content");
		Validate.notNull(this.getStore(), CannedMessages.NULL_ARGUMENT, "this.store");
		
		this.setTokenWeightMap(new HashMap<LinguisticToken, Double>());
		
		// get the right NLP based on the language of the corpus.
		ILinguisticProcessor nlp = new LinguisticProcessorFactory()
			.create(new LinguisticProcessorFactoryOptions()
				.setMustTag(true)
				.setLanguage(this.getStore().getLanguage()));
		
		// create the map from tokens.
		LinguisticText nlpText = nlp.tag(this.getContent());
		for (LinguisticToken nlpToken : nlpText.getTokens()) {
			// only include token if the pos tag is included in tokenizing options (no tags in the options means we include everything).
			if (this.getTokenizingOptions().getTags() == null ||
				this.getTokenizingOptions().getTags().size() == 0 ||
				Iterables.any(this.getTokenizingOptions().getTags(), StringPredicates.patternContains(nlpToken.getPosTag()))) {
				
				nlpToken.setLemmatized(this.getTokenizingOptions().isLemmatized());
				MapsExtensions.increment(this.getTokenWeightMap(true, true), nlpToken);
			}
		}
		
		// make a note of the content that was used. when it changes, we will retokenize.
		this.tokenizedContent = this.getContent();
		return this;
	}
	
	@Override
	public String toString() {
		return this.getContent();
	}
}