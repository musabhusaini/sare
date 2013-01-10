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

package edu.sabanciuniv.sentilab.sare.models.base.documentStore;

import edu.sabanciuniv.sentilab.sare.models.base.PersistentObjectFactoryOptions;

/**
 * The base class for all factory options for factories that create {@link PersistentDocumentStore} instances.
 * @author Mus'ab Husaini
 * @param <T> the type of objects that will be created; must extend {@link PersistentDocumentStore}.
 */
public abstract class PersistentDocumentStoreFactoryOptions<T extends PersistentDocumentStore>
	extends PersistentObjectFactoryOptions<T> {

	protected String ownerId;
	
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
	public PersistentObjectFactoryOptions<T> setOwnerId(String ownerId) {
		this.ownerId = ownerId;
		return this;
	}
}