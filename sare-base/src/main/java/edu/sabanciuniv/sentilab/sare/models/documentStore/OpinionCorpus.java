package edu.sabanciuniv.sentilab.sare.models.documentStore;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import edu.sabanciuniv.sentilab.sare.models.documentStore.base.DocumentStoreBase;

@Entity
@DiscriminatorValue("Opinion")
public class OpinionCorpus
	extends DocumentStoreBase {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1879400925531543833L;
}