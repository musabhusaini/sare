package edu.sabanciuniv.sentilab.sare.models.base.documentStore;

import java.lang.reflect.ParameterizedType;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * The generic class that can store document of a specific type.
 * @author Mus'ab Husaini
 *
 * @param <T> the type of documents to be stored; must derive from {@link PersistentDocument}.
 */
public abstract class GenericDocumentStore<T extends GenericDocument<T>>
	extends PersistentDocumentStore {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8103895619672871420L;

	@SuppressWarnings("unchecked")
	@Override
	public Iterable<T> getDocuments()  {
		if (this.documents == null) {
			return Lists.newArrayList();
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
			if (this.documents != null) {
				for (T document : Lists.newArrayList(this.getDocuments())) {
					this.removeDocument(document);
				}
			}
			
			this.documents = null;
		} else {
			this.setDocuments(null);
			
			for (T document : documents) {
				this.addDocument(document);
			}
		}
		return this;
	}
	
	/**
	 * Gets a boolean flag indicating whether the provided document is in this store or not.
	 * @param document the {@code T} object to look for.
	 * @return {@code true} if the document is contained in this store, {@code false} otherwise.
	 */
	public boolean hasDocument(T document) {
		return Iterables.contains(this.getDocuments(), document);
	}
	
	/**
	 * Adds a document to this store.
	 * @param document the {@code T} type document to add.
	 * @return the {@code this} object.
	 */
	public GenericDocumentStore<T> addDocument(T document) {
		Validate.notNull(document, CannedMessages.NULL_ARGUMENT, "document");
		
		if (this.documents == null) {
			this.documents = Lists.newArrayList();
		}
		
		if (document.getStore() != this) {
			document.setStore(this);
		}
		
		if (!this.documents.contains(document)) {
			this.documents.add(document);
		}
		
		this.addReferer(document);
		return this;
	}
	
	/**
	 * Removes a document from this store.
	 * @param document the {@code T} type document to remove.
	 * @return {@code true} if an element was removed as a result of this call. 
	 */
	public boolean removeDocument(T document) {
		if (document == null) {
			return false;
		}
		
		if (document.getStore() == this) {
			document.setStore(null);
		}
		
		this.removeReferer(document);
		
		if (this.documents == null) {
			return false;
		}
		
		if (this.documents.contains(document)) {
			return this.documents.remove(document);
		} else {
			return false;
		}
	}
}