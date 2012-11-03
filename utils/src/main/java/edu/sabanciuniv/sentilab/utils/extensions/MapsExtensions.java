package edu.sabanciuniv.sentilab.utils.extensions;

import java.util.Map;

import org.apache.commons.lang3.Validate;

import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A class with some static extensions for {@link Map} types.
 * @author Mus'ab Husaini
 */
public abstract class MapsExtensions {
	
	private MapsExtensions() {
		// prevent instantiation.
	}
	
	/**
	 * Increments the value of a given key in a map.
	 * @param map the {@link Map} to use.
	 * @param key the key to look for.
	 * @param by the value to increment by.
	 * @param add a {@link Boolean} flag indicating whether to add the key if it doesn't exist or not.
	 * @return the map that was passed in.
	 */
	public static <K> Map<K, Double> increment(Map<K, Double> map, K key, Double by, boolean add) {
		Validate.notNull(map, CannedMessages.NULL_ARGUMENT, "map");
		
		Double value = map.get(key);
		if (value == null && add) {
			value = 0.0;
		}
		
		if (value != null) {
			map.put(key, value + by);
		}
		
		return map;
	}
	
	/**
	 * Increments the value of a given key in a map, adding any key that doesn't exist.
	 * @param map the {@link Map} to use.
	 * @param key the key to look for.
	 * @param by the value to increment by.
	 * @return the map that was passed in.
	 */
	public static <K> Map<K, Double> increment(Map<K, Double> map, K key, Double by) {
		return increment(map, key, by, true);
	}
	
	/**
	 * Increments the value of a given key in a map by {@code 1}, adding any key that doesn't exist.
	 * @param map the {@link Map} to use.
	 * @param key the key to look for.
	 * @param by the value to increment by.
	 * @return the map that was passed in.
	 */
	public static <K> Map<K, Double> increment(Map<K, Double> map, K key) {
		return increment(map, key, 1.0);
	}
	
	/**
	 * Increments the value of a given key in a map by {@code 1}.
	 * @param map the {@link Map} to use.
	 * @param key the key to look for.
	 * @param add a {@link Boolean} flag indicating whether to add the key if it doesn't exist or not.
	 * @return the map that was passed in.
	 */
	public static <K> Map<K, Double> increment(Map<K, Double> map, K key, boolean add) {
		return increment(map, key, 1.0, add);
	}
}