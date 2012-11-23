package edu.sabanciuniv.sentilab.utils.text.nlp.base;

import org.apache.commons.lang3.Validate;

import edu.sabanciuniv.sentilab.core.models.IModel;

/**
 * The base class for all objects produced by an {@link ILinguisticProcessor}.
 * @author Mus'ab Husaini
 */
public abstract class LinguisticObject
	implements IModel {
	
	/**
	 * The {@link ILinguisticProcessor} object that was used to produce this data.
	 */
	protected ILinguisticProcessor processor;
	
	/**
	 * Creates an instance of the {@code LinguisticObject} object.
	 * @param processor the {@link ILinguisticProcessor} that was used to produce this data.
	 */
	protected LinguisticObject(ILinguisticProcessor processor) {
		this.processor = Validate.notNull(processor, "The argument 'processor' must not be null");
	}
}