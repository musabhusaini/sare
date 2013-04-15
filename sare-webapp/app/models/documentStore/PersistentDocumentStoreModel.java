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

package models.documentStore;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.Validate;

import models.PersistentObjectModel;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.PersistentDocumentStoreController;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;

public class PersistentDocumentStoreModel
	extends PersistentObjectModel {
	
	public String title;
	public String description;
	public String language;
	public Long size;
	
	public PersistentDocumentStoreModel(PersistentDocumentStore documentStore) {
		super(documentStore);
		
		if (documentStore != null) {
			this.title = documentStore.getTitle();
			this.description = documentStore.getDescription();
			this.language = documentStore.getLanguage();
		}
	}
	
	public PersistentDocumentStoreModel() {
		this(null);
	}
	
	public long populateSize(EntityManager em, PersistentDocumentStore store) {
		Validate.notNull(em);
		return this.size = store == null ? 0 : new PersistentDocumentStoreController().getSize(em, store);
	}
	
	@Override
	public String toString() {
		return this.title;
	}
}