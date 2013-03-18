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

import controllers.base.SareTransactionalAction;

import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexiconFactoryOptions;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;

public class AspectLexiconFactoryOptionsModel
	extends PersistentDocumentStoreFactoryOptionsModel {
	
	public PersistentDocumentStoreModel baseStore;
	
	public AspectLexiconFactoryOptionsModel(AspectLexiconFactoryOptions options) {
		super(options);
		
		if (options != null) {
			if (options.getBaseStore() != null) {
				this.baseStore = (PersistentDocumentStoreModel)createViewModel(options.getBaseStore());
			}
		}
	}
	
	public AspectLexiconFactoryOptionsModel() {
		this(null);
	}
	
	public AspectLexiconFactoryOptions toFactoryOptions() {
		AspectLexiconFactoryOptions options = (AspectLexiconFactoryOptions)new AspectLexiconFactoryOptions()
			.setTitle(this.title)
			.setDescription(this.description);
		
		if (this.baseStore != null && SareTransactionalAction.em() != null) {
			options.setBaseStore(SareTransactionalAction.fetchResource(this.baseStore.id, PersistentDocumentStore.class));
		}
		
		return options;
	}
}