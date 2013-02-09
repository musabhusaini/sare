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

package edu.sabanciuniv.sentilab.sare.controllers.base.documentStore;

import org.apache.commons.lang3.StringUtils;

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.base.PersistentObjectFactory;
import edu.sabanciuniv.sentilab.sare.controllers.base.document.IDocumentController;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;

/**
 * The base class for all factories that create {@link PersistentDocumentStore} instances.
 * @author Mus'ab Husaini
 * @param <T> the type of object that will be created; must derive from {@link PersistentDocumentStore}.
 * @param <O> the type of options that will be used to create the objects; must derive from {@link PersistentDocumentStoreFactoryOptions}.
 */
public abstract class PersistentDocumentStoreFactory<T extends PersistentDocumentStore, O extends PersistentDocumentStoreFactoryOptions<T>>
	extends PersistentObjectFactory<T, O> implements IDocumentController {
	
	@Override
	public T create(O options)
		throws IllegalFactoryOptionsException {
		
		T store = super.create(options);
		
		if (StringUtils.isNotEmpty(options.getOwnerId())) {
			store.setOwnerId(options.getOwnerId());
		}
		
		if (StringUtils.isNotEmpty(options.getTitle())) {
			store.setTitle(options.getTitle());
		}
		
		if (StringUtils.isNotEmpty(options.getDescription())) {
			store.setDescription(options.getDescription());
		}
		
		if (StringUtils.isNotEmpty(options.getLanguage())) {
			store.setLanguage(options.getLanguage());
		}
		
		return store;
	}
}