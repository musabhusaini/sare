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

import javax.persistence.*;

import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;

/**
 * An aspect lexicon builder store.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("alex-builder")
public class AspectLexiconBuilderDocumentStore
	extends LexiconBuilderDocumentStore {

	private static final long serialVersionUID = -6183785889418787784L;
	
	/**
	 * Creates a new instance of the {@link AspectLexiconBuilderDocumentStore}.
	 * @param corpus the {@link DocumentCorpus} the lexicon is based on.
	 * @param lexicon the {@link AspectLexicon} being built.
	 */
	public AspectLexiconBuilderDocumentStore(DocumentCorpus corpus, AspectLexicon lexicon) {
		super(corpus, lexicon);
	}
	
	/**
	 * Creates a new instance of the {@link AspectLexiconBuilderDocumentStore}.
	 */
	public AspectLexiconBuilderDocumentStore() {
		this(null, null);
	}
	
	@Override
	public AspectLexicon getLexicon() {
		Lexicon lexicon = super.getLexicon();
		if (lexicon instanceof AspectLexicon) {
			return (AspectLexicon)lexicon;
		}
		return null;
	}
}