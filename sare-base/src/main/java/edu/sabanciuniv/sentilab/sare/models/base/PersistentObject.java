package edu.sabanciuniv.sentilab.sare.models.base;

import java.util.*;

import javax.persistence.*;

import com.google.common.collect.Lists;

/**
 * The base class for objects that are to be persisted.
 * @author Mus'ab Husaini
 */
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "object_type")
@Entity
@Table(name = "persistent_objects")
public abstract class PersistentObject
	extends UniquelyIdentifiableObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1497804812766237628L;

	@Column
	@Temporal(TemporalType.TIMESTAMP)
	protected Date created;
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	protected Date updated;
	
	@ManyToMany(fetch=FetchType.LAZY)
	@JoinTable(
		name="jt_object_references",
		joinColumns={@JoinColumn(name="referer_id", referencedColumnName="uuid")},
		inverseJoinColumns={@JoinColumn(name="referee_id", referencedColumnName="uuid")}
	)
	protected List<PersistentObject> referencedObjects;
	
	@ManyToMany(mappedBy="referencedObjects", cascade=CascadeType.ALL)
	protected List<PersistentObject> refererObjects;
	
	/**
	 * Creates an empty instance of {@link PersistentObject}. 
	 */
	protected PersistentObject() {
		this.referencedObjects = Lists.newArrayList();
		this.refererObjects = Lists.newArrayList();
	}
	
	@PrePersist
	protected void preCreate() {
		this.created = this.updated = new Date();
	}
	
	@PreUpdate
	protected void preUpdate() {
		this.updated = new Date();
	}
	
	/**
	 * Adds a reference to the provided object in the current object.
	 * @param reference the {@link PersistentObject} whose reference is to be added.
	 * @return the {@code this} object.
	 */
	protected PersistentObject addReference(PersistentObject reference) {
		if (reference == this) {
			return this;
		}
		
		if (reference != null) {
			if (!this.referencedObjects.contains(reference)) {
				this.referencedObjects.add(reference);
			}
			
			if (!reference.refererObjects.contains(this)) {
				reference.addReferer(this);
			}
		}
		
		return this;
	}
	
	protected PersistentObject removeReference(PersistentObject reference) {
		if (reference != null) {
			if (this.referencedObjects.contains(reference)) {
				this.referencedObjects.remove(reference);
			}
			
			if (reference.refererObjects.contains(this)) {
				reference.removeReferer(this);
			}
		}
		
		return this;
	}
	
	/**
	 * Removes the reference to the provided object from the current object.
	 * @param referer the {@link PersistentObject} whose reference is to be removed.
	 * @return the {@code this} object.
	 */
	protected PersistentObject addReferer(PersistentObject referer) {
		if (referer == this) {
			return this;
		}
		
		if (referer != null) {
			if (!this.refererObjects.contains(referer)) {
				this.refererObjects.add(referer);
			}
			
			if (!referer.referencedObjects.contains(this)) {
				referer.addReference(this);
			}
		}
		
		return this;
	}
	
	protected PersistentObject removeReferer(PersistentObject referer) {
		if (referer != null) {
			if (this.refererObjects.contains(referer)) {
				this.refererObjects.remove(referer);
			}
			
			if (referer.referencedObjects.contains(this)) {
				referer.removeReference(this);
			}
		}
		
		return this;
	}
	
	/**
	 * Gets the date this object was first created.
	 * @return the {@link Date} object representing the date of creation.
	 */
	public Date getFirstCreatedDate() {
		return this.created;
	}

	/**
	 * Gets the date this object was last updated.
	 * @return the {@link Date} object representing the date of updation.
	 */
	public Date getLastUpdatedDate() {
		return this.updated;
	}

	/**
	 * Gets the ID of the entity that owns this object.
	 * @return the {@link String} objecting representing the identifier for the owner.
	 */
	public abstract String getOwnerId();
}