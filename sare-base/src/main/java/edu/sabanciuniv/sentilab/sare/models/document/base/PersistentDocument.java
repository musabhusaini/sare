package edu.sabanciuniv.sentilab.sare.models.document.base;

import java.util.*;

import javax.persistence.*;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.base.*;
import edu.sabanciuniv.sentilab.sare.models.documentStore.base.DocumentStoreBase;

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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "store_id")
	protected DocumentStoreBase store;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "base_document_id")
	protected PersistentDocument baseDocument;
	
	@OneToMany(mappedBy = "baseDocument")
	protected Collection<PersistentDocument> derivedDocuments;
	
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
		this.baseDocument = baseDocument;
		this.addReference(baseDocument);
		
		if (this.baseDocument != null && !this.baseDocument.getDerivedDocuments().contains(this)) {
			this.baseDocument.getDerivedDocuments().add(this);
		}
		
		return this;
	}

	/**
	 * Gets the derived documents of this document.
	 * @return the {@link Collection} of {@link PersistentDocument} objects that are the derived documents of this document.
	 */
	public Collection<PersistentDocument> getDerivedDocuments() {
		if (this.derivedDocuments == null) {
			this.derivedDocuments = Lists.newArrayList();
		}
		
		return this.derivedDocuments;
	}

	/**
	 * Sets the derived documents of this document.
	 * @param derivedDocuments the {@link Collection} of {@link PersistentDocument} objects to set as the derived documents.
	 * @return the {@code this} object.
	 */
	public PersistentDocument setDerivedDocuments(Collection<PersistentDocument> derivedDocuments) {
		this.derivedDocuments = derivedDocuments;
		
		if (this.derivedDocuments != null) {
			for (PersistentDocument derivedDocument : this.derivedDocuments) {
				if (derivedDocument.getBaseDocument() != this) {
					derivedDocument.setBaseDocument(this);
				}
			}
		}
		
		return this;
	}
}