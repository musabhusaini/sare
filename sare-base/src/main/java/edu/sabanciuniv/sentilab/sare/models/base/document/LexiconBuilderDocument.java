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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sabanciuniv.sentilab.sare.models.base.document;

import javax.persistence.*;

import edu.sabanciuniv.sentilab.core.models.UserInaccessibleModel;

/**
 * A document that shadows a full text document for a lexicon builder.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("lex-builder-doc")
public class LexiconBuilderDocument
	extends FullTextDocument implements UserInaccessibleModel {

	private static final long serialVersionUID = -2107212900717706813L;
	
	/**
	 * Creates an instance of {@link LexiconBuilderDocument}.
	 * @param baseDocument the {@link FullTextDocument} object to base this shadow document on.
	 */
	public LexiconBuilderDocument(FullTextDocument baseDocument) {
		this.setBaseDocument(baseDocument);
		
		if (baseDocument != null) {
			this.weight = baseDocument instanceof IWeightedDocument ?
				((IWeightedDocument)baseDocument).getWeight() : baseDocument.weight;
		}
	}
	
	/**
	 * Creates an instance of {@link LexiconBuilderDocument}.
	 */
	public LexiconBuilderDocument() {
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
	
	public Double getWeight() {
		return this.weight;
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
		
		if (this.getBaseDocument() instanceof FullTextDocument) {
			return ((FullTextDocument)this.getBaseDocument()).getTokenizingOptions();
		}
		
		return null;
	}

	/**
	 * Gets a flag indicating whether this document has been "seen" or not.
	 * @return {@code true} if the document has been seen, {@code false} otherwise.
	 */
	public boolean isSeen() {
		return this.flag;
	}
	
	/**
	 * Sets a flag indicating whether this document was "seen" or not.
	 * @param isSeen {@code true} if the document was seen, {@code false} otherwise.
	 * @return the {@code this} object.
	 */
	public LexiconBuilderDocument setSeen(boolean isSeen) {
		this.flag = isSeen;
		return this;
	}

	@Override
	public FullTextDocument getAccessible() {
		return this.getFullTextDocument();
	}
}