package edu.sabanciuniv.sentilab.sare.models.base.document;

import edu.sabanciuniv.sentilab.sare.models.base.documentStore.GenericDocumentStore;

/**
 * The generic base class for documents.
 * @author Mus'ab Husaini
 *
 * @param <T> a circular reference to this type of document; must derive from {@link GenericDocument}.
 */
public abstract class GenericDocument<T extends GenericDocument<T>>
	extends TokenizedDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2122612051400547475L;

	@SuppressWarnings("unchecked")
	@Override
	public GenericDocumentStore<T> getStore() {
		if (this.store == null) {
			return null;
		}
		
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
		this.addReference(store);
		return this;
	}
}