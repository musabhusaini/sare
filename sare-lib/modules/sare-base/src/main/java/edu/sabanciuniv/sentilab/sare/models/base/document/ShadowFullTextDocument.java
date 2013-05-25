/*
 * Sentilab SARE: a Sentiment Analysis Research Environment
 * Copyright (C) 2013 Sabanci University Sentilab
 * http://sentilab.sabanciuniv.edu
 * 
 * This file is part of SARE.
 * 
 * SARE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * SARE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sabanciuniv.sentilab.sare.models.base.document;

/**
 * A full text document that does not have its own content, but uses content from another document.
 * @author Mus'ab Husaini
 */
public abstract class ShadowFullTextDocument
		extends FullTextDocument {

	private static final long serialVersionUID = 7595973847180068167L;

	/**
	 * Creates an instance of {@link ShadowFullTextDocument}.
	 * @param baseDocument the {@link FullTextDocument} to shadow.
	 */
	public ShadowFullTextDocument(FullTextDocument baseDocument) {
		this.setBaseDocument(baseDocument);
	}
	
	/**
	 * Creates an instance of {@link ShadowFullTextDocument}.
	 */
	public ShadowFullTextDocument() {
		this(null);
	}
	
	/**
	 * Gets the underlying full text document.
	 * @return the underlying {@link FullTextDocument}.
	 */
	public FullTextDocument getFullTextDocument() {
		if (this.getBaseDocument() instanceof FullTextDocument) {
			return (FullTextDocument)this.getBaseDocument();
		}
		return null;
	}
	
	@Override
	public String getContent() {
		return this.getBaseDocument() == null ? null : this.getBaseDocument().getContent();
	}
	
	@Override
	public TokenizingOptions getTokenizingOptions() {
		if (super.getTokenizingOptions() != null) {
			return super.getTokenizingOptions();
		}
		
		if (this.getFullTextDocument() != null) {
			return this.getFullTextDocument().getTokenizingOptions();
		}
		
		return null;
	}
}