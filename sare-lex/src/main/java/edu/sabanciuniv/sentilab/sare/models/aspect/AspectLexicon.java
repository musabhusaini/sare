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

import org.apache.commons.lang3.Validate;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A class that represents an aspect lexicon.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("aspect-lexicon")
public class AspectLexicon
	extends Lexicon {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1318074240345771899L;

	/**
	 * Creates an instance of the {@link AspectLexicon} class.
	 */
	public AspectLexicon() {
		//
	}
	
	/**
	 * Creates an instance of the {@link AspectLexicon} class based on the provided store.
	 * @param baseStore the {@link PersistentDocumentStore} object to use as the base store for this instance.
	 */
	public AspectLexicon(PersistentDocumentStore baseStore) {
		this();
		this.setBaseStore(baseStore);
	}

	/**
	 * Gets the corpus this lexicon or its parent lexicon is based on, if any.
	 * @return the {@link DocumentCorpus} this lexicon is based on.
	 */
	public DocumentCorpus getBaseCorpus() {
		if (this.getBaseStore() instanceof AspectLexicon) {
			return ((AspectLexicon)this.getBaseStore()).getBaseCorpus();
		} else if (this.getBaseStore() instanceof DocumentCorpus) {
			return (DocumentCorpus)this.getBaseStore();
		}
		
		return null;
	}
	
	/**
	 * Gets the aspect expressions in this aspect.
	 * @return an {@link Iterable} of {@link AspectExpression} objects contained in this aspect.
	 */
	public Iterable<AspectExpression> getExpressions() {
		return this.wrapGeneric(AspectExpression.class).getDocuments();
	}
	
	/**
	 * Finds the given expression within this lexicon.
	 * @param expression the expression to look for.
	 * @param recursive a flag indicating whether to look recursively in the entire hierarchy or not.
	 * @return the {@link AspectExpression} object for this expression.
	 */
	public AspectExpression findExpression(final String expression, boolean recursive) {
		AspectExpression aspectExpression = Iterables.find(this.getExpressions(), new Predicate<AspectExpression>() {
			@Override
			public boolean apply(AspectExpression input) {
				return expression != null && expression.equalsIgnoreCase(input.getContent());
			}
		}, null);
		
		// look recursively.
		if (aspectExpression == null && recursive) {
			for (AspectLexicon subAspect : this.getAspects()) {
				aspectExpression = subAspect.findExpression(expression, recursive);
				if (aspectExpression != null) {
					break;
				}
			}
		}
		
		return aspectExpression;
	}
	
	/**
	 * Finds the given expression within this lexicon (not recursively).
	 * @param expression the expression to look for.
	 * @return the {@link AspectExpression} object for this expression.
	 */
	public AspectExpression findExpression(String expression) {
		return this.findExpression(expression, false);
	}
	
	/**
	 * Checks to see whether this lexicon has this expression or not.
	 * @param expression the expression to look for.
	 * @param recursive a flag indicating whether to look recursively in the entire hierarchy or not.
	 * @return {@code true} if this lexicon contains the given expression; {@code false} otherwise.
	 */
	public boolean hasExpression(String expression, boolean recursive) {
		return this.findExpression(expression, recursive) != null;
	}
	
	/**
	 * Checks to see whether this lexicon has this expression or not (not recursively).
	 * @param expression the expression to look for.
	 * @return {@code true} if this lexicon contains the given expression; {@code false} otherwise.
	 */
	public boolean hasExpression(String expression) {
		return this.hasExpression(expression, false);
	}
	
	/**
	 * Adds the given expression to this lexicon.
	 * @param expression the expression to add.
	 * @return the {@link AspectExpression} object added, if added; {@code null} otherwise.
	 */
	public AspectExpression addExpression(String expression) {
		if (!this.hasExpression(expression)) {
			return (AspectExpression)new AspectExpression()
				.setContent(expression)
				.setStore(this);
		}
		
		return null;
	}
	
	/**
	 * Removes the given expression from this lexicon.
	 * @param expression the expression to remove.
	 * @return {@code true} if an expression was removed, {@code false} otherwise.
	 */
	public boolean removeExpression(String expression) {
		AspectExpression aspectExpression = this.findExpression(expression);
		return aspectExpression != null ? this.removeDocument(aspectExpression) : false;
	}

	/**
	 * Gets all the aspects stored under this lexicon.
	 * @return the {@link Iterable} of {@link AspectLexicon} items stored under this lexicon.
	 */
	public Iterable<AspectLexicon> getAspects() {
		return Iterables.filter(this.getDerivedStores(), AspectLexicon.class);
	}
	
	/**
	 * Finds an aspect in this lexicon.
	 * @param aspect the aspect title to look for.
	 * @param recursive a flag indicating whether to look recursively in the entire hierarchy or not.
	 * @return the {@link AspectLexicon} object for the aspect if present, {@code null} otherwise.
	 */
	public AspectLexicon findAspect(final String aspect, boolean recursive) {
		AspectLexicon aspectLexicon = Iterables.find(this.getAspects(), new Predicate<AspectLexicon>() {
			@Override
			public boolean apply(AspectLexicon input) {
				return aspect != null && aspect.equalsIgnoreCase(input.getTitle());
			}
		}, null);
		
		// look recursively.
		if (aspectLexicon == null && recursive) {
			for (AspectLexicon subAspect : this.getAspects()) {
				aspectLexicon = subAspect.findAspect(aspect, recursive);
				if (aspectLexicon != null) {
					break;
				}
			}
		}
		
		return aspectLexicon;
	}

	/**
	 * Finds an aspect in this lexicon (not recursively).
	 * @param aspect the aspect title to look for.
	 * @return the {@link AspectLexicon} object for the aspect if present, {@code null} otherwise.
	 */
	public AspectLexicon findAspect(String aspect) {
		return this.findAspect(aspect, false);
	}
	
	/**
	 * Checks to see whether the lexicon contains a given aspect or not.
	 * @param aspect the aspect title to look for.
	 * @param recursive a flag indicating whether to look recursively in the entire hierarchy or not.
	 * @return {@code true} if this lexicon contains such an aspect, {@code false} otherwise.
	 */
	public boolean hasAspect(String aspect, boolean recursive) {
		return this.findAspect(aspect, recursive) != null;
	}
	
	/**
	 * Checks to see whether the lexicon contains a given aspect or not (not recursively).
	 * @param aspect the aspect title to look for.
	 * @return {@code true} if this lexicon contains such an aspect, {@code false} otherwise.
	 */
	public boolean hasAspect(String aspect) {
		return this.hasAspect(aspect, false);
	}
	
	/**
	 * Adds a given aspect to this lexicon.
	 * @param aspect the aspect title to add.
	 * @return the {@link AspectLexicon} object that was added if it didn't exist, {@code null} otherwise.
	 */
	public AspectLexicon addAspect(String aspect) {
		if (!this.hasAspect(aspect)) {
			return (AspectLexicon)new AspectLexicon(this)
				.setTitle(aspect);
		}
		
		return null;
	}
	
	/**
	 * Removes a given aspect from this lexicon.
	 * @param aspect the aspect title to remove.
	 * @return the {@link AspectLexicon} object that was removed, or {@code null} if none was removed.
	 */
	public AspectLexicon removeAspect(String aspect) {
		AspectLexicon aspectLexicon = this.findAspect(aspect);
		if (aspectLexicon != null) {
			this.removeDerivedStore(aspectLexicon);
		}
		
		return aspectLexicon;
	}
	
	/**
	 * Migrates a given aspect to this lexicon.
	 * @param aspect the {@link AspectLexicon} object to migrate.
	 * @return {@code true} if the aspect was migrated, {@code false} otherwise.
	 */
	public boolean migrateAspect(AspectLexicon aspect) {
		Validate.notNull(aspect, CannedMessages.NULL_ARGUMENT, "aspect");
		
		if (aspect.getBaseStore() != null && aspect.getBaseStore().getIdentifier().equals(this.getIdentifier())) {
			return true;
		}
		
		if (this.hasAspect(aspect.getTitle())) {
			return false;
		}
		
		aspect.setBaseStore(this);
		return true;
	}
	
	/**
	 * Updates an aspect to a new value.
	 * @param aspect the original aspect.
	 * @param newValue the new value of the aspect.
	 * @return the {@link AspectLexicon} object that was updated; {@code null} if nothing was updated.
	 */
	public AspectLexicon updateAspect(String aspect, String newValue) {
		AspectLexicon aspectObj = this.findAspect(aspect);
		if (aspectObj == null) {
			return null;
		}
		
		if (this.hasAspect(newValue)) {
			return null;
		}
		
		aspectObj.setTitle(newValue);
		return aspectObj;
	}
	
	@Override
	public String getOwnerId() {
		return super.getOwnerId() == null && this.getBaseStore() != null ? this.getBaseStore().getOwnerId() : super.getOwnerId();
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