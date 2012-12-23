package models.documentStore;

import models.base.ViewModel;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;

public class PersistentDocumentStoreView
	extends ViewModel {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String id;
	public String title;
	public String description;
	public String language;
	public int size;
	
	public PersistentDocumentStoreView(PersistentDocumentStore documentStore) {
		super(documentStore);
		
		if (documentStore != null) {
			this.id = documentStore.getIdentifier().toString();
			this.title = documentStore.getTitle();
			this.description = documentStore.getDescription();
			this.language = documentStore.getLanguage();
			this.size = Iterables.size(documentStore.getDocuments());
		}
	}
	
	public PersistentDocumentStoreView() {
		this(null);
	}
}