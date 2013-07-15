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

package edu.sabanciuniv.sentilab.sare.models.aspect

import scala.collection.JavaConversions._

import javax.persistence._

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.Validate._

import edu.sabanciuniv.sentilab.sare.models.base.documentStore._
import edu.sabanciuniv.sentilab.utils.CannedMessages

/**
 * A class that represents an aspect lexicon.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("aspect-lexicon")
class AspectLexicon(baseStore: PersistentDocumentStore)
	extends Lexicon {

	setBaseStore(baseStore)
	
	/**
	 * Creates an instance of the {@link AspectLexicon} class based on the provided store.
	 * @param baseStore the {@link PersistentDocumentStore} object to use as the base store for this instance.
	 */
	def this() = this(null)

	/**
	 * Gets the corpus this lexicon or its parent lexicon is based on, if any.
	 * @return the {@link DocumentCorpus} this lexicon is based on.
	 */
	def getBaseCorpus: DocumentCorpus = Option(getBaseStore) match {
	  	case Some(corpus: DocumentCorpus) => corpus
	  	case Some(lexicon: AspectLexicon) => lexicon.getBaseCorpus
	  	case _ => null
	}
	
	/**
	 * Gets the parent aspect of this aspect.
	 * @return the {@link AspectLexicon} object representing the parent aspect; {@code null} if none.
	 */
	def getParentAspect = Option(getBaseStore) match {
	  	case Some(lexicon: AspectLexicon) => lexicon
	  	case _ => null
	}

	/**
	 * Gets the base lexicon of this aspect.
	 * @return the {@link AspectLexicon} object representing the base lexicon; {@code this} object if it is the lexicon itself.
	 */
	def getBaseLexicon: AspectLexicon = Option(getParentAspect) map { _.getBaseLexicon } getOrElse this
	
	/**
	 * Gets the aspect expressions in this aspect.
	 * @return an {@link Iterable} of {@link AspectExpression} objects contained in this aspect.
	 */
	def getExpressions = getDocuments(classOf[AspectExpression])
	
	/**
	 * Finds the given expression within this lexicon.
	 * @param expression the expression to look for.
	 * @param recursive a flag indicating whether to look recursively in the entire hierarchy or not.
	 * @return the {@link AspectExpression} object for this expression.
	 */
	def findExpression(expression: String, recursive: Boolean): AspectExpression = Option(expression) map { expression =>
		getExpressions find {
			_ equals expression
		} getOrElse {
			if (recursive) {
				getAspects find { aspect => Option(aspect.findExpression(expression, recursive)).isDefined } map {
					_.findExpression(expression, recursive) } getOrElse null
			} else null
		}
	} getOrElse null
	
	/**
	 * Finds the given expression within this lexicon (not recursively).
	 * @param expression the expression to look for.
	 * @return the {@link AspectExpression} object for this expression.
	 */
	def findExpression(expression: String): AspectExpression = findExpression(expression, false)
	
	/**
	 * Checks to see whether this lexicon has this expression or not.
	 * @param expression the expression to look for.
	 * @param recursive a flag indicating whether to look recursively in the entire hierarchy or not.
	 * @return {@code true} if this lexicon contains the given expression; {@code false} otherwise.
	 */
	def hasExpression(expression: String, recursive: Boolean) = Option(findExpression(expression, recursive)).isDefined
	
	/**
	 * Checks to see whether this lexicon has this expression or not (not recursively).
	 * @param expression the expression to look for.
	 * @return {@code true} if this lexicon contains the given expression; {@code false} otherwise.
	 */
	def hasExpression(expression: String): Boolean = hasExpression(expression, false)
	
	/**
	 * Adds the given expression to this lexicon.
	 * @param expression the expression to add.
	 * @return the {@link AspectExpression} object added, if added; {@code null} otherwise.
	 */
	def addExpression(expression: String) = Option(expression) filter { expressions =>
	  	!expression.isEmpty && !hasExpression(expression)
	} map { expression =>
		new AspectExpression()
			.setContent(expression)
			.setStore(this).asInstanceOf[AspectExpression]
	} getOrElse null
	
	def +=(expression: String) = addExpression(expression)
	
	/**
	 * Removes the given expression from this lexicon.
	 * @param expression the expression to remove.
	 * @return the {@link AspectExpression} that was removed; {@code null} if none.
	 */
	def removeExpression(expression: String) = {
		val aspectExpression = findExpression(expression)
		if (removeDocument(aspectExpression)) aspectExpression else null
	}
	
	def -=(expression: String) = removeExpression(expression)

	/**
	 * Migrates a given expression to this lexicon.
	 * @param expression the {@link AspectExpression} object to migrate.
	 * @return {@code true} if the expression was migrated, {@code false} otherwise.
	 */
	def migrateExpression(expression: AspectExpression) = Option(expression) match {
	  	case Some(expression) if this.equals(expression.getAspect) => true
	  	case Some(expression) if hasExpression(expression.getContent) => false
	  	case Some(expression) => { expression.setStore(this); true; }
	  	case _ => false
	}

	/**
	 * Updates an expression to a new value.
	 * @param expression the original expression.
	 * @param newValue the new value of the expression
	 * @return the {@link AspectExpression} object that was update; {@code null} if nothing was updated.
	 */
	def updateExpression(expression: String, newValue: String) = Option(expression) map {
		findExpression(_)
	} filter { Option(_).isDefined } map { aspectExp =>
	  	Option(newValue) match {
	  	  	case Some(newValue) if StringUtils.equalsIgnoreCase(newValue, expression) => aspectExp
	  	  	case Some(newValue) if newValue.isEmpty || hasExpression(newValue) => null
	  	  	case Some(newValue) => aspectExp.setContent(newValue).asInstanceOf[AspectExpression]
	  	  	case _ => null
	  	}
	} getOrElse null
	
	/**
	 * Gets all the aspects stored under this lexicon.
	 * @return the {@link Iterable} of {@link AspectLexicon} items stored under this lexicon.
	 */
	def getAspects =
	  	asJavaIterable(getDerivedStores filter { _.isInstanceOf[AspectLexicon] } map { _.asInstanceOf[AspectLexicon] })
	
	/**
	 * Finds an aspect in this lexicon.
	 * @param aspect the aspect title to look for.
	 * @param recursive a flag indicating whether to look recursively in the entire hierarchy or not.
	 * @return the {@link AspectLexicon} object for the aspect if present, {@code null} otherwise.
	 */
	def findAspect(aspect: String, recursive: Boolean): AspectLexicon = Option(aspect) map { aspect =>
		getAspects find { aspectObj =>
			StringUtils.equalsIgnoreCase(aspect, aspectObj.getTitle)
		} getOrElse {
			if (recursive) {
				getAspects find { subAspect =>
				  	Option(subAspect.findAspect(aspect, recursive)).isDefined
				} map { _.findAspect(aspect, recursive) } getOrElse null
			} else null
		}
	} getOrElse null

	/**
	 * Finds an aspect in this lexicon (not recursively).
	 * @param aspect the aspect title to look for.
	 * @return the {@link AspectLexicon} object for the aspect if present, {@code null} otherwise.
	 */
	def findAspect(aspect: String): AspectLexicon = findAspect(aspect, false)
	
	/**
	 * Checks to see whether the lexicon contains a given aspect or not.
	 * @param aspect the aspect title to look for.
	 * @param recursive a flag indicating whether to look recursively in the entire hierarchy or not.
	 * @return {@code true} if this lexicon contains such an aspect, {@code false} otherwise.
	 */
	def hasAspect(aspect: String, recursive: Boolean) = Option(this.findAspect(aspect, recursive)).isDefined
	
	/**
	 * Checks to see whether the lexicon contains a given aspect or not (not recursively).
	 * @param aspect the aspect title to look for.
	 * @return {@code true} if this lexicon contains such an aspect, {@code false} otherwise.
	 */
	def hasAspect(aspect: String): Boolean = hasAspect(aspect, false)
	
	/**
	 * Adds a given aspect to this lexicon.
	 * @param aspect the aspect title to add.
	 * @return the {@link AspectLexicon} object that was added if it didn't exist, {@code null} otherwise.
	 */
	def addAspect(aspect: String) = Option(aspect) filter { aspect => !aspect.isEmpty && !hasAspect(aspect) } map {
		new AspectLexicon(this).setTitle(_).asInstanceOf[AspectLexicon]
	} getOrElse null
	
	/**
	 * Removes a given aspect from this lexicon.
	 * @param aspect the aspect title to remove.
	 * @return the {@link AspectLexicon} object that was removed, or {@code null} if none was removed.
	 */
	def removeAspect(aspect: String) = Option(aspect) map { findAspect(_) } map { aspect =>
	  	removeDerivedStore(aspect)
	  	aspect
	} getOrElse null
	
	/**
	 * Migrates a given aspect to this lexicon.
	 * @param aspect the {@link AspectLexicon} object to migrate.
	 * @return {@code true} if the aspect was migrated, {@code false} otherwise.
	 */
	def migrateAspect(aspect: AspectLexicon) = Option(aspect) match {
	  	case Some(aspect) if this.equals(aspect.getParentAspect) => true
	  	case Some(aspect) if this.hasAspect(aspect.getTitle) => false
	  	case Some(aspect) => { aspect.setBaseStore(this); true; }
	  	case _ => false
	}

	/**
	 * Updates an aspect to a new value.
	 * @param aspect the original aspect.
	 * @param newValue the new value of the aspect.
	 * @return the {@link AspectLexicon} object that was updated; {@code null} if nothing was updated.
	 */
	def updateAspect(aspect: String, newValue: String) = Option(aspect) map {
		findAspect(_)
	} filter { Option(_).isDefined } map { aspectObj =>
	  	Option(newValue) match {
	  	  	case Some(newValue) if StringUtils.equalsIgnoreCase(newValue, aspect) => aspectObj
	  	  	case Some(newValue) if newValue.isEmpty || hasAspect(newValue) => null
	  	  	case Some(newValue) => aspectObj.setTitle(newValue).asInstanceOf[AspectLexicon]
	  	  	case _ => null
	  	}
	} getOrElse null
	
	override def getOwnerId = (Option(super.getOwnerId), Option(getBaseStore)) match {
	  	case (Some(ownerId), _) => ownerId
	  	case (_, Some(baseStore)) => baseStore.getOwnerId
	  	case _ => null
	}
	
	override def getTitle = (Option(super.getTitle), Option(getBaseStore)) match {
	  	case (Some(title), _) => title
	  	case (_, Some(baseStore)) => baseStore.getTitle
	  	case _ => null
	}
	
	override def getLanguage = (Option(super.getLanguage), Option(getBaseStore)) match {
	  	case (Some(language), _) => language
	  	case (_, Some(baseStore)) => baseStore.getLanguage
	  	case _ => null
	}
	
	override def getDescription = (Option(super.getDescription), Option(getBaseStore)) match {
	  	case (Some(description), _) => description
	  	case (_, Some(baseStore)) => baseStore.getDescription
	  	case _ => null
	}
	
	override def toString = String.format("%s (%s)", getTitle(),
	    this.getExpressions map { _.toString } reduce((acc, next) => String.format("%s,%s", acc, next)))
}