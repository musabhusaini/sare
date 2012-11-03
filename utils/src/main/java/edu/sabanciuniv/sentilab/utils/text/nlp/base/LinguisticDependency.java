package edu.sabanciuniv.sentilab.utils.text.nlp.base;

/**
 * The base class for objects that 
 * @author Mus'ab Husaini
 */
public abstract class LinguisticDependency
	extends LinguisticObject {

	/**
	 * Creates an instance of the {@code LinguisticDependency} object.
	 * @param processor the {@link ILinguisticProcessor} that was used to produce this dependency.
	 */
	protected LinguisticDependency(ILinguisticProcessor processor) {
		super(processor);
	}
	
	/**
	 * Gets the relation of this dependency.
	 * @return the relation.
	 */
	public abstract String getRelation();
	
	/**
	 * Gets the governor token of this dependency.
	 * @return the governor.
	 */
	public abstract LinguisticToken getGovernor();
	
	/**
	 * Gets the dependent token of this dependency.
	 * @return the dependent.
	 */
	public abstract LinguisticToken getDependent();
}