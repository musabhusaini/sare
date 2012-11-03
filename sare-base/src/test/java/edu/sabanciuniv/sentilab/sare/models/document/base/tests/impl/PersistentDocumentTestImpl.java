package edu.sabanciuniv.sentilab.sare.models.document.base.tests.impl;

import edu.sabanciuniv.sentilab.sare.models.document.base.PersistentDocument;

public class PersistentDocumentTestImpl extends PersistentDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String content;
	
	@Override
	public String getContent() {
		return this.content;
	}

	public PersistentDocumentTestImpl setContent(String content) {
		this.content = content;
		return this;
	}
}
