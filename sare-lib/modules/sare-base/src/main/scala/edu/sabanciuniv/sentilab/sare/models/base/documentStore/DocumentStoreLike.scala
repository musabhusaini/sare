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

import edu.sabanciuniv.sentilab.core.models.ModelLike
import edu.sabanciuniv.sentilab.sare.models.base.document.DocumentLike

/**
 * The base interface for all document stores.
 * @author Mus'ab Husaini
 */
trait DocumentStoreLike extends ModelLike {

	/**
	 * Gets the title of this store.
	 * @return the title of this store.
	 */
	def getTitle: String
	
	/**
	 * Gets the language that this store's documents are in.
	 * @return the language of this store.
	 */
	def getLanguage: String
	
	/**
	 * Gets the description of this store.
	 * @return
	 */
	def getDescription: String
	
	/**
	 * Gets the documents in this store.
	 * @return the {@link Iterable} containing {@link DocumentLike} objects stored in this store.
	 */
	def getDocuments: java.lang.Iterable[_ <: DocumentLike]
}
