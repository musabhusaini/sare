package edu.sabanciuniv.sentilab.sare.models.documentStore;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import edu.sabanciuniv.sentilab.sare.models.document.OpinionDocument;
import edu.sabanciuniv.sentilab.sare.models.documentStore.base.GenericDocumentStore;

@Entity
@DiscriminatorValue("Opinion")
public class OpinionCorpus
	extends GenericDocumentStore<OpinionDocument> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1879400925531543833L;
}