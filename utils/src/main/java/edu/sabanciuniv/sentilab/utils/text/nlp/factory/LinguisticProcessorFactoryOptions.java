package edu.sabanciuniv.sentilab.utils.text.nlp.factory;

/**
 * The options required by the {@link LinguisticProcessorFactory}.
 * @author Mus'ab Husaini
 */
public class LinguisticProcessorFactoryOptions {
	
	private String name;
	private boolean ignoreNameCase;
	private String language;
	private boolean mustTag;
	private boolean mustParse;
	
	/**
	 * Gets the name of the desired linguistic processor.
	 * @return the name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Sets the name of the desired linguistic processor.
	 * @param name the name to set.
	 * @return the {@code this} object.
	 */
	public LinguisticProcessorFactoryOptions setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Gets a flag indicating whether to ignore case for name.
	 * @return the flag.
	 */
	public boolean isIgnoreNameCase() {
		return this.ignoreNameCase;
	}

	/**
	 * Sets a flag indicating whether to ignore case for name.
	 * @param ignoreNameCase the flag to set.
	 * @return the {@code this} object.
	 */
	public LinguisticProcessorFactoryOptions setIgnoreNameCase(boolean ignoreNameCase) {
		this.ignoreNameCase = ignoreNameCase;
		return this;
	}

	/**
	 * Gets the language of the desired linguistic processor.
	 * @return the language.
	 */
	public String getLanguage() {
		return language;
	}
	
	/**
	 * Sets the language of the desired linguistic processor.
	 * @param language the language to set.
	 * @return the {@code this} object.
	 */
	public LinguisticProcessorFactoryOptions setLanguage(String language) {
		this.language = language;
		return this;
	}

	/**
	 * Gets a flag indicating whether the desired linguistic processor must be able to tag or not.
	 * @return the flag.
	 */
	public boolean isMustTag() {
		return this.mustTag;
	}

	/**
	 * Sets a flag indicating whether the desired linguistic processor must be able to tag or not.
	 * @param mustTag the flag to set.
	 * @return the {@code this} object.
	 */
	public LinguisticProcessorFactoryOptions setMustTag(boolean mustTag) {
		this.mustTag = mustTag;
		return this;
	}

	/**
	 * Gets a flag indicating whether the desired linguistic processor must be able to parse or not.
	 * @return the flag.
	 */
	public boolean isMustParse() {
		return this.mustParse;
	}

	/**
	 * Sets a flag indicating whether the desired linguistic processor must be able to parse or not.
	 * @param mustParse the flag to set.
	 * @return the {@code this} object.
	 */
	public LinguisticProcessorFactoryOptions setMustParse(boolean mustParse) {
		this.mustParse = mustParse;
		return this;
	}
}