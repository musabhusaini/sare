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

package edu.sabanciuniv.sentilab.sare.models.base

import java.io.Serializable
import java.util.UUID

import javassist.bytecode.ByteArray

import javax.persistence._

import org.apache.commons.lang3._

import edu.sabanciuniv.sentilab.core.models._
import edu.sabanciuniv.sentilab.utils._

/**
 * An object that has a unique identifier attached to it.
 * @author Mus'ab Husaini
 */
@MappedSuperclass
class UniquelyIdentifiableObject(byteId: Array[Byte])
	extends ModelLike with UniquelyIdentifiable with Serializable {
	
	setId(byteId)
	
	@Id
	@Lob
	@Column(name="uuid", columnDefinition="VARBINARY(16)", length=16, updatable=false, nullable=false)
	private var id: Array[Byte] = _
  
	/**
	 * Creates a new object of type {@link UniquelyIdentifiableObject}.
	 * @param id Identifier of this object.
	 */
	def this(uuid: UUID) = this(UuidUtils.toBytes(uuid))
	
	/**
	 * Creates a new object of type {@link UniquelyIdentifiableObject}.
	 * @param id Identifier of the object.
	 */
	def this(uuid: String) = this(UuidUtils.toBytes(uuid))

	/**
	 * Creates a new object of type {@link UniquelyIdentifiableObject}.
	 */
	def this() = this(UUID.randomUUID)
	
	/**
	 * Sets the identifier for this object.
	 * @param id The identifier to set.
	 * @return the {@code this} object.
	 */
	def setIdentifier(id: String): UniquelyIdentifiableObject = setIdentifier(UuidUtils.create(id))
	
	/**
	 * Sets the UUID for this object.
	 * @param id The {@link UUID} object to set as the identifier.
	 * @return the {@code this} object.
	 */
	def setIdentifier(id: UUID): UniquelyIdentifiableObject =
	  	setId(UuidUtils.toBytes(Validate.notNull(id, CannedMessages.NULL_ARGUMENT, "id")))

	/**
	 * Sets the ID for this object.
	 * @param id the {@link ByteArray} containing the UUID bytes to set.
	 * @return the {@code this} object.
	 */
	def setId(id: Array[Byte]) = {
		Option(id) match {
		  	case Some(id) if UuidUtils.isUuid(id) => this.id = id
		  	case Some(id) => throw new java.lang.IllegalArgumentException("argument 'id' is not a valid UUID")
		  	case _ => ()
		}
		
		this
	}
	
	/**
	 * Gets the UUID for this object.
	 * @return the {@link UUID} object containing the UUID for this object.
	 */
	@Transient
	def getIdentifier = UuidUtils.create(id)

	/**
	 * Gets the ID for this object.
	 * @return the {@link ByteArray} containing the byte version of the UUID for this object.
	 */
	def getId = id

	override def toString = UuidUtils.normalize(getIdentifier)
	
	override def equals(obj: Any) = Option(obj) match {
	  	case Some(other: UniquelyIdentifiableObject) => ObjectUtils.equals(getIdentifier, other.getIdentifier)
	  	case _ => super.equals(obj)
	}
	
	override def hashCode = UuidUtils.normalize(this.getId).hashCode
}