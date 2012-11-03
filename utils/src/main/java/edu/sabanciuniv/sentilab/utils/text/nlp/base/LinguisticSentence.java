package edu.sabanciuniv.sentilab.utils.text.nlp.base;

/**
 * The base class for all linguistic sentences.
 * @author Mus'ab Husaini
 */
public abstract class LinguisticSentence
	extends LinguisticEntity {
	
	/**
	 * Creates an instance of {@code LinguisticSentence}.
	 * @param processor the {@link ILinguisticProcessor} that was used to produce this data.
	 */
	protected LinguisticSentence(ILinguisticProcessor processor) {
		super(processor);
	}
	
	/**
	 * Gets all the tokens in this sentence.
	 * @return the tokens in this sentence.
	 */
	public abstract Iterable<LinguisticToken> getTokens();
	
	/**
	 * Gets all the linguistic dependencies in this sentence.
	 * @return the linguistic dependencies in this sentence.
	 */
	public abstract Iterable<LinguisticDependency> getDependencies();
}