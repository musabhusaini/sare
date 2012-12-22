package models.documentStore;

import org.springframework.util.ClassUtils;

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
	
	public PersistentDocumentStoreView() {
		this(null);
	}
	
	public PersistentDocumentStoreView(PersistentDocumentStore documentStore) {
		this.type = ClassUtils.getShortName(documentStore.getClass());
		
		if (documentStore != null) {
			this.id = documentStore.getIdentifier().toString();
			this.title = documentStore.getTitle();
			this.description = documentStore.getDescription();
			this.language = documentStore.getLanguage();
			this.size = Iterables.size(documentStore.getDocuments());
		}
	}
}