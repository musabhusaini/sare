package edu.sabanciuniv.sentilab.sare.models.setcover;

import java.util.Collections;

import javax.persistence.*;

import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;

/**
 * The class for a document set cover.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("setcover-corpus")
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
	 * @param baseStore the base {@link PersistentDocumentStore} object.
	 */
	public DocumentSetCover(PersistentDocumentStore baseStore) {
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
		if (this.documents == null) {
			return false;
		}
		
		boolean replaced = Collections.replaceAll(this.documents, original, replacement);
		if (replaced) {
			replacement.setStore(this);
		}
		
		return replaced;
	}

	/**
	 * Gets the total weight of this set cover.
	 * @return the weight of the set cover.
	 */
	public double totalWeight() {
		double weight = 0;
		for (SetCoverDocument document : this.getDocuments()) {
			weight += document.getWeight();
		}
		
		return weight;
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