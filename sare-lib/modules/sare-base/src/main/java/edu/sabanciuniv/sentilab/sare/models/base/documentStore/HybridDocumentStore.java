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

import javax.persistence.Entity;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.collect.*;

/**
 * Base class for stores that combine two or more stores, possibly of various types.
 * @author Mus'ab Husaini
 */
@Entity
public abstract class HybridDocumentStore
		extends PersistentDocumentStore implements IDerivedStore {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3075937879542916348L;
	
	/**
	 * Creates a new instance of the {@link HybridDocumentStore}.
	 * @param stores the {@link PersistentDocumentStore} objects this hybrid is based on.
	 */
	public HybridDocumentStore(PersistentDocumentStore... stores) {
		if (stores != null) {
			for (PersistentDocumentStore store : stores) {
				this.addReference(store);
			}
		}
	}
	
	/**
	 * Creates a new instance of the {@link HybridDocumentStore}.
	 * @param stores the {@link PersistentDocumentStore} objects this hybrid is based on.
	 */
	public HybridDocumentStore(Iterable<PersistentDocumentStore> stores) {
		this(Iterables.toArray(ObjectUtils.defaultIfNull(stores, Lists.<PersistentDocumentStore>newArrayList()),
			PersistentDocumentStore.class));
	}
	
	/**
	 * Creates a new instance of the {@link HybridDocumentStore}.
	 */
	public HybridDocumentStore() {
		this(new PersistentDocumentStore[]{});
	}
	
	/**
	 * Gets all the base stores of a given type.
	 * @param clazz the type of base stores to find.
	 * @return an {@link Iterable} containing all the base stores of the given type.
	 */
	public <T extends PersistentDocumentStore> Iterable<T> getBaseStores(Class<T> clazz) {
		return Iterables.filter(this.referencedObjects, clazz);
	}
}