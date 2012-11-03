package edu.sabanciuniv.sentilab.sare.models.base;

import java.util.UUID;

/**
 * An object that has a UUID.
 * @author Mus'ab Husaini
 */
public interface IUniquelyIdentifiable {
	/**
	 * Gets the identifier of this instance.
	 * @return The identifier of this instance.
	 */
	public UUID getIdentifier();
}
