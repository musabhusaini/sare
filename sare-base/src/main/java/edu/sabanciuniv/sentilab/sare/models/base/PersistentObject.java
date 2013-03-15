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

package edu.sabanciuniv.sentilab.sare.models.base;

import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang3.*;

import com.google.common.collect.Lists;
import com.google.gson.*;

import edu.sabanciuniv.sentilab.utils.CannedMessages;

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

	private static final String DIRTY_FLAG_STRING = "##dirty##";
	
	@Column
	@Temporal(TemporalType.TIMESTAMP)
	protected Date created;

	@Version
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

	@Column(columnDefinition="LONGTEXT")
	protected String title;
	
	@Basic(fetch=FetchType.LAZY)
	@Column(name="other_data", columnDefinition="TEXT")
	private String otherData;
	
	@Transient
	private JsonObject otherDataJson;
	
	protected void setOtherData() {
		if (this.otherDataJson != null && this.otherDataJson.entrySet().size() == 0) {
			this.otherDataJson = null;
		}
		this.otherData = this.otherDataJson != null ? this.otherDataJson.toString() : null;
	}
	
	/**
	 * Creates an empty instance of {@link PersistentObject}. 
	 */
	protected PersistentObject() {
		this.referencedObjects = Lists.newArrayList();
		this.refererObjects = Lists.newArrayList();
	}
	
	@PrePersist
	protected void preCreate() {
		this.created = new Date();
		this.setOtherData();
	}
	
	@PreUpdate
	protected void preUpdate() {
		this.setOtherData();
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
	 * Gets any other data attached to this object.
	 * @return the {@link JsonObject} representing other data.
	 */
	public JsonObject getOtherData() {
		if (this.otherDataJson == null) {
			if (this.otherData == null) {
				this.setOtherData(new JsonObject());
			} else {
				this.setOtherData(this.otherData);
			}
		}
		
		this.otherData = DIRTY_FLAG_STRING;
		return this.otherDataJson;
	}
	
	/**
	 * Sets any other data attached to this object.
	 * @param otherData the {@link JsonObject} representing any other data.
	 * @return the {@code this} object.
	 */
	public PersistentObject setOtherData(JsonObject otherData) {
		this.otherDataJson = otherData;
		this.otherData = DIRTY_FLAG_STRING;
		return this;
	}

	/**
	 * Gets a flag indicating whether this object has the given property attached or not.
	 * @param property the name of the property to look for.
	 * @return {@code true} if the property exists, {@code false} otherwise.
	 */
	public boolean hasProperty(String property) {
		Validate.notEmpty(property, CannedMessages.EMPTY_ARGUMENT, "property");
		return this.getOtherData().has(property);
	}

	/**
	 * Gets an attached property of this object.
	 * @param property the name of the property to get.
	 * @param classOfT the type of object expected.
	 * @return the retrieved property; {@code null} if not present or if the type is not correct.
	 */
	public <T> T getProperty(String property, Class<T> classOfT) {
		Validate.notEmpty(property, CannedMessages.EMPTY_ARGUMENT, "property");
		Validate.notNull(classOfT, CannedMessages.NULL_ARGUMENT, "type");
		
		try {
			return new Gson().fromJson(this.getOtherData().get(property), classOfT);
		} catch (JsonSyntaxException e) {
			return null;
		}
	}
	
	/**
	 * Attaches an extra property to this object.
	 * @param property the name of the property to attached.
	 * @param value value of the property to be attached.
	 * @return the {@code this} object.
	 */
	public PersistentObject setProperty(String property, Object value) {
		if (value == null) {
			this.getOtherData().remove(property);
		} else {
			this.getOtherData().add(property, new Gson().toJsonTree(value));
		}
		return this;
	}
	
	/**
	 * Sets any other data attached to this object.
	 * @param otherData the {@link String} object containing other data (should be a valid stringified JSON object).
	 * @return the {@code this} object.
	 * @throws IllegalArgumentException when the passed string cannot be parsed to a valid JSON object.
	 */
	public PersistentObject setOtherData(String otherData) {
		if (otherData != null) { 
			JsonElement tmpJsonData = new JsonParser().parse(StringUtils.defaultString(otherData));
			if (tmpJsonData.isJsonObject()) {
				this.setOtherData(tmpJsonData.getAsJsonObject());
			} else {
				throw new IllegalArgumentException("the argument 'otherData' must be a valid stringified JSON object");
			}
		} else {
			this.setOtherData((JsonObject)null);
		}
		
		return this;
	}
	
	/**
	 * Gets the ID of the entity that owns this object.
	 * @return the {@link String} objecting representing the identifier for the owner.
	 */
	public abstract String getOwnerId();
}