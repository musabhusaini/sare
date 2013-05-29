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

package edu.sabanciuniv.sentilab.sare.models.setcover

import javax.persistence._
import edu.sabanciuniv.sentilab.sare.models.base.document._
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticText
import edu.sabanciuniv.sentilab.sare.models.base.document._

/**
 * The class for set cover documents.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("setcover-document")
class SetCoverDocument(baseDocument: PersistentDocument)
	extends MergableDocument with IWeightedDocument {

	setCovered(true)
	setBaseDocument(baseDocument)
	
	private def updateWeight {
		if (weight == null) {
			setWeight(getWeight)
		}
	}

	/**
	 * Sets the weight of this document.
	 * @param weight the weight to set.
	 * @return the {@code this} object.
	 */
	private def setWeight(weight: java.lang.Double) = { this.weight = weight; this }

	protected override def preCreate {
		updateWeight
		super.preCreate
	}
	
	protected override def preUpdate() {
		updateWeight
		super.preUpdate
	}

	/**
	 * Creates a new instance of {@link SetCoverDocument}.
	 */
	def this() = this(null)
	
	override def setBaseDocument(baseDocument: PersistentDocument) = {
		super.setBaseDocument(baseDocument)
		title = null
		getContent(true)
		this
	}
	
	/**
	 * Gets the (possibly NLP-enriched) content of this document.
	 * @param enhanced {@code true} if NLP-enhanced content is desired, {@code false} otherwise.
	 * @return the (possibly NLP-enhanced) content of this document.
	 */
	def getContent(enhanced: Boolean) = enhanced match {
	  	case true => {
	  		getBaseDocument match {
	  		  	case doc: FullTextDocument => {
	  		  		if (title == null || ((Option(getLastUpdatedDate), Option(doc.getLastUpdatedDate)) match {
	  		  		  	case (Some(date), Some(superDate)) => date.getTime <= superDate.getTime
	  		  		  	case _ => false
	  		  		})) Option(doc.getParsedContent) foreach { content => title = content.toString(true) }
	  		  	}
	  		  	case _ => ()
	  		}
			title
		}
	  	case false => Option(getBaseDocument) map { _.getContent } getOrElse null
	}

	override def getContent = getContent(false)
	
	/**
	 * Gets the weight of this document.
	 * @return the weight of this document.
	 */
	override def getWeight = Option(weight) getOrElse getTotalTokenWeight
	
	/**
	 * Resets the weight of this document to {@code null}.
	 * @return the {@code this} object.
	 */
	def resetWeight = {
		setWeight(null)
		this
	}
	
	/**
	 * Gets a flag indicating whether this document is covered or not.
	 * @return {@code true} if covered; {@code false} otherwise.
	 */
	def isCovered = flag
	
	/**
	 * Sets a flag indicating whether this document is covered or not.
	 * @param covered {@code true} if covered; {@code false} otherwise.
	 * @return the {@code this} object.
	 */
	def setCovered(covered: Boolean) = {
		flag = covered
		this
	}

	override def toString = Option(getBaseDocument) map { _.toString } getOrElse super.toString 
}