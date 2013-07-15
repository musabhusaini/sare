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

package edu.sabanciuniv.sentilab.sare.models.base.documentStore

import scala.collection.JavaConversions._

import javax.persistence.Entity

import com.google.common.collect._

/**
 * Base class for stores that combine two or more stores, possibly of various types.
 * @author Mus'ab Husaini
 */
@Entity
abstract class HybridDocumentStore(stores: java.lang.Iterable[PersistentDocumentStore])
	extends PersistentDocumentStore
	with DerivedStoreLike {
	
	Option(stores) foreach { _ foreach { addReference _ } }
	
	/**
	 * Creates a new instance of the {@link HybridDocumentStore}.
	 * @param stores the {@link PersistentDocumentStore} objects this hybrid is based on.
	 */
	def this(stores: PersistentDocumentStore*) = this(stores.toSeq)
	
	/**
	 * Creates a new instance of the {@link HybridDocumentStore}.
	 */
	def this() = this(Seq())
	
	/**
	 * Gets all the base stores of a given type.
	 * @param clazz the type of base stores to find.
	 * @return an {@link Iterable} containing all the base stores of the given type.
	 */
	def getBaseStores[T <: PersistentDocumentStore](clazz: Class[T]): java.lang.Iterable[T] =
	  	referencedObjects filter { clazz isAssignableFrom _.getClass } map { _.asInstanceOf[T] }
}