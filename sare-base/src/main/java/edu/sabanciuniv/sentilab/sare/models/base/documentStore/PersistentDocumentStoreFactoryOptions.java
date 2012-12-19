package edu.sabanciuniv.sentilab.sare.models.base.documentStore;

import edu.sabanciuniv.sentilab.sare.models.base.PersistentObjectFactoryOptions;

/**
 * The base class for all factory options for factories that create {@link PersistentDocumentStore} instances.
 * @author Mus'ab Husaini
 * @param <T> the type of objects that will be created; must extend {@link PersistentDocumentStore}.
 */
public abstract class PersistentDocumentStoreFactoryOptions<T extends PersistentDocumentStore> extends
		PersistentObjectFactoryOptions<T> {

	protected String ownerId;
	
	/**
	 * Gets the owner ID that will be set on the target object.
	 * @return the owner ID to be set.
	 */
	public String getOwnerId() {
		return this.ownerId;
	}
	
	/**
	 * Sets the owner ID required to be set fo the target object.
	 * @param ownerId the owne ID to set.
	 * @return the {@code this} object.
	 */
	public PersistentObjectFactoryOptions<T> setOwnerId(String ownerId) {
		this.ownerId = ownerId;
		return this;
	}
}