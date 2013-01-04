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

package models.document;

import edu.sabanciuniv.sentilab.sare.models.base.document.PersistentDocument;
import models.PersistentObjectModel;

public class PersistentDocumentModel
	extends PersistentObjectModel {

	public String content;
	
	public PersistentDocumentModel(PersistentDocument document) {
		super(document);
		
		if (document != null) { 
			this.content = document.getContent();
		}
	}
	
	public PersistentDocumentModel() {
		this(null);
	}
}