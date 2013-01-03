package models.documentStore;

import edu.sabanciuniv.sentilab.sare.models.base.documentStore.DocumentCorpus;

public class DocumentCorpusModel
	extends PersistentDocumentStoreModel {
	
	public DocumentCorpusModel(DocumentCorpus corpus) {
		super(corpus);
	}
	
	public DocumentCorpusModel() {
		this(null);
	}
}
