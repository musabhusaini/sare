package edu.sabanciuniv.sentilab.sare.models.document.base;

import edu.sabanciuniv.sentilab.sare.models.documentStore.base.GenericDocumentStore;

/**
 * The generic base class for documents.
 * @author Mus'ab Husaini
 *
 * @param <T> a circular reference to this type of document; must derive from {@link GenericDocument}.
 */
public abstract class GenericDocument<T extends GenericDocument<T>>
	extends PersistentDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2122612051400547475L;

	@SuppressWarnings("unchecked")
	@Override
	public GenericDocumentStore<T> getStore() {
		try {
			return (GenericDocumentStore<T>)this.store;
		} catch(ClassCastException e) {
			return null;
		}
	}
	
	/**
	 * Sets the document store that this document is stored under.
	 * @param stores the {@link GenericDocumentStore} to store this document under.
	 * @return the {@code this} object.
	 */
	public GenericDocument<T> setStore(GenericDocumentStore<T> store) {
		this.store = store;
		return this;
	}
}