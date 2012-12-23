package models.document;

import edu.sabanciuniv.sentilab.sare.models.base.document.PersistentDocument;
import models.base.ViewModel;

public class PersistentDocumentView extends ViewModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4107151672582395851L;

	public String content;
	
	public PersistentDocumentView(PersistentDocument document) {
		super(document);
		
		if (document != null) { 
			this.content = document.getContent();
		}
	}
	
	public PersistentDocumentView() {
		this(null);
	}
}
