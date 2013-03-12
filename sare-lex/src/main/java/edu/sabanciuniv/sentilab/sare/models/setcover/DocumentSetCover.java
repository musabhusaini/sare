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

package edu.sabanciuniv.sentilab.sare.models.setcover;

import java.util.*;

import javax.persistence.*;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;

/**
 * The class for a document set cover.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("setcover-corpus")
public class DocumentSetCover
	extends DocumentCorpus
	implements IDerivedStore {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6709686005135631465L;

	private static final String TOKENIZING_OPTIONS_FIELD = "tokenizingOptions";
	private static final String WEIGHT_COVERAGE_FIELD = "weightCoverage";
	
	/**
	 * Creates a new instance of the {@link DocumentSetCover} class.
	 */
	public DocumentSetCover() {
		//
	}
	
	/**
	 * Creates a new instance of the {@link DocumentSetCover} class.
	 * @param baseStore the base {@link PersistentDocumentStore} object.
	 */
	public DocumentSetCover(PersistentDocumentStore baseStore) {
		this();
		this.setBaseStore(baseStore);
	}
	
	/**
	 * Gets the corpus from which this set cover is derived.
	 * @return the {@link DocumentCorpus} which is the source of this set cover.
	 */
	public DocumentCorpus getBaseCorpus() {
		if (this.getBaseStore() instanceof DocumentCorpus) {
			return (DocumentCorpus)this.getBaseStore();
		}
		return null;
	}
	
	/**
	 * Gets all of the documents including non-covered documents.
	 * @return the {@link Iterable} of all {@link SetCoverDocument} objects that are part of this set cover.
	 */
	public Iterable<SetCoverDocument> getAllDocuments() {
		return Iterables.filter(super.getDocuments(), SetCoverDocument.class);
	}
	
	@Override
	public Iterable<PersistentDocument> getDocuments() {
		return Iterables.filter(super.getDocuments(), new Predicate<PersistentDocument>() {
			@Override
			public boolean apply(PersistentDocument input) {
				if (input instanceof SetCoverDocument) {
					return ((SetCoverDocument)input).isCovered();
				}
				return false;
			}
		});
	}

	/**
	 * Replaces a given document with another document.
	 * @param original the original document to replace.
	 * @param replacement the new document to replace with.
	 * @return {@code true} if the document was replaced.
	 */
	public boolean replaceDocuments(SetCoverDocument original, SetCoverDocument replacement) {
		if (this.documents == null) {
			return false;
		}
		
		boolean replaced = Collections.replaceAll(this.documents, original, replacement);
		if (replaced) {
			replacement.setStore(this);
		}
		
		return replaced;
	}

	/**
	 * Gets the total weight of this set cover (including uncovered documents).
	 * @return the weight of the set cover including uncovered documents.
	 */
	public double getTotalWeight() {
		double weight = 0;
		for (SetCoverDocument document : this.getAllDocuments()) {
			weight += document.getWeight();
		}
		
		return weight;
	}
	
	/**
	 * Gets the total weight the covered documents in this set cover.
	 * @return the weight of the set cover.
	 */
	public double getTotalCoveredWeight() {
		double weight = 0;
		for (SetCoverDocument document : this.getDocuments(SetCoverDocument.class)) {
			weight += document.getWeight();
		}
		
		return weight;
	}
	
	/**
	 * Gets the tokenizing options that were used to create this set cover.
	 * @return the {@link TokenizingOptions} used.
	 */
	public TokenizingOptions getTokenizingOptions() {
		return this.getProperty(TOKENIZING_OPTIONS_FIELD, TokenizingOptions.class);
	}
	
	/**
	 * Sets the tokenizing options that were used to create this set cover.
	 * Does not alter the set cover, just sets a value for record-keeping.
	 * @param tokenizingOptions the {@link TokenizingOptions} object to set.
	 * @return the {@code this} object.
	 */
	public DocumentSetCover setTokenizingTags(TokenizingOptions tokenizingOptions) {
		return (DocumentSetCover)this.setProperty(TOKENIZING_OPTIONS_FIELD, tokenizingOptions);
	}
	
	/**
	 * Gets the weight coverage that was used to create this set cover.
	 * @return the weight coverage used.
	 */
	public Double getWeightCoverage() {
		return this.getProperty(WEIGHT_COVERAGE_FIELD, Double.class);
	}
	
	/**
	 * Sets the weight coverage that was used to create this set cover.
	 * Does not alter the set cover, just sets a value for record-keeping.
	 * @param weightCoverage the weight coverage to set.
	 * @return the {@code this} object.
	 */
	public DocumentSetCover setWeightCoverage(Double weightCoverage) {
		if (weightCoverage == null) {
			this.setProperty(WEIGHT_COVERAGE_FIELD, null);
		} else {
			this.setProperty(WEIGHT_COVERAGE_FIELD, weightCoverage);
		}
		return this;
	}
	
	@Override
	public String getTitle() {
		return super.getTitle() == null && this.getBaseStore() != null ? this.getBaseStore().getTitle() : super.getTitle();
	}
	
	@Override
	public String getLanguage() {
		return super.getLanguage() == null && this.getBaseStore() != null ? this.getBaseStore().getLanguage() : super.getLanguage();
	}
	
	@Override
	public String getDescription() {
		return super.getDescription() == null && this.getBaseStore() != null ? this.getBaseStore().getDescription() : super.getDescription();
	}
}