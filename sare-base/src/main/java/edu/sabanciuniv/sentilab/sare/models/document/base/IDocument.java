package edu.sabanciuniv.sentilab.sare.models.document.base;

import edu.sabanciuniv.sentilab.sare.models.base.IModel;
import edu.sabanciuniv.sentilab.sare.models.documentStore.base.IDocumentStore;

/**
 * The base interface for all documents.
 * @author Mus'ab Husaini
 */
public interface IDocument
	extends IModel {
	
	/**
	 * Gets the textual content of this document.
	 * @return the textual content of this document.
	 */
	public String getContent();
	
	/**
	 * Gets the document store that this document is stored under.
	 * @return the {@link IDocumentStore} object that this document is stored under.
	 */
	public IDocumentStore getStore();
}
