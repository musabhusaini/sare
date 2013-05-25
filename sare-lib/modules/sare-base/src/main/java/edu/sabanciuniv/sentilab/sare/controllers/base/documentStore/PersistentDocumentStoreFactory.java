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
public abstract class PersistentDocumentStoreFactory<T extends PersistentDocumentStore>
		extends PersistentObjectFactory<T> implements IDocumentController {

	protected String title;
	protected String description;
	protected String language;
	protected String ownerId;
	
	/**
	 * Gets the title of the store to be created.
	 * @return the title of the store.
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Sets the title of the store to create.
	 * @param title the title of the store to set.
	 * @return the {@code this} object.
	 */
	public PersistentDocumentStoreFactory<T> setTitle(String title) {
		this.title = title;
		return this;
	}
	
	/**
	 * Gets the description of the store to be created.
	 * @return the description of the store.
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Sets the description of the store to create.
	 * @param description the description of the store to be set.
	 * @return the {@code this} object.
	 */
	public PersistentDocumentStoreFactory<T> setDescription(String description) {
		this.description = description;
		return this;
	}
	
	/**
	 * Gets the language of the store to be created.
	 * @return the language of the store.
	 */
	public String getLanguage() {
		return this.language;
	}
	
	/**
	 * Sets the language of the store to create.
	 * @param language the language of the store to be set.
	 * @return the {@code this} object.
	 */
	public PersistentDocumentStoreFactory<T> setLanguage(String language) {
		this.language = language;
		return this;
	}

	/**
	 * Gets the owner ID that will be set on the target object.
	 * @return the owner ID to be set.
	 */
	public String getOwnerId() {
		return this.ownerId;
	}
	
	/**
	 * Sets the owner ID required to be set fo the target object.
	 * @param ownerId the owne ID to set.
	 * @return the {@code this} object.
	 */
	public PersistentObjectFactory<T> setOwnerId(String ownerId) {
		this.ownerId = ownerId;
		return this;
	}
	
	@Override
	public T create()
		throws IllegalFactoryOptionsException {
		
		T store = super.create();
		
		if (StringUtils.isNotEmpty(this.getOwnerId())) {
			store.setOwnerId(this.getOwnerId());
		}
		
		if (StringUtils.isNotEmpty(this.getTitle())) {
			store.setTitle(this.getTitle());
		}
		
		if (StringUtils.isNotEmpty(this.getDescription())) {
			store.setDescription(this.getDescription());
		}
		
		if (StringUtils.isNotEmpty(this.getLanguage())) {
			store.setLanguage(this.getLanguage());
		}
		
		return store;
	}
}