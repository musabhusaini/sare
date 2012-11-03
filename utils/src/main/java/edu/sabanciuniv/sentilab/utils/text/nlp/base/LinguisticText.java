package edu.sabanciuniv.sentilab.utils.text.nlp.base;

/**
 * The base class for a linguistic text.
 * @author Mus'ab Husaini
 */
public abstract class LinguisticText
	extends LinguisticEntity {
	
	/**
	 * Creates an instance of {@code LinguisticText}.
	 * @param processor the {@link ILinguisticProcessor} that was used to produce this data.
	 */
	protected LinguisticText(ILinguisticProcessor processor) {
		super(processor);
	}
	
	/**
	 * Gets all the sentences in this text.
	 * @return the sentences in this text.
	 */
	public abstract Iterable<LinguisticSentence> getSentences();
	
	/**
	 * Gets all the tokens in this text.
	 * @return the tokens in this text.
	 */
	public abstract Iterable<LinguisticToken> getTokens();
}