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

package edu.sabanciuniv.sentilab.sare.models.base.documentStore;

import javax.persistence.*;

import com.google.common.base.*;

import edu.sabanciuniv.sentilab.core.models.UserInaccessibleModel;
import edu.sabanciuniv.sentilab.sare.models.base.document.*;

/**
 * The base class for a lexicon builder store.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("lex-builder")
public class LexiconBuilderDocumentStore
		extends CorpusLexiconHybridStore<Lexicon> implements UserInaccessibleModel {
	
	private static final long serialVersionUID = -5249613407009854782L;
	
	/**
	 * Creates an instance of {@link LexiconBuilderDocumentStore}.
	 * @param corpus the {@link DocumentCorpus} to base this store on.
	 * @param lexicon the {@link Lexicon} being created.
	 */
	public LexiconBuilderDocumentStore(DocumentCorpus corpus, Lexicon lexicon) {
		super(corpus, lexicon,
			new Function<PersistentDocument, LexiconBuilderDocument>() {
				@Override
				public LexiconBuilderDocument apply(PersistentDocument input) {
					if (input instanceof FullTextDocument) {
						return new LexiconBuilderDocument((FullTextDocument)input);
					}
					return null;
				}
			}
		);
	}
	
	/**
	 * Creates an empty instance of {@link LexiconBuilderDocumentStore}.
	 */
	public LexiconBuilderDocumentStore() {
		this(null, null);
	}
	
	@Override
	public DocumentCorpus getAccessible() {
		return this.getCorpus();
	}
}