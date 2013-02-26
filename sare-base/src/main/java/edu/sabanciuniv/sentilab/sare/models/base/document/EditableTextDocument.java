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

/**
 * A class for documents containing editable textual content.
 * @author Mus'ab Husaini
 */
@Entity
public abstract class EditableTextDocument
	extends FullTextDocument
	implements EditableDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5392373511223975393L;

	@Column(name="title")
	private String content;
	
	@Override
	public String getContent() {
		return this.content;
	}
	
	/**
	 * Sets the content of this document.
	 * @param content the content to set.
	 * @return the {@code this} object.
	 */
	@Override
	public EditableTextDocument setContent(String content) {
		this.content = content;
		this.weight = content != null ? (double)this.getContent().length() : 0.0;
		return this;
	}
}