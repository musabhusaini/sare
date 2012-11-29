package edu.sabanciuniv.sentilab.sare.models.base;

import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Lists;

import edu.sabanciuniv.sentilab.utils.CannedMessages;

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

	@ManyToMany
	@JoinTable(
		name="jt_object_references",
		joinColumns={@JoinColumn(name="referer_id", referencedColumnName="uuid")},
		inverseJoinColumns={@JoinColumn(name="referee_id", referencedColumnName="uuid")}
	)
	protected List<PersistentObject> referencedObjects;
	
	@ManyToMany(mappedBy="referencedObjects", cascade=CascadeType.ALL)
	protected List<PersistentObject> refererObjects;
	
	protected PersistentObject() {
		this.referencedObjects = Lists.newArrayList();
		this.refererObjects = Lists.newArrayList();
	}
	
	protected PersistentObject addReference(PersistentObject reference) {
		Validate.notNull(reference, CannedMessages.NULL_ARGUMENT, "reference");
		
		if (!this.referencedObjects.contains(reference)) {
			this.referencedObjects.add(reference);
			reference.addReferer(this);
		}
		
		return this;
	}
	
	protected PersistentObject addReferer(PersistentObject referer) {
		Validate.notNull(referer, CannedMessages.NULL_ARGUMENT, "referer");
		
		if (!this.refererObjects.contains(referer)) {
			this.refererObjects.add(referer);
			referer.addReference(this);
		}
		
		return this;
	}
}