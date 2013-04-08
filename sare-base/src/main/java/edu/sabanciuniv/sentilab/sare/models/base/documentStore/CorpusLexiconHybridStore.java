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

import com.google.common.base.*;
import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.models.base.document.*;

/**
 * A hybrid store combining a {@link DocumentCorpus} and a {@link Lexicon} type stores.
 * @author Mus'ab Husaini
 */
public abstract class CorpusLexiconHybridStore<L extends Lexicon>
		extends HybridDocumentStore {

	private static final long serialVersionUID = 34183622693620836L;
	
	/**
	 * Creates a new instance of {@link CorpusLexiconHybridStore}.
	 * @param corpus the {@link DocumentCorpus} to base this store on.
	 * @param lexicon the lexicon to attach.
	 * @param shadowize a {@link Function} object that can convert base store documents to a shadow document.
	 */
	protected <T extends ShadowFullTextDocument> CorpusLexiconHybridStore(DocumentCorpus corpus, L lexicon,
			Function<PersistentDocument, T> shadowize) {
		super(corpus, lexicon);
		this.setBaseStore(corpus);
		
		if (corpus != null && shadowize != null) {
			this.setDocuments(Iterables.filter(
				Iterables.transform(corpus.getDocuments(), shadowize), Predicates.notNull()));
		}
	}
	
	/**
	 * Creates a new instance of {@link CorpusLexiconHybridStore}.
	 */
	public CorpusLexiconHybridStore() {
		this(null, null, null);
	}
	
	/**
	 * Gets the corpus this builder is based on.
	 * @return the {@link DocumentCorpus} object this builder is based on.
	 */
	public DocumentCorpus getCorpus() {
		if (this.getBaseStore() instanceof DocumentCorpus) {
			return (DocumentCorpus)this.getBaseStore();
		}
		return null;
	}
	
	/**
	 * Gets the lexicon being created by this builder.
	 * @return the {@link Lexicon} object this builder is creating.
	 */
	public Lexicon getLexicon() {
		return Iterables.getFirst(this.getBaseStores(Lexicon.class), null);
	}

	@Override
	public String getTitle() {
		return super.getTitle() != null ? super.getTitle() :
			this.getBaseStore() != null ? this.getBaseStore().getTitle() : null;
	}

	@Override
	public String getLanguage() {
		return super.getLanguage() != null ? super.getLanguage() :
			this.getBaseStore() != null ? this.getBaseStore().getLanguage() : null;
	}

	@Override
	public String getDescription() {
		return super.getDescription() != null ? super.getDescription() :
			this.getBaseStore() != null ? this.getBaseStore().getDescription() : null;
	}

	@Override
	public String getOwnerId() {
		return super.getOwnerId() != null ? super.getOwnerId() :
			this.getBaseStore() != null ? this.getBaseStore().getOwnerId() : null;
	}
}