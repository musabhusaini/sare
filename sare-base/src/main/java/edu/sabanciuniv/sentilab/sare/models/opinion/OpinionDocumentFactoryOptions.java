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

package edu.sabanciuniv.sentilab.sare.models.opinion;

import org.w3c.dom.Node;

import edu.sabanciuniv.sentilab.sare.models.base.PersistentObjectFactoryOptions;

/**
 * The default set of options that can be used to construct an {@link OpinionDocument} object.
 * The most specific combination of properties will be used.
 * @author Mus'ab Husaini
 */
public class OpinionDocumentFactoryOptions
	extends PersistentObjectFactoryOptions<OpinionDocument> {

	private OpinionCorpus corpus;
	private Node xmlNode;
	private String content;
	private Double polarity;
	
	/**
	 * Gets the corpus the document will be stored in.
	 * @return the {@link OpinionCorpus} object representing the corpus of documents.
	 */
	public OpinionCorpus getCorpus() {
		return this.corpus;
	}
	
	/**
	 * Sets the corpus to store the document in.
	 * @param corpus the {@link OpinionCorpus} object to set.
	 * @return the {@code this} object.
	 */
	public OpinionDocumentFactoryOptions setCorpus(OpinionCorpus corpus) {
		this.corpus = corpus;
		return this;
	}
	
	/**
	 * Gets the XML node to create the document from, if the document is to be created from an XML node.
	 * @return the {@link Node} object representing the XML node.
	 */
	public Node getXmlNode() {
		return this.xmlNode;
	}
	
	/**
	 * Sets the XML node the document will be created from.
	 * @param xmlNode the {@link Node} object representing the XML node to create the document from.
	 * @return the {@code this} object.
	 */
	public OpinionDocumentFactoryOptions setXmlNode(Node xmlNode) {
		this.xmlNode = xmlNode;
		return this;
	}
	
	/**
	 * Gets the text content of the document to be created.
	 * @return the {@link String} content of the document.
	 */
	public String getContent() {
		return this.content;
	}
	
	/**
	 * Sets the text content of the document to be created.
	 * @param content the {@link String} content of the document.
	 * @return the {@code this} object.
	 */
	public OpinionDocumentFactoryOptions setContent(String content) {
		this.content = content;
		return this;
	}
	
	/**
	 * Gets the polarity of the document to be created.
	 * @return the {@link Double} polarity of the opinion document.
	 */
	public Double getPolarity() {
		return this.polarity;
	}
	
	/**
	 * Sets the polarity of the document to be created.
	 * @param polarity the {@link Double} polarity of the document.
	 * @return the {@code this} object.
	 */
	public OpinionDocumentFactoryOptions setPolarity(Double polarity) {
		this.polarity = polarity;
		return this;
	}
}