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
 * The basic unit of a lexicon.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("lex-doc")
public class LexiconDocument
	extends EditablePartialDocument {

	private static final long serialVersionUID = -4658589725445481595L;

	@Override
	public LexiconDocument setContent(String content) {
		super.setContent(content == null ? content : content.toLowerCase().trim());
		return this;
	}
}