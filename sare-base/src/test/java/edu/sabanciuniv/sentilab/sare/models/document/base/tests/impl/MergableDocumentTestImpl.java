package edu.sabanciuniv.sentilab.sare.models.document.base.tests.impl;

import edu.sabanciuniv.sentilab.sare.models.document.base.MergableDocument;

public class MergableDocumentTestImpl extends MergableDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String content;
	
	@Override
	public String getContent() {
		return this.content;
	}
	
	public MergableDocumentTestImpl setContent(String content) {
		this.content = content;
		return this;
	}
}