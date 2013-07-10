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

package edu.sabanciuniv.sentilab.sare.models.base.documentStore;

import javax.persistence.Entity;

import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticProcessorLike;
import edu.sabanciuniv.sentilab.utils.text.nlp.factory.*;

/**
 * The base class for all document corpora.
 * @author Mus'ab Husaini
 */
@Entity
public abstract class DocumentCorpus
		extends PersistentDocumentStore {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6392601190047482976L;
	
	/**
	 * Gets the basic linguistic processor that can be used for this corpus.
	 * @return the {@link LinguisticProcessorLike} object that can be used.
	 */
	public LinguisticProcessorLike getLinguisticProcessor() {
		return new LinguisticProcessorFactory()
			.setLanguage(this.getLanguage())
			.create();
	}
}