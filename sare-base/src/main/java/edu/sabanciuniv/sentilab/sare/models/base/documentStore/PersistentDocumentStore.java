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

package edu.sabanciuniv.sentilab.sare.models.base.documentStore;

import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.base.*;
import edu.sabanciuniv.sentilab.sare.models.base.document.PersistentDocument;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * The base class for all objects that can store documents.
 * @author Mus'ab Husaini
 */
@Entity
public abstract class PersistentDocumentStore
	extends PersistentObject
	implements IDocumentStore {

	/**
	 * 
	 */
	private static final long serialVersionUID = 24809167105583854L;
	
	@ManyToOne
	@JoinColumn(name = "base_store_id")
	private PersistentDocumentStore baseStore;
	
	@OneToMany(mappedBy = "baseStore")
	private Collection<PersistentDocumentStore> derivedStores;
	
	@Column(name="owner_id")
	private String ownerId;
	
	/**
	 * The documents stored under this store. Derived classes are responsible for maintaining this relationship.
	 */
	@OneToMany(mappedBy = "store")
	protected List<PersistentDocument> documents;

	protected PersistentDocumentStore addDerivedStore(PersistentDocumentStore derivedStore) {
		Validate.notNull(derivedStore, CannedMessages.NULL_ARGUMENT, "derivedStore");
		
		if (!this.hasDerivedStore(derivedStore)) {
			this.derivedStores.add(derivedStore);
		}
		
		if (derivedStore.getBaseStore() != this) {
			derivedStore.setBaseStore(this);
		}
		
		return this;
	}
	
	protected boolean removeDerivedStore(PersistentDocumentStore derivedStore) {
		Validate.notNull(derivedStore, CannedMessages.NULL_ARGUMENT, "derivedStore");
		
		boolean result = false;
		if (this.hasDerivedStore(derivedStore)) {
			result = this.derivedStores.remove(derivedStore);
			
			if (derivedStore.getBaseStore() == this) {
				derivedStore.setBaseStore(null);
			}
		}
		
		return result;
	}
	
	/**
	 * Gets the base store for this store.
	 * @return the {@link PersistentDocumentStore} object that is the base store for this store.
	 */
	public PersistentDocumentStore getBaseStore() {
		return baseStore;
	}

	/**
	 * Sets the base store for this store.
	 * @param baseStore the base store.
	 * @return the {@code this} object.
	 */
	public PersistentDocumentStore setBaseStore(PersistentDocumentStore baseStore) {
		// make sure we don't end up with a cyclical tree.
		PersistentDocumentStore chainBase = baseStore;
		while (chainBase != null) {
			if (chainBase.getIdentifier().equals(this.getIdentifier())) {
				throw new IllegalArgumentException("derived document tree must be acyclic");
			}
			chainBase = chainBase.getBaseStore();
		}
		
		PersistentDocumentStore prevBase = this.getBaseStore();
		
		this.baseStore = baseStore;
		this.addReference(baseStore);
		
		if (prevBase != null) {
			this.removeReference(prevBase);
			prevBase.removeDerivedStore(this);
		}
		
		if (this.baseStore != null && !this.baseStore.hasDerivedStore(this)) {
			this.baseStore.addDerivedStore(this);
		}
		
		return this;
	}

	/**
	 * Gets the derived stores of this store.
	 * @return the {@link Iterable} of {@link PersistentDocumentStore} objects that derive from this store.
	 */
	public Iterable<PersistentDocumentStore> getDerivedStores() {
		if (this.derivedStores == null) {
			this.derivedStores = Lists.newArrayList();
		}
		
		return Collections.unmodifiableCollection(this.derivedStores);
	}
	
	/**
	 * Gets a flag indicating whether the provided store is in the list of derived stores for this store.
	 * @param derivedStore the {@link PersistentDocumentStore} object to look for.
	 * @return {@code true} if the provided store is in the list of derived stores, {@code false} otherwise.
	 */
	public boolean hasDerivedStore(PersistentDocumentStore derivedStore) {
		return Iterables.contains(this.getDerivedStores(), derivedStore);
	}

	@Override
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Sets the title of this store.
	 * @param title the title of this store.
	 * @return the {@code this} object.
	 */
	public PersistentDocumentStore setTitle(String title) {
		this.title = title;
		return this;
	}
	
	@Override
	public String getLanguage() {
		return this.getProperty("language", String.class);
	}

	/**
	 * Sets the language that this store's documents are in.
	 * @param language the language of this store.
	 * @return the {@code this} object.
	 */
	public PersistentDocumentStore setLanguage(String language) {
		this.setProperty("language", language);
		return this;
	}

	@Override
	public String getDescription() {
		return this.getProperty("description", String.class);
	}
	
	/**
	 * Sets the description of this store.
	 * @param description the description of this store.
	 * @return the {@code this} object.
	 */
	public PersistentDocumentStore setDescription(String description) {
		this.setProperty("description", description);
		return this;
	}
	
	/**
	 * Sets the ID of the entity that owns this store.
	 * @param ownerId the ID of the owner.
	 * @return the {@code this} object.
	 */
	public PersistentDocumentStore setOwnerId(String ownerId) {
		this.ownerId = ownerId;
		return this;
	}
	
	@Override
	public String getOwnerId() { 
		return this.ownerId;
	}
	
	@Override
	public Iterable<PersistentDocument> getDocuments()  {
		// must not return null.
		if (this.documents == null) {
			return Lists.newArrayList();
		}
		
		return Collections.unmodifiableList(this.documents);
	}

	/**
	 * Sets the documents in this store.
	 * @param documents the {@link Iterable} of documents to set.
	 * @return the {@code this} object.
	 */
	public PersistentDocumentStore setDocuments(Iterable<? extends PersistentDocument> documents) {
		if (documents == null) {
			if (Iterables.size(this.getDocuments()) != 0) {
				List<PersistentDocument> tmpDocuments = Lists.newArrayList(this.getDocuments());
				for (PersistentDocument document : tmpDocuments) {
					this.removeDocument(document);
				}
			}
			
			this.documents = null;
		} else {
			this.setDocuments(null);
			
			for (PersistentDocument document : documents) {
				this.addDocument(document);
			}
		}
		return this;
	}
	
	/**
	 * Gets a boolean flag indicating whether the provided document is in this store or not.
	 * @param document the {@code PersistentDocument} object to look for.
	 * @return {@code true} if the document is contained in this store, {@code false} otherwise.
	 */
	public boolean hasDocument(PersistentDocument document) {
		return this.getDocuments() != null ? Iterables.contains(this.getDocuments(), document) : false;
	}
	
	/**
	 * Adds a document to this store.
	 * @param document the {@code PersistentDocument} object to add.
	 * @return the {@code this} object.
	 */
	public PersistentDocumentStore addDocument(PersistentDocument document) {
		Validate.notNull(document, CannedMessages.NULL_ARGUMENT, "document");
		
		if (this.documents == null) {
			this.documents = Lists.newArrayList();
		}
		
		if (document.getStore() != this) {
			document.setStore(this);
		}
		
		if (!this.documents.contains(document)) {
			this.documents.add(document);
		}
		
		this.addReferer(document);
		return this;
	}
	
	/**
	 * Adds all the given document to this store.
	 * @param documents the {@link Iterable} of {@link PersistentDocument} objects to add.
	 * @return the {@code this} object.
	 */
	public PersistentDocumentStore addDocuments(Iterable<? extends PersistentDocument> documents) {
		Validate.notNull(documents, CannedMessages.NULL_ARGUMENT, "documents");
		
		for (PersistentDocument document : documents) {
			this.addDocument(document);
		}
		
		return this;
	}
	
	/**
	 * Removes a document from this store.
	 * @param document the {@code PersistentDocument} object to remove.
	 * @return {@code true} if an element was removed as a result of this call. 
	 */
	public boolean removeDocument(PersistentDocument document) {
		if (document == null) {
			return false;
		}
		
		if (document.getStore() == this) {
			document.setStore(null);
		}
		
		this.removeReferer(document);
		
		if (this.documents == null) {
			return false;
		}
		
		if (this.documents.contains(document)) {
			return this.documents.remove(document);
		} else {
			return false;
		}
	}
	
	/**
	 * Removes all the given documents from this store.
	 * @param documents the {@link Iterable} of {@link PersistentDocument} objects to remove.
	 * @return the {@code this} object.
	 */
	public PersistentDocumentStore removeDocuments(Iterable<? extends PersistentDocument> documents) {
		Validate.notNull(documents, CannedMessages.NULL_ARGUMENT, "documents");
		
		for (PersistentDocument document : documents) {
			this.removeDocument(document);
		}
		
		return this;
	}
	
	/**
	 * Wraps a {@link GenericizedDocumentStore} around this store for easier access.
	 * @param clazz the type of documents to assume.
	 * @return the {@link GenericizedDocumentStore} version of this store.
	 */
	public <T extends PersistentDocument> GenericizedDocumentStore<T> wrapGeneric(Class<T> clazz) {
		return GenericizedDocumentStore.create(this, clazz);
	}
}