package edu.sabanciuniv.sentilab.sare.models.documentStore.base;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.sabanciuniv.sentilab.sare.models.document.base.PersistentDocument;
import edu.sabanciuniv.sentilab.sare.models.base.UniquelyIdentifiableObject;

/**
 * The base class for all objects that can store documents.
 * @author Mus'ab Husaini
 */
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "store_type")
@Entity
@Table(name = "document_stores")
public abstract class DocumentStoreBase
	extends UniquelyIdentifiableObject
	implements IDocumentStore {

	/**
	 * 
	 */
	private static final long serialVersionUID = 24809167105583854L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "base_store_id")
	protected DocumentStoreBase baseStore;
	
	@OneToMany(mappedBy = "baseStore", cascade = CascadeType.ALL)
	protected Collection<DocumentStoreBase> derivedStores;
	
	@Column
	protected String title;
	
	@Column
	protected String language;
	
	@Column
	protected String description;
	
	@OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
	protected Collection<PersistentDocument> documents;

	/**
	 * Gets the base store for this store.
	 * @return the {@link DocumentStoreBase} object that is the base store for this store.
	 */
	public DocumentStoreBase getBaseStore() {
		return baseStore;
	}

	/**
	 * Sets the base store for this store.
	 * @param baseStore the base store.
	 * @return the {@code this} object.
	 */
	public DocumentStoreBase setBaseStore(DocumentStoreBase baseStore) {
		this.baseStore = baseStore;
		return this;
	}

	/**
	 * Gets the derived stores of this store.
	 * @return the {@link Collection} of {@link DocumentStoreBase} objects that derive from this store.
	 */
	public Collection<DocumentStoreBase> getDerivedStores() {
		return derivedStores;
	}

	/**
	 * Sets the derived store of this store.
	 * @param derivedStores the {@link Collection} of {@link DocumentStoreBase} objects to set as the derived stores of this store.
	 * @return the {@code this} object.
	 */
	public DocumentStoreBase setDerivedStores(Collection<DocumentStoreBase> derivedStores) {
		this.derivedStores = derivedStores;
		return this;
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
	public DocumentStoreBase setTitle(String title) {
		this.title = title;
		return this;
	}
	
	@Override
	public String getLanguage() {
		return language;
	}

	/**
	 * Sets the language that this store's documents are in.
	 * @param language the language of this store.
	 * @return the {@code this} object.
	 */
	public DocumentStoreBase setLanguage(String language) {
		this.language = language;
		return this;
	}

	@Override
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Sets the description of this store.
	 * @param description the description of this store.
	 * @return the {@code this} object.
	 */
	public DocumentStoreBase setDescription(String description) {
		this.description = description;
		return this;
	}
	
	@Override
	public Collection<PersistentDocument> getDocuments()  {
		return this.documents;
	}

	/**
	 * Sets the documents in this store.
	 * @param documents the documents to set.
	 * @return the {@code this} object.
	 */
	public DocumentStoreBase setDocuments(Collection<PersistentDocument> documents) {
		this.documents = documents;
		return this;
	}
}