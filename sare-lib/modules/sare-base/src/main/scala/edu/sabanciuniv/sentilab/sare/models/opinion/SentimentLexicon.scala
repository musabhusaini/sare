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

package edu.sabanciuniv.sentilab.sare.models.opinion

import scala.collection.JavaConversions._

import javax.persistence._

import edu.sabanciuniv.sentilab.sare.models.base.documentStore._

/**
 * A sentiment lexicon.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("sentiment-lexicon")
class SentimentLexicon extends Lexicon {

	/**
	 * Gets the expressions in this lexicon.
	 * @return an {@link Iterable} of {@link SentimentExpression} objects.
	 */
	def getExpressions = getDocuments(classOf[SentimentExpression])
	
	/**
	 * Finds the given expression in this lexicon.
	 * @param expression the expression to search for.
	 * @param pos the POS of the expression.
	 * @return the {@link SentimentExpression} object found; {@code null} if none.
	 */
	def findExpression(expression: String, pos: String) = Option(expression) map { exp =>
	  	val sentExp = new SentimentExpression(exp, pos)
	  	getExpressions find { _.equals(sentExp) } getOrElse null
	} getOrElse null
	
	/**
	 * Finds all instances of the given expression in this lexicon.
	 * @param expression the expression to search for.
	 * @return an {@link Iterable} of {@link SentimentExpression} objects found.
	 */
	def findExpressions(expression: String): java.lang.Iterable[SentimentExpression] = asJavaIterable(
	    Option(expression) map { exp =>
		  	val sentExp = new SentimentExpression(exp)
		  	getExpressions filter { _.equals(sentExp) }
		} getOrElse Seq()
	)
	
	/**
	 * Checks if this lexicon has the given expression or not.
	 * @param expression the expression to look for.
	 * @return {@code true} if the lexicon contains the given expression; {@code false} otherwise.
	 */
	def hasExpression(expression: String, pos: String = null) = findExpression(expression, pos) != null
	
	/**
	 * Adds the given expression to this lexicon.
	 * @param expression the expression to add.
	 * @param negative the negative polarity of the expression.
	 * @param neutral the neutral polarity of the expression.
	 * @param positive the positive polarity of the expression.
	 * @return the {@link SentimentExpression} object added.
	 */
	def addExpression(expression: String, pos: String = null, negative: java.lang.Double = null, neutral: java.lang.Double = null, positive: java.lang.Double = null) =
	  	Option(expression) filter { !hasExpression(_, pos) } map { exp =>
	  		val sentExp = new SentimentExpression(expression, pos, negative, neutral, positive)
	  		addDocument(sentExp)
	  		sentExp
	  	} getOrElse null
	
	/**
	 * Removes the given expression from this lexicon.
	 * @param expression the expression to remove.
	 * @return the {@link SentimentExpression} object that was removed; {@code null} if none.
	 */
	def removeExpression(expression: String, pos: String) = Option(expression) map {
	  	findExpression(_, pos)
	} filter { _ != null } map { sentExp =>
	  	removeDocument(sentExp)
	  	sentExp
	} getOrElse null
	
	/**
	 * Updates the given expression to a new value.
	 * @param expression the expression to update.
	 * @param newValue the new value of the expression.
	 * @return the {@link SentimentExpression} that was updated.
	 */
	def updateExpression(expression: String, pos: String, newValue: String) = Option(expression) map { exp =>
	  	findExpression(exp, pos)
	} filter { _ != null } map { sentExp =>
	  	Option(newValue) filter { newValue => !hasExpression(newValue, pos) } map { newValue =>
	  		sentExp.setContent(newValue).asInstanceOf[SentimentExpression]
	  	} getOrElse null
	} getOrElse null
}