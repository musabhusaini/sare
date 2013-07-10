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

package edu.sabanciuniv.sentilab.sare.controllers.opinion;

import javax.xml.xpath.*;

import org.apache.commons.lang3.Validate;
import org.w3c.dom.Node;

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.base.PersistentObjectFactory;
import edu.sabanciuniv.sentilab.sare.controllers.base.document.DocumentController;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A factory for creating {@link OpinionDocument} objects.
 * @author Mus'ab Husaini
 */
public class OpinionDocumentFactory
		extends PersistentObjectFactory<OpinionDocument>
		implements DocumentController {

	private OpinionCorpus corpus;
	private Node xmlNode;
	private String content;
	private Double polarity;

	private OpinionDocument create(OpinionDocument document, OpinionCorpus corpus, String content, Double polarity) {
		if (corpus != null) {
			document.setStore(corpus);
		}
		
		if (content != null) {
			document.setContent(content);
		}
		
		if (polarity != null) {
			document.setPolarity(polarity);
		}
		
		return document;
	}

	private OpinionDocument create(OpinionDocument document, OpinionCorpus corpus, Node node)
		throws XPathException {

		try {
			Validate.notNull(node, CannedMessages.NULL_ARGUMENT, "node");
		} catch (NullPointerException e) {
			throw new IllegalFactoryOptionsException(e);
		}
		
		XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();
	    Double polarity = (Double)xpath.compile("./@polarity").evaluate(node, XPathConstants.NUMBER);
	    
	    return this.create(document, corpus, node.getTextContent().trim(), polarity);
	}

	@Override
	protected OpinionDocument createPrivate(OpinionDocument document)
			throws IllegalFactoryOptionsException {
		
		boolean existing = true;
		if (document == null) {
			document = new OpinionDocument();
			existing = false;
		}
		
		if (this.getContent() != null) {
			return this.create(document, this.getCorpus(), this.getContent(), this.getPolarity());
		}
		if (this.getXmlNode() != null) {
			try {
				return this.create(document, this.getCorpus(), this.getXmlNode());
			} catch (XPathException e) {
				throw new IllegalFactoryOptionsException("options.xmlNode is not a valid input", e);
			}
		}
		
		if (!existing) {
			throw new IllegalFactoryOptionsException("options did not have enough information to create this object");
		}
		
		return document;
	}
	
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
	public OpinionDocumentFactory setCorpus(OpinionCorpus corpus) {
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
	public OpinionDocumentFactory setXmlNode(Node xmlNode) {
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
	public OpinionDocumentFactory setContent(String content) {
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
	public OpinionDocumentFactory setPolarity(Double polarity) {
		this.polarity = polarity;
		return this;
	}
}