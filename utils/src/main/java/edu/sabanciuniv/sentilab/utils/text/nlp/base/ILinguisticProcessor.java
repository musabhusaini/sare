package edu.sabanciuniv.sentilab.utils.text.nlp.base;

/**
 * A class that implements this interface will be able to provide NLP capabilities.
 * @author Mus'ab Husaini
 */
public interface ILinguisticProcessor {
	
	/**
	 * Decomposes a given text using NLP to its sentences and tokens.
	 * @param text the text to decompose.
	 * @return a {@link LinguisticText} object containing the decomposed text.
	 */
	public LinguisticText decompose(String text);
	
	/**
	 * Tags a given text with POS tags.
	 * @param text the text to tag.
	 * @return a {@link LinguisticText} object containing the tagged text.
	 */
	public LinguisticText tag(String text);
	
	/**
	 * Parses a given text for linguistic dependencies.
	 * @param text the text to parse.
	 * @return a {@link LinguisticText} object containing the parsed text.
	 */
	public LinguisticText parse(String text); 
}