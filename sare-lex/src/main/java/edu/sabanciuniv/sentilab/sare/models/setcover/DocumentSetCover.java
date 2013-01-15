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

package edu.sabanciuniv.sentilab.sare.models.setcover;

import java.util.*;
import java.util.regex.Pattern;

import javax.persistence.*;

import com.google.common.base.Function;
import com.google.common.collect.*;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import edu.sabanciuniv.sentilab.sare.models.base.document.TokenizingOptions;
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

	private static final String TOKENIZING_TAGS_FIELD = "tokenizingTags";
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
	 * Gets the total weight of this set cover.
	 * @return the weight of the set cover.
	 */
	public double totalWeight() {
		double weight = 0;
		for (SetCoverDocument document : this.wrapGeneric(SetCoverDocument.class).getDocuments()) {
			weight += document.getWeight();
		}
		
		return weight;
	}
	
	/**
	 * Gets the tokenizing tags that were used to create this set cover.
	 * @return the {@link Iterables} of tag strings.
	 */
	public Iterable<String> getTokenizingTags() {
		JsonElement field = this.getOtherData().get(TOKENIZING_TAGS_FIELD);
		if (field != null && field.isJsonArray()) {
			JsonArray array = field.getAsJsonArray();
			return Iterables.transform(array, new Function<JsonElement, String>() {
				@Override
				public String apply(JsonElement input) {
					return input.getAsString();
				}
			});
		}
		
		return Lists.newArrayList();
	}
	
	/**
	 * Sets the tokenizing tags that were used to create this set cover.
	 * Does not alter the set cover, just sets a value for record-keeping.
	 * @param tokenizingTags the {@link Iterables} of tag strings to set.
	 * @return the {@code this} object.
	 */
	public DocumentSetCover setTokenizingTags(Iterable<String> tokenizingTags) {
		if (tokenizingTags == null) {
			this.getOtherData().remove(TOKENIZING_TAGS_FIELD);
		} else {
			List<String> tokenizingTagsList = Lists.newArrayList(tokenizingTags);
			this.getOtherData().add(TOKENIZING_TAGS_FIELD, new Gson().toJsonTree(tokenizingTagsList,
				new TypeToken<List<String>>(){
					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;
				}.getType()));
		}
		return this;
	}
	
	/**
	 * Sets the tokenizing tags that were used to create this set cover.
	 * Does not alter the set cover, just sets a value for record-keeping.
	 * @param tokenizingOptions the {@link TokenizingOptions} object that was used to create this set cover.
	 * @return the {@code this} object.
	 */
	public DocumentSetCover setTokenizingTags(TokenizingOptions tokenizingOptions) {
		if (tokenizingOptions == null) {
			return this.setTokenizingTags((Iterable<String>)null);
		}
		
		return this.setTokenizingTags(Iterables.transform(tokenizingOptions.getTags(), new Function<Pattern, String>() {
			@Override
			public String apply(Pattern input) {
				return TokenizingOptions.stripPattern(input.pattern());
			}
		}));
	}
	
	/**
	 * Gets the weight coverage that was used to create this set cover.
	 * @return the weight coverage used.
	 */
	public Double getWeightCoverage() {
		JsonElement field = this.getOtherData().get(WEIGHT_COVERAGE_FIELD);
		if (field != null && field.isJsonPrimitive() && field.getAsJsonPrimitive().isNumber()) {
			return field.getAsDouble();
		}
		return null;
	}
	
	/**
	 * Sets the weight coverage that was used to create this set cover.
	 * Does not alter the set cover, just sets a value for record-keeping.
	 * @param weightCoverage the weight coverage to set.
	 * @return the {@code this} object.
	 */
	public DocumentSetCover setWeightCoverage(Double weightCoverage) {
		if (weightCoverage == null) {
			this.getOtherData().remove(WEIGHT_COVERAGE_FIELD);
		} else {
			this.getOtherData().addProperty(WEIGHT_COVERAGE_FIELD, weightCoverage);
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