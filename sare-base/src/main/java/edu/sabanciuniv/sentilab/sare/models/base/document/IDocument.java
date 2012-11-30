package edu.sabanciuniv.sentilab.sare.models.base.document;

import edu.sabanciuniv.sentilab.core.models.IModel;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.IDocumentStore;

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
