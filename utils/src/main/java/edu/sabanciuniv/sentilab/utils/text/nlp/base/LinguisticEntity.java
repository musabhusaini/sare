package edu.sabanciuniv.sentilab.utils.text.nlp.base;

/**
 * Abstract class for any linguistic entity of a text.
 * @author Mus'ab Husaini
 */
public abstract class LinguisticEntity
	extends LinguisticObject
	implements Comparable<LinguisticEntity> {
	
	/**
	 * Creates an instance of {@link LinguisticEntity} with the specified text value.
	 * @param processor the {@link ILinguisticProcessor} that was used to produce this data.
	 */
	protected LinguisticEntity(ILinguisticProcessor processor) {
		super(processor);
	}
	
	/**
	 * Gets the text value of this entity.
	 * @return The text value of this entity.
	 */
	public abstract String getText();
	
	@Override
	public String toString() {
		return this.getText();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof LinguisticEntity) {
			return this.compareTo((LinguisticEntity)other) == 0;
		}
		
		return super.equals(other);
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	@Override
	public int compareTo(LinguisticEntity other) {
		return this.toString().compareTo(other.toString());
	}
}