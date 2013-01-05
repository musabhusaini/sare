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

import java.util.UUID;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Node;

import edu.sabanciuniv.sentilab.sare.models.base.PersistentObjectFactoryOptions;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

/**
 * The default set of options that can be used to construct an {@link OpinionDocument} object.
 * The most specific combination of properties will be used.
 * @author Mus'ab Husaini
 */
public class OpinionDocumentFactoryOptions
	extends PersistentObjectFactoryOptions<OpinionDocument> {

	private byte[] existingId;
	private OpinionCorpus corpus;
	private Node xmlNode;
	private String content;
	private Double polarity;
	private EntityManager em;
	
	/**
	 * Gets the ID of an existing document to be modified.
	 * @return the ID to fetch the document with.
	 */
	public byte[] getExistingId() {
		return this.existingId;
	}

	/**
	 * Sets the ID of an existing document to be modified. Must also provide a non-{@code null} value for {@code em}
	 * if this is {@code null}
	 * @param id the ID to fetch the document with.
	 * @return the {@code this} object.
	 */
	public OpinionDocumentFactoryOptions setExistingId(byte[] id) {
		this.existingId = id;
		return this;
	}
	
	/**
	 * Sets the ID of an existing document to be modified. Must also provide a non-{@code null} value for {@code em}
	 * if this is {@code null}
	 * @param id the ID to fetch the document with.
	 * @return the {@code this} object.
	 */
	public OpinionDocumentFactoryOptions setExistingId(String id) {
		if (StringUtils.isEmpty(id)) {
			return this;
		}
		
		return this.setExistingId(UuidUtils.toBytes(id));
	}

	/**
	 * Sets the ID of an existing document to be modified. Must also provide a non-{@code null} value for {@code em}
	 * if this is {@code null}
	 * @param id the ID to fetch the document with.
	 * @return the {@code this} object.
	 */
	public OpinionDocumentFactoryOptions setExistingId(UUID id) {
		if (id == null) {
			return this;
		}
		
		return this.setExistingId(UuidUtils.toBytes(id));
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
	
	/**
	 * Gets an entity manager that will be used in case an ID was provided.
	 * @return the {@link EntityManager} that will be used.
	 */
	public EntityManager getEm() {
		return this.em;
	}

	/**
	 * Sets the entity manager to use in case an ID was provided. Only needed if an existing ID was provided.
	 * @param em the {@link EntityManager} to be set.
	 * @return the {@code this} object.
	 */
	public OpinionDocumentFactoryOptions setEm(EntityManager em) {
		this.em = em;
		return this;
	}
}