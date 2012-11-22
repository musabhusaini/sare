package edu.sabanciuniv.sentilab.sare.models.documentStore;

import java.util.Collections;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import edu.sabanciuniv.sentilab.sare.models.document.SetCoverDocument;
import edu.sabanciuniv.sentilab.sare.models.documentStore.base.GenericDocumentStore;
import edu.sabanciuniv.sentilab.sare.models.documentStore.base.DocumentStoreBase;

/**
 * The class for a document set cover.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("SetCover")
public class DocumentSetCover
	extends GenericDocumentStore<SetCoverDocument> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6709686005135631465L;
	
	/**
	 * Creates a new instance of the {@link DocumentSetCover} class.
	 */
	public DocumentSetCover() {
		//
	}
	
	/**
	 * Creates a new instance of the {@link DocumentSetCover} class.
	 * @param baseStore the base {@link DocumentStoreBase} object.
	 */
	public DocumentSetCover(DocumentStoreBase baseStore) {
		this();
		this.setBaseStore(baseStore);
	}
	
	/**
	 * Replaces a given document with another document.
	 * @param original the original document to replace.
	 * @param replacement the new document to replace with.
	 * @return {@code true} if the document was replaced.
	 */
	public boolean replaceDocuments(SetCoverDocument original, SetCoverDocument replacement) {
		return Collections.replaceAll(this.documents, original, replacement);
	}
	
	@Override
	public String getTitle() {
		return super.getTitle() == null && this.getBaseStore() != null ? this.getBaseStore().getTitle() : super.getTitle();
	}
	
	@Override
	public String getLanguage() {
		return super.getLanguage() == null && this.getBaseStore() != null ? this.getBaseStore().getLanguage() : super.getLanguage();
	}
	
	@Override
	public String getDescription() {
		return super.getDescription() == null && this.getBaseStore() != null ? this.getBaseStore().getDescription() : super.getDescription();
	}
}