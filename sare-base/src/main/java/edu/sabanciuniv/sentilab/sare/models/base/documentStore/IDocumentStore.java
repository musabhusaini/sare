package edu.sabanciuniv.sentilab.sare.models.base.documentStore;

import edu.sabanciuniv.sentilab.core.models.IModel;
import edu.sabanciuniv.sentilab.sare.models.base.document.IDocument;

/**
 * The base interface for all document stores.
 * @author Mus'ab Husaini
 */
public interface IDocumentStore
	extends IModel {

	/**
	 * Gets the title of this store.
	 * @return the title of this store.
	 */
	public String getTitle();
	
	/**
	 * Gets the language that this store's documents are in.
	 * @return the language of this store.
	 */
	public String getLanguage();
	
	/**
	 * Gets the description of this store.
	 * @return
	 */
	public String getDescription();
	
	/**
	 * Gets the documents in this store.
	 * @return the {@link Iterable} containing {@link IDocument} objects stored in this store.
	 */
	public Iterable<? extends IDocument> getDocuments();
}
