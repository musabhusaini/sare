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

package edu.sabanciuniv.sentilab.sare.models.base;

import java.io.Serializable;
import java.util.UUID;

import javassist.bytecode.ByteArray;

import javax.persistence.*;

import org.apache.commons.lang3.*;

import edu.sabanciuniv.sentilab.core.models.*;
import edu.sabanciuniv.sentilab.utils.*;

/**
 * An object that has a unique identifier attached to it.
 * @author Mus'ab Husaini
 */
@MappedSuperclass
public class UniquelyIdentifiableObject
	implements IModel, IUniquelyIdentifiable, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@Lob
	@Column(name="uuid", columnDefinition="VARBINARY(16)", length=16, updatable=false, nullable=false)
	private byte[] id;

	/**
	 * Creates a new object of type {@link UniquelyIdentifiableObject}.
	 */
	public UniquelyIdentifiableObject() {
		this(UUID.randomUUID());
	}
	
	/**
	 * Creates a new object of type {@link UniquelyIdentifiableObject}.
	 * @param id Identifier of this object.
	 */
	public UniquelyIdentifiableObject(UUID id) {
		this.setIdentifier(id);
	}
	
	/**
	 * Creates a new object of type {@link UniquelyIdentifiableObject}.
	 * @param id Identifier of the object.
	 */
	public UniquelyIdentifiableObject(String id) {
		this.setIdentifier(id);
	}
	
	/**
	 * Sets the identifier for this object.
	 * @param id The identifier to set.
	 * @return the {@code this} object.
	 */
	public UniquelyIdentifiableObject setIdentifier(String id) {
		this.setIdentifier(UuidUtils.create(id));
		return this;
	}
	
	/**
	 * Sets the UUID for this object.
	 * @param id The {@link UUID} object to set as the identifier.
	 * @return the {@code this} object.
	 */
	public UniquelyIdentifiableObject setIdentifier(UUID id) {
		this.id = UuidUtils.toBytes(Validate.notNull(id, CannedMessages.NULL_ARGUMENT, "id"));
		return this;
	}

	/**
	 * Gets the UUID for this object.
	 * @return the {@link UUID} object containing the UUID for this object.
	 */
	@Transient
	public UUID getIdentifier() {
		return UuidUtils.create(this.id);
	}

	/**
	 * Gets the ID for this object.
	 * @return the {@link ByteArray} containing the byte version of the UUID for this object.
	 */
	public byte[] getId() {
		return this.id;
	}

	/**
	 * Sets the ID for this object.
	 * @param id the {@link ByteArray} containing the UUID bytes to set.
	 * @return the {@code this} object.
	 */
	public UniquelyIdentifiableObject setId(byte[] id) {
		this.id = id;
		return this;
	}
	
	@Override
	public String toString() {
		return this.getIdentifier().toString();
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof UniquelyIdentifiableObject) {
			return ObjectUtils.equals(this.getIdentifier(), ((UniquelyIdentifiableObject)other).getIdentifier());
		}
		
		return super.equals(other);
	}
	
	@Override
	public int hashCode() {
		return UuidUtils.normalize(this.getId()).hashCode();
	}
}