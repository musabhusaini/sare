package edu.sabanciuniv.sentilab.sare.models.opinion;

import javax.persistence.*;

import edu.sabanciuniv.sentilab.sare.models.base.documentStore.GenericDocumentStore;

@Entity
@DiscriminatorValue("opinion-corpus")
public class OpinionCorpus
	extends GenericDocumentStore<OpinionDocument> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1879400925531543833L;
}