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

import java.util.*;
import java.util.Map.Entry;

import javax.persistence.*;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.opinion.OpinionMinedDocument;

/**
 * A document that contains the result of aspect-based opinion mining.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("alex-opinion-mined-doc")
public class AspectOpinionMinedDocument
		extends OpinionMinedDocument {

	private static final long serialVersionUID = 1891874870536475030L;

	private static class AspectPolarityTuple {
		public AspectLexicon aspect;
		public Double polarity;
		
		public AspectPolarityTuple(AspectLexicon aspect, Double polarity) {
			this();
			
			this.aspect = aspect;
			this.polarity = polarity;
		}
		
		public AspectPolarityTuple() {
		}
	}
	
	/**
	 * Creates an instance of {@link AspectOpinionMinedDocument}.
	 * @param baseDocument the {@link FullTextDocument} to base this instance on.
	 */
	public AspectOpinionMinedDocument(FullTextDocument baseDocument) {
		super(baseDocument);
	}
	
	/**
	 * Creates an instance of {@link AspectOpinionMinedDocument}.
	 */
	public AspectOpinionMinedDocument() {
		this(null);
	}

	/**
	 * Gets the aspect-based opinion polarities of this document.
	 * @return a {@link Map} of {@link AspectLexicon} objects and their polarities. The keys might only contain the most necessary information.
	 */
	public Map<AspectLexicon, Double> getAspectPolarities() {
		AspectPolarityTuple[] aspectPolaritiesList = this.getProperty("aspectPolarities", AspectPolarityTuple[].class);
		if (aspectPolaritiesList == null) {
			return Maps.<AspectLexicon, Double>newHashMap();
		}
		
		Map<AspectLexicon, Double> aspectPolarities = Maps.newHashMap();
		for (AspectPolarityTuple tuple : aspectPolaritiesList) {
			aspectPolarities.put(tuple.aspect, tuple.polarity);
		}
		
		return aspectPolarities;
	}

	/**
	 * Sets the aspect-based opinion polarities of this document.
	 * @param aspectPolarities a {@link Map} of {@link AspectLexicon} objects to corresponding polarities to set.
	 * @return the {@code this} object.
	 */
	public AspectOpinionMinedDocument setAspectPolarities(Map<AspectLexicon, Double> aspectPolarities) {
		List<AspectPolarityTuple> tuples = Lists.newArrayList();
		if (aspectPolarities != null) {
			for (Entry<AspectLexicon, Double> entry : aspectPolarities.entrySet()) {
				tuples.add(new AspectPolarityTuple(entry.getKey(), entry.getValue()));
			}
		}
		this.setProperty("aspectPolarities", tuples.toArray(new AspectPolarityTuple[]{}));
		return this;
	}
}