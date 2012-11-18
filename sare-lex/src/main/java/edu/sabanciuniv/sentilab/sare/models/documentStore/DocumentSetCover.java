package edu.sabanciuniv.sentilab.sare.models.documentStore;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import edu.sabanciuniv.sentilab.sare.models.document.SetCoverDocument;
import edu.sabanciuniv.sentilab.sare.models.documentStore.base.GenericDocumentStore;

@Entity
@DiscriminatorValue("SetCover")
public class DocumentSetCover
	extends GenericDocumentStore<SetCoverDocument> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6709686005135631465L;
	
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