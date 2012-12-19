package edu.sabanciuniv.sentilab.sare.models.base;

import com.google.gson.*;

import edu.sabanciuniv.sentilab.core.models.factory.IFactoryOptions;

/**
 * The base class for all factory options for factories that create {@link PersistentObject} instances.
 * @author Mus'ab Husaini
 * @param <T> the type of objects that will be created; must extend {@link PersistentObject}.
 */
public abstract class PersistentObjectFactoryOptions<T extends PersistentObject>
	implements IFactoryOptions<T> {

	protected String otherData;

	/**
	 * Gets any other data to be attached to the target object.
	 * @return the {@link String} representing other data.
	 */
	public String getOtherData() {
		return this.otherData;
	}

	/**
	 * Sets any other data to attach to the target object.
	 * @param otherData the {@link String} representing any other data (must be valid JSON).
	 * @return the {@code this} object.
	 * @throws IllegalArgumentException when the argument cannot be parsed as a JSON object.
	 */
	public PersistentObjectFactoryOptions<T> setOtherData(String otherData) {
		if (otherData != null) {
			try {
				if (!new JsonParser().parse(otherData).isJsonObject()) {
					throw new JsonSyntaxException("");
				}
			} catch (JsonSyntaxException e) {
				throw new IllegalArgumentException("argument 'otherData' must be valid JSON", e);
			}
		}
		
		this.otherData = otherData;
		return this;
	}
}