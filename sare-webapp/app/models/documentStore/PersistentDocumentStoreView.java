package models.documentStore;

import javax.persistence.EntityManager;

import models.base.ViewModel;

import com.google.common.collect.Iterables;

import controllers.base.SareTransactionalAction;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.PersistentDocumentStoreController;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;

public class PersistentDocumentStoreView
	extends ViewModel {
	
	public String id;
	public String title;
	public String description;
	public String language;
	public long size;
	
	public PersistentDocumentStoreView(PersistentDocumentStore documentStore) {
		super(documentStore);
		
		if (documentStore != null) {
			this.id = documentStore.getIdentifier().toString();
			this.title = documentStore.getTitle();
			this.description = documentStore.getDescription();
			this.language = documentStore.getLanguage();
			EntityManager em = SareTransactionalAction.em();
			if (em != null) {
				// TODO: perhaps there is a better way to do this than to put controller code in the view model.
				this.size = new PersistentDocumentStoreController().getSize(em, this.id);
			} else {
				this.size = Iterables.size(documentStore.getDocuments());
			}
		}
	}
	
	public PersistentDocumentStoreView() {
		this(null);
	}
}