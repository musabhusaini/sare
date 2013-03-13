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

package models.documentStore;

import javax.persistence.EntityManager;

import models.PersistentObjectModel;

import com.google.common.collect.Iterables;

import controllers.base.SareTransactionalAction;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.PersistentDocumentStoreController;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;

public class PersistentDocumentStoreModel
	extends PersistentObjectModel {
	
	public String title;
	public String description;
	public String language;
	public long size;
	
	public PersistentDocumentStoreModel(PersistentDocumentStore documentStore) {
		super(documentStore);
		
		if (documentStore != null) {
			this.title = documentStore.getTitle();
			this.description = documentStore.getDescription();
			this.language = documentStore.getLanguage();
			EntityManager em = SareTransactionalAction.em();
			if (em != null) {
				// TODO: perhaps there is a better way to do this than to put controller code in the view model.
				this.size = new PersistentDocumentStoreController().getSize(em, documentStore);
			} else {
				this.size = Iterables.size(documentStore.getDocuments());
			}
		}
	}
	
	public PersistentDocumentStoreModel() {
		this(null);
	}
}