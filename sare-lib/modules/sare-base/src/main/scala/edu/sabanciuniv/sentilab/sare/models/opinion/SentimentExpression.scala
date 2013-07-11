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

import javax.persistence._

import org.apache.commons.lang3.StringUtils._

import edu.sabanciuniv.sentilab.sare.models.base.document._

object SentimentExpression {
	private val negativeString = "negative"
	private val neutralString = "neutral"
	private val positiveString = "positive"
}

/**
 * A single sentiment expressions.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("sentiment-expression")
class SentimentExpression(expression: String, negative: java.lang.Double, neutral: java.lang.Double, positive: java.lang.Double)
	extends LexiconDocument {
	
	setContent(expression)
	setNegative(negative)
	setNeutral(neutral)
	setPositive(positive)
  
	def this(expression: String, negative: java.lang.Double, positive: java.lang.Double) =
	  	this(expression, negative, null, positive)
	
	def this(expression: String, positive: java.lang.Double) = this(expression, null, positive)
	
	def this(expression: String) = this(expression, null)
	
	def this() = this(null)
	
	/**
	 * Gets the negative polarity of this expression.
	 * @return the negative polarity of this expression.
	 */
	def getNegative = getProperty(SentimentExpression.negativeString, classOf[Double])
	
	/**
	 * Sets the negative polarity of this expression.
	 * @param value the negative polarity to set.
	 * @return the {@code this} object.
	 */
	def setNegative(value: java.lang.Double) = setProperty(SentimentExpression.negativeString, value)
	
	/**
	 * Gets the neutral polarity of this expression.
	 * @return the neutral polarity of this expression.
	 */
	def getNeutral = getProperty(SentimentExpression.neutralString, classOf[Double])
	
	/**
	 * Sets the neutral polarity of this expression.
	 * @param value the neutral polarity to set.
	 * @return the {@code this} object.
	 */
	def setNeutral(value: java.lang.Double) = setProperty(SentimentExpression.neutralString, value)
	
	/**
	 * Gets the positive polarity of this expression.
	 * @return the positive polarity of this expression.
	 */
	def getPositive = getProperty(SentimentExpression.positiveString, classOf[Double])
	
	/**
	 * Sets the positive polarity of this expression.
	 * @param value the positive polarity to set.
	 * @return the {@code this} object.
	 */
	def setPositive(value: java.lang.Double) = setProperty(SentimentExpression.positiveString, value)
	
	override def equals(obj: Any) = obj match {
	  	case sentExp: SentimentExpression => {
	  		equalsIgnoreCase(getContent, sentExp.getContent) &&
	  		((Option(getNegative), Option(sentExp.getNegative)) match {
	  		  	case (Some(neg), Some(otherNeg)) => neg == otherNeg
	  		  	case _ => true
	  		}) &&
	  		((Option(getNeutral), Option(sentExp.getNeutral)) match {
	  		  	case (Some(neu), Some(otherNeu)) => neu == otherNeu
	  		  	case _ => true
	  		}) &&
	  		((Option(getPositive), Option(sentExp.getPositive)) match {
	  		  	case (Some(pos), Some(otherPos)) => pos == otherPos
	  		  	case _ => true
	  		})
	  	}
	  	case _ => super.equals(obj)
	}
	
	override def hashCode =
		(Option(getNegative) map { _.hashCode } getOrElse 0) +
		(Option(getNeutral) map { _.hashCode } getOrElse 0) +
		(Option(getPositive) map { _.hashCode } getOrElse 0) +
		(Option(getContent) map { _.hashCode } getOrElse 0)
		
	override def toString = defaultString(getContent) + "(" +
		"negative=" + defaultString(Option(getNegative) map { _.toString } getOrElse null, "null") + "," +
		"neutral=" + defaultString(Option(getNeutral) map { _.toString } getOrElse null, "null") + "," +
		"positive=" + defaultString(Option(getPositive) map { _.toString } getOrElse null, "null") + ")"
}