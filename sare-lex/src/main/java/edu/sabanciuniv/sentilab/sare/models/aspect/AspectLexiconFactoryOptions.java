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

package edu.sabanciuniv.sentilab.sare.models.aspect;

import edu.sabanciuniv.sentilab.sare.models.base.documentStore.DocumentCorpus;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStoreFactoryOptions;

/**
 * Factory options for factories that can create {@link AspectLexicon} objects.
 * @author Mus'ab Husaini
 */
public class AspectLexiconFactoryOptions
	extends PersistentDocumentStoreFactoryOptions<AspectLexicon> {
	
	private DocumentCorpus baseCorpus;
	
	/**
	 * Gets the base corpus that will be set for the lexicon.
	 * @return the {@link DocumentCorpus} object representing the base corpus.
	 */
	public DocumentCorpus getBaseCorpus() {
		return this.baseCorpus;
	}
	
	/**
	 * Sets the base corpus to set for the lexicon (can be null).
	 * @param baseCorpus the {@link DocumentCorpus} object representing the base corpus to be set.
	 * @return the {@code this} object.
	 */
	public AspectLexiconFactoryOptions setBaseCorpus(DocumentCorpus baseCorpus) {
		this.baseCorpus = baseCorpus;
		return this;
	}
}