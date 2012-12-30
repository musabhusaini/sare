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

import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.base.*;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * The base class for all persistent document objects.
 * @author Mus'ab Husaini
 */
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
public abstract class PersistentDocument
	extends PersistentObject
	implements IDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5346580952234720900L;

	/**
	 * The store this document is stored under. Derived classes are responsible for maintaining the relationship.
	 */
	@ManyToOne
	@JoinColumn(name = "store_id")
	protected PersistentDocumentStore store;
	
	@ManyToOne
	@JoinColumn(name = "base_document_id")
	private PersistentDocument baseDocument;
	
	@OneToMany(mappedBy = "baseDocument")
	private Collection<PersistentDocument> derivedDocuments;
	
	protected PersistentDocument addDerivedDocument(PersistentDocument derivedDocument) {
		Validate.notNull(derivedDocument, CannedMessages.NULL_ARGUMENT, "derivedDocument");
		
		if (!this.hasDerivedDocument(derivedDocument)) {
			this.derivedDocuments.add(derivedDocument);
		}
		
		if (derivedDocument.getBaseDocument() != this) {
			derivedDocument.setBaseDocument(this);
		}
		
		return this;
	}
	
	protected PersistentDocument removeDerivedDocument(PersistentDocument derivedDocument) {
		Validate.notNull(derivedDocument, CannedMessages.NULL_ARGUMENT, "derivedDocument");
		
		if (this.hasDerivedDocument(derivedDocument)) {
			this.derivedDocuments.remove(derivedDocument);
			
			if (derivedDocument.getBaseDocument() == this) {
				derivedDocument.setBaseDocument(null);
			}
		}
		
		return this;
	}
	
	@Override
	public PersistentDocumentStore getStore() {
		return this.store;
	}
	
	/**
	 * Gets this document's base document.
	 * @return the {@link PersistentDocument} object which is the base document of this document.
	 */
	public PersistentDocument getBaseDocument() {
		return this.baseDocument;
	}
	
	/**
	 * Sets the base document.
	 * @param baseDocument the {@link PersistentDocument} object to set as the base document.
	 * @return the {@code this} object.
	 */
	public PersistentDocument setBaseDocument(PersistentDocument baseDocument) {
		PersistentDocument prevBase = this.getBaseDocument();
		
		this.baseDocument = baseDocument;
		this.addReference(baseDocument);
		
		if (prevBase != null) {
			this.removeReference(prevBase);
			prevBase.removeDerivedDocument(this);
		}
		
		if (this.baseDocument != null && !this.baseDocument.hasDerivedDocument(this)) {
			this.baseDocument.addDerivedDocument(this);
		}
		
		return this;
	}

	/**
	 * Gets the derived documents of this document.
	 * @return an {@link Iterable} of {@link PersistentDocument} objects that are the derived documents of this document.
	 */
	public Iterable<PersistentDocument> getDerivedDocuments() {
		if (this.derivedDocuments == null) {
			this.derivedDocuments = Lists.newArrayList();
		}
		
		return Collections.unmodifiableCollection(this.derivedDocuments);
	}
	
	/**
	 * Gets a flag indicating whether the provided document is in the list of derived documents of this document.
	 * @param derivedDocument the {@link PersistentDocument} object to look for.
	 * @return {@code true} if the document exists in the list of derived documents, {@code false} otherwise.
	 */
	public boolean hasDerivedDocument(PersistentDocument derivedDocument) {
		return Iterables.contains(this.getDerivedDocuments(), derivedDocument);
	}
	
	@Override
	public String getOwnerId() {
		return this.store != null ? this.store.getOwnerId() : null;
	}
}