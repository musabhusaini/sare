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
	private val posTagString = "posTag"
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
class SentimentExpression(expression: String, posTag: String, negative: java.lang.Double, neutral: java.lang.Double, positive: java.lang.Double)
	extends LexiconDocument {
	
	setContent(expression)
	setPosTag(posTag)
	setNegative(negative)
	setNeutral(neutral)
	setPositive(positive)
  
	def this(expression: String, pos: String, negative: java.lang.Double, positive: java.lang.Double) =
	  	this(expression, pos, negative, null, positive)
	
	def this(expression: String, pos: String, positive: java.lang.Double) = this(expression, pos, null, positive)
	
	def this(expression: String, pos: String) = this(expression, pos, null)
	
	def this(expression: String) = this(expression, null)
	
	def this() = this(null)
	
	override def setContent(value: String) = super.setContent(
	    Option(value) map { _.trim.toLowerCase } getOrElse null
	)
	
	/**
	 * Gets the POS tag of this expression.
	 * @return the POS tag of this expression.
	 */
	def getPosTag = getProperty(SentimentExpression.posTagString, classOf[String])
	
	/**
	 * Sets the POS tag of this expression.
	 * @param value the POS tag to set.
	 * @return the {@code this} object.
	 */
	def setPosTag(value: String) = setProperty(SentimentExpression.posTagString,
	    Option(value) map { _.trim.toLowerCase } getOrElse null
	).asInstanceOf[SentimentExpression]
	
	/**
	 * Gets the negative polarity of this expression.
	 * @return the negative polarity of this expression.
	 */
	def getNegative = getProperty(SentimentExpression.negativeString, classOf[java.lang.Double])
	
	/**
	 * Sets the negative polarity of this expression.
	 * @param value the negative polarity to set.
	 * @return the {@code this} object.
	 */
	def setNegative(value: java.lang.Double) = setProperty(SentimentExpression.negativeString, value).asInstanceOf[SentimentExpression]
	
	/**
	 * Gets the neutral polarity of this expression.
	 * @return the neutral polarity of this expression.
	 */
	def getNeutral = getProperty(SentimentExpression.neutralString, classOf[java.lang.Double])
	
	/**
	 * Sets the neutral polarity of this expression.
	 * @param value the neutral polarity to set.
	 * @return the {@code this} object.
	 */
	def setNeutral(value: java.lang.Double) = setProperty(SentimentExpression.neutralString, value).asInstanceOf[SentimentExpression]
	
	/**
	 * Gets the positive polarity of this expression.
	 * @return the positive polarity of this expression.
	 */
	def getPositive = getProperty(SentimentExpression.positiveString, classOf[java.lang.Double])
	
	/**
	 * Sets the positive polarity of this expression.
	 * @param value the positive polarity to set.
	 * @return the {@code this} object.
	 */
	def setPositive(value: java.lang.Double) = setProperty(SentimentExpression.positiveString, value).asInstanceOf[SentimentExpression]
	
	override def equals(obj: Any) = obj match {
	  	case sentExp: SentimentExpression => {
	  		equalsIgnoreCase(getContent, sentExp.getContent) &&
	  		((Option(getPosTag), Option(sentExp.getPosTag)) match {
	  		  	case (Some(posTag), Some(otherPosTag)) => equalsIgnoreCase(posTag, otherPosTag)
	  		  	case _ => true
	  		}) &&
	  		((Option(getNegative), Option(sentExp.getNegative)) match {
	  		  	case (Some(negative), Some(otherNegative)) => negative == otherNegative
	  		  	case _ => true
	  		}) &&
	  		((Option(getNeutral), Option(sentExp.getNeutral)) match {
	  		  	case (Some(neutral), Some(otherNeutral)) => neutral == otherNeutral
	  		  	case _ => true
	  		}) &&
	  		((Option(getPositive), Option(sentExp.getPositive)) match {
	  		  	case (Some(positive), Some(otherPositive)) => positive == otherPositive
	  		  	case _ => true
	  		})
	  	}
	  	case _ => super.equals(obj)
	}
	
	override def hashCode =
		(Option(getNegative) map { _.hashCode } getOrElse 0) +
		(Option(getNeutral) map { _.hashCode } getOrElse 0) +
		(Option(getPositive) map { _.hashCode } getOrElse 0) +
		(Option(getContent) map { _.hashCode } getOrElse 0) +
		(Option(getPosTag) map { _.hashCode } getOrElse 0)
		
	override def toString = String.format("%s - %s (negative=%f, neutral=%f, positive=%f)",
	    defaultString(getContent),
	    defaultString(getPosTag),
	    getNegative,
	    getNeutral,
		getPositive
	)
}