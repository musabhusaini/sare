package models.document;

import edu.sabanciuniv.sentilab.sare.models.opinion.OpinionDocument;

public class OpinionDocumentView
	extends PersistentDocumentView {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4477492768049749815L;

	public Double polarity;
	
	public OpinionDocumentView(OpinionDocument document) {
		super(document);
		
		if (document != null) {
			this.polarity = document.getPolarity();
		}
	}
	
	public OpinionDocumentView() {
		this(null);
	}
}
