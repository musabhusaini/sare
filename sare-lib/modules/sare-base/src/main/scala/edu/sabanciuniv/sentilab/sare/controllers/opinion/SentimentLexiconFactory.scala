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

package edu.sabanciuniv.sentilab.sare.controllers.opinion

import java.io.InputStream

import javax.xml.parsers._
import javax.xml.xpath._

import scala.io.Source
import scala.collection.JavaConversions._

import org.w3c.dom._

import org.apache.commons.lang3._
import org.apache.commons.lang3.text._
import org.apache.commons.lang3.Validate._
import org.apache.commons.lang3.StringUtils._

import edu.sabanciuniv.sentilab.sare.controllers.base.documentStore._
import edu.sabanciuniv.sentilab.sare.models.opinion._
import edu.sabanciuniv.sentilab.utils.CannedMessages

class SentimentLexiconFactory extends NonDerivedStoreFactory[SentimentLexicon] {
	
	protected override def addXmlPacket(lexicon: SentimentLexicon, input: InputStream) = {
		notNull(lexicon, CannedMessages.NULL_ARGUMENT, "lexicon")
		notNull(input, CannedMessages.NULL_ARGUMENT, "input")
		
		val domFactory = DocumentBuilderFactory.newInstance
	    domFactory.setNamespaceAware(true)
	    val doc = domFactory.newDocumentBuilder.parse(input)

	    val factory = XPathFactory.newInstance
	    val xpath = factory.newXPath
	    
	    val lexiconNode = Option(xpath.compile("/sentiment-lexicon").evaluate(doc, XPathConstants.NODE)) match {
		  	case Some(node: Node) => node
		  	case _ => notNull(doc.getDocumentElement, CannedMessages.NULL_ARGUMENT, "/sentiment-lexicon")
		}
		
		Option(xpath.compile("./@title").evaluate(lexiconNode, XPathConstants.STRING)) match {
		  	case Some(title: String) => lexicon.setTitle(title)
		  	case _ => ()
		}
		
		Option(xpath.compile("./@description").evaluate(lexiconNode, XPathConstants.STRING)) match {
		  	case Some(description: String) => lexicon.setDescription(description)
		  	case _ => ()
		}
		
		Option(xpath.compile("./@language").evaluate(lexiconNode, XPathConstants.STRING)) match {
		  	case Some(language: String) => lexicon.setLanguage(language)
		  	case _ => ()
		}
		
		val expressionNodes = Option(xpath.compile("./expressions/expression").evaluate(lexiconNode, XPathConstants.NODESET)) match {
		  	case Some(nodes: NodeList) if nodes.getLength == 0 => {
		  		Option(xpath.compile("./words/word").evaluate(lexiconNode, XPathConstants.NODESET)) match {
		  		  	case Some(nodes: NodeList) => nodes
		  		  	case _ => null
		  		}
		  	}
		  	case Some(nodes: NodeList) => nodes
		  	case _ => null
		}
		
		def extractPolarity = (node: Node, path: String) =>
			Option(xpath.compile(path).evaluate(node, XPathConstants.NUMBER)) match {
  			  	case Some(pol: java.lang.Double) => pol
  			  	case _ => null
  			}
		
		Option(expressionNodes) foreach { nodes =>
		  	for ( index <- 0 until nodes.getLength ) {
		  		val node = expressionNodes.item(index)
		  		lexicon.addExpression(node.getTextContent,
	  			    Option(xpath.compile("./@pos-tag").evaluate(node, XPathConstants.STRING)) match {
	  			    	case Some(posTag: String) => posTag
	  			    	case _ => null
	  				},
	  			    extractPolarity(node, "./@negative"),
	  			    extractPolarity(node, "./@neutral"),
	  			    extractPolarity(node, "./@positive")
	  			)
		  	}
		}
		
		this
	}
	
	protected override def addTextPacket(lexicon: SentimentLexicon, input: InputStream, delimiter: String) = {
		notNull(lexicon, CannedMessages.NULL_ARGUMENT, "lexicon")
		notNull(input, CannedMessages.NULL_ARGUMENT, "input")
		
		val delim = defaultString(delimiter, "\t")
		val expressions = Source.fromInputStream(input).getLines filter { !_.trim.isEmpty } map { line =>
		  	val tokenizer = new StrTokenizer(line, StrMatcher.stringMatcher(delim), StrMatcher.quoteMatcher)
			val columns = tokenizer.getTokenList
			columns.size match {
			  	case size if size == 1 => new SentimentExpression(columns.get(0))
			  	case size if size == 2 => new SentimentExpression(columns.get(0), columns.get(1))
			  	case size if size == 3 => new SentimentExpression(columns.get(0), columns.get(1), positive = columns.get(2).toDouble)
			  	case size if size == 4 => new SentimentExpression(columns.get(0), columns.get(1), columns.get(2).toDouble, positive = columns.get(3).toDouble)
			  	case size if size >= 5 => new SentimentExpression(columns.get(0), columns.get(1), columns.get(2).toDouble, columns.get(3).toDouble, columns.get(4).toDouble)
			  	case _ => null
			}
		} filter { Option(_).isDefined } toIterable
		
		lexicon ++= expressions
		
		this
	}
	
	protected override def createNew = new SentimentLexicon
}