package edu.sabanciuniv.sentilab.sare.models.base.document;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Maps;

import edu.sabanciuniv.sentilab.utils.CannedMessages;
import edu.sabanciuniv.sentilab.utils.extensions.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticToken;

/**
 * The base class for documents that can be merged into each other.
 * @author Mus'ab Husaini
 */
public abstract class MergableDocument<T extends MergableDocument<T>>
	extends GenericDocument<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1698326581232522413L;

	/**
	 * Gets a token weight map obtained if the given document were to be merged into this document without modifying either of the actual maps.
	 * @param other the {@link TokenizedDocument} object to merge.
	 * @return the {@link Map} object with the tokens and their respective weights after the merge.
	 */
	protected Map<LinguisticToken, Double> getMergedMap(TokenizedDocument other) {
		Validate.notNull(other, CannedMessages.NULL_ARGUMENT, "other");
		
		Map<LinguisticToken, Double> newMap = Maps.newHashMap(this.getTokenWeightMap(true));
		for (Entry<LinguisticToken, Double> entry : other.getTokenWeightMap().entrySet()) {
			MapsExtensions.increment(newMap, entry.getKey(), entry.getValue(), false);
		}
		
		return newMap;
	}

	/**
	 * Gets the total token weight if a given document was merged into this document.
	 * @param other the {@link TokenizedDocument} object to merge.
	 * @return the total token weight resulting from the merge.
	 */
	public double getMergedWeight(TokenizedDocument other) {
		return IterablesExtensions.sum(this.getMergedMap(other).values());
	}
	
	/**
	 * Merge the tokens of a given document into this document.
	 * @param other the {@link TokenizedDocument} object to merge.
	 * @return the {@code this} object.
	 */
	public MergableDocument<T> merge(TokenizedDocument other) {
		Map<LinguisticToken, Double> mergedMap = this.getMergedMap(other);
		for (Entry<LinguisticToken, Double> mergedEntry : mergedMap.entrySet()) {
			this.setTokenWeight(mergedEntry.getKey(), mergedEntry.getValue());
			
			if (other instanceof MergableDocument) {
				((MergableDocument<?>)other).setTokenWeight(mergedEntry.getKey(), 0);
			}
		}
		
		return this;
	}
}