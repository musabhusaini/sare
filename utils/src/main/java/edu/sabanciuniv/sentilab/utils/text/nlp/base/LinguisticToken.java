package edu.sabanciuniv.sentilab.utils.text.nlp.base;

/**
 * The base class for a linguistic token.
 * @author Mus'ab Husaini
 */
public abstract class LinguisticToken
	extends LinguisticEntity {
	
	/**
	 * A flag indicating whether this token is lemmatized or not.
	 */
	protected boolean isLemmatized;
	
	/**
	 * Creates an instance of the {@code LinguisticToken} class.
	 * @param processor the {@link ILinguisticProcessor} that was used to produce this data.
	 */
	protected LinguisticToken(ILinguisticProcessor processor) {
		super(processor);
	}
	
	/**
	 * Gets a flag indicating whether this token is lemmatized or not.
	 * @return the flag.
	 */
	public boolean isLemmatized() {
		return this.isLemmatized;
	}

	/**
	 * Sets the flag indicating whether this token is lemmatized or not.
	 * @param isLemmatized the flag to set.
	 * @return this object.
	 */
	public LinguisticToken setLemmatized(boolean isLemmatized) {
		this.isLemmatized = isLemmatized;
		return this;
	}

	/**
	 * Gets the lemma of this token.
	 * @return The lemma.
	 */
	public abstract String getLemma();

	/**
	 * Gets the POS tag of this token.
	 * @return The POS tag.
	 */
	public abstract String getPosTag();
	
	/**
	 * Gets the word that this token represents. Might be lemmatized depending on the value of the {@code isLemmatized} property.
	 * @return the word that this token represents.
	 */
	public String getWord() {
		return !this.isLemmatized() ? this.getText() : this.getLemma();
	}
	
	@Override
	public String toString() {
		return this.getWord() + "/" + this.getPosTag();
	}
}