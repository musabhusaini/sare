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
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Entity
public abstract class PersistentDocumentStore
	extends PersistentObject
	implements IDocumentStore {

	/**
	 * 
	 */
	private static final long serialVersionUID = 24809167105583854L;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "base_store_id")
	private PersistentDocumentStore baseStore;
	
	@OneToMany(mappedBy = "baseStore")
	private Collection<PersistentDocumentStore> derivedStores;
	
	@Column(name="owner_id")
	private String ownerId;
	
	@Column
	protected String title;
	
	@Column
	protected String language;
	
	@Column
	protected String description;
	
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
		return this.language == null ? "en"	: this.language;
	}

	/**
	 * Sets the language that this store's documents are in.
	 * @param language the language of this store.
	 * @return the {@code this} object.
	 */
	public PersistentDocumentStore setLanguage(String language) {
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
	public PersistentDocumentStore setDescription(String description) {
		this.description = description;
		return this;
	}
	
	/**
	 * Sets the ID of the entity that owns this store.
	 * @param ownerId the {@link UUID} of the owner.
	 * @return the {@code this} object.
	 */
	public PersistentDocumentStore setOwnerId(UUID ownerId) {
		this.ownerId = ownerId != null ? ownerId.toString() : null;
		return this;
	}
	
	@Override
	public UUID getOwnerId() { 
		return UUID.fromString(this.ownerId);
	}
}