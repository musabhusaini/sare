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

package edu.sabanciuniv.sentilab.sare.models.opinion;

import javax.persistence.*;

import edu.sabanciuniv.sentilab.sare.models.base.document.*;

/**
 * Represents an opinion document (review).
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("opinion-document")
public class OpinionDocument
	extends EditableTextDocument {

	private static final long serialVersionUID = -2242899151250566895L;

	/**
	 * Gets the opinion polarity of this document.
	 * @return the opinion polarity of this document.
	 */
	public Double getPolarity() {
		Double polarity = this.getProperty("polarity", Double.class);
		if (polarity != null && polarity.isNaN()) {
			return null;
		}
		return polarity;
	}

	/**
	 * Sets the opinion polarity of this document.
	 * @param polarity the opinion polarity to set.
	 * @return the {@code this} object.
	 */
	public OpinionDocument setPolarity(Double polarity) {
		if (polarity != null && polarity.isNaN()) {
			polarity = null;
		}
		this.setProperty("polarity", polarity);
		return this;
	}
	
	@Override
	public String toString() {
		return String.format("%s [polarity = %1.2f]", super.toString(), this.getPolarity());
	}
}