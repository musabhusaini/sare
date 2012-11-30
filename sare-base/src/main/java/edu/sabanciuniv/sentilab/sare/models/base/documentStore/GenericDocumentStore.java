package edu.sabanciuniv.sentilab.sare.models.base.documentStore;

import java.lang.reflect.ParameterizedType;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.base.document.PersistentDocument;

/**
 * The generic class that can store document of a specific type.
 * @author Mus'ab Husaini
 *
 * @param <T> the type of documents to be stored; must derive from {@link PersistentDocument}.
 */
public abstract class GenericDocumentStore<T extends PersistentDocument>
	extends DocumentStoreBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8103895619672871420L;

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<T> getDocuments()  {
		if (this.documents == null) {
			return null;
		}
		
		return Iterables.filter(this.documents,
			(Class<T>)((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
	}

	/**
	 * Sets the documents in this store.
	 * @param documents the {@link Iterable} of documents to set.
	 * @return the {@code this} object.
	 */
	public GenericDocumentStore<T> setDocuments(Iterable<T> documents) {
		if (documents == null) {
			this.documents = null;
		} else {
			this.documents = Lists.newArrayList(Iterables.filter(documents, PersistentDocument.class));
			
			for (T document : documents) {
				this.addReferer(document);
			}
		}
		return this;
	}
	
	/**
	 * Adds a document to this store.
	 * @param document the {@code T} type document to add.
	 * @return the {@code this} object.
	 */
	public GenericDocumentStore<T> addDocument(T document) {
		if (this.documents == null) {
			this.documents = Lists.newArrayList();
		}
		
		this.documents.add(document);
		this.refererObjects.add(document);
		return this;
	}
	
	/**
	 * Removes a document from this store.
	 * @param document the {@code T} type document to remove.
	 * @return {@code true} if an element was removed as a result of this call. 
	 */
	public boolean removeDocument(T document) {
		if (this.documents == null) {
			return false;
		}
		
		this.refererObjects.remove(document);
		return this.documents.remove(document);
	}
}