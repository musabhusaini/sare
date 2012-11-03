package edu.sabanciuniv.sentilab.utils.extensions;

import org.apache.commons.lang3.Validate;

import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A class with some static extensions for {@link Iterable} types.
 * @author Mus'ab Husaini
 */
public abstract class IterablesExtensions {
	
	private IterablesExtensions() {
		// prevent instantiation.
	}
	
	/**
	 * Calculates the sum of a given iterable of {@link Number} type items.
	 * @param iterable the {@link Iterable} of {@link Number} type items whose sum is desired.
	 * @return the {@link Double} value with the sum.
	 */
	public static <I extends Number> double sum(Iterable<I> iterable) {
		Validate.notNull(iterable, CannedMessages.NULL_ARGUMENT, "iterable");
		
		double s = 0;
		for (I item : iterable) {
			s += item.doubleValue();
		}
		
		return s;
	}
}