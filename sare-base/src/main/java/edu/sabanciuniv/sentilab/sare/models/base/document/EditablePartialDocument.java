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

import javax.persistence.Entity;

/**
 * A class that represents editable partial documents.
 * @author Mus'ab Husaini
 */
@Entity
public abstract class EditablePartialDocument
	extends PartialTextDocument
	implements EditableDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1135997891869496330L;

	@Override
	public String getContent() {
		return this.title;
	}
	
	/**
	 * Sets the content of this document.
	 * @param content the content to set.
	 * @return the {@code this} object.
	 */
	@Override
	public EditablePartialDocument setContent(String content) {
		this.title = content;
		return this;
	}
}