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

package edu.sabanciuniv.sentilab.sare.models.opinion

import javax.persistence._

import edu.sabanciuniv.sentilab.sare.models.base.document._

/**
 * Represents an opinion document (review).
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("opinion-document")
class OpinionDocument extends EditableTextDocument with IOpinionDocument {

	override def getPolarity = Option(getProperty("polarity", classOf[java.lang.Double])) map {
	  	case polarity if polarity.isNaN => null
	  	case polarity => polarity
	} getOrElse null

	/**
	 * Sets the opinion polarity of this document.
	 * @param polarity the opinion polarity to set.
	 * @return the {@code this} object.
	 */
	def setPolarity(polarity: java.lang.Double) =
		setProperty("polarity", Option(polarity) filter { !_.isNaN } getOrElse null).asInstanceOf[OpinionDocument]
	
	override def toString = "%s [polarity = %1.2f]".format(super.toString, getPolarity)
}