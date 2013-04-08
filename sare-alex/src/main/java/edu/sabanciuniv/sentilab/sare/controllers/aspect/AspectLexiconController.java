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

package edu.sabanciuniv.sentilab.sare.controllers.aspect;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.apache.commons.lang3.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.base.documentStore.NonDerivedStoreFactory;
import edu.sabanciuniv.sentilab.sare.models.aspect.*;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A controller for creating and manipulating {@link AspectLexicon} objects.
 * @author Mus'ab Husaini
 */
public class AspectLexiconController
		extends NonDerivedStoreFactory<AspectLexicon> {

	private PersistentDocumentStore baseStore;

	@Override
	protected AspectLexiconController addXmlPacket(AspectLexicon lexicon, InputStream input)
		throws ParserConfigurationException, SAXException, IOException, XPathException {
		
		Validate.notNull(lexicon, CannedMessages.NULL_ARGUMENT, "lexicon");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	    domFactory.setNamespaceAware(true);
	    Document doc = domFactory.newDocumentBuilder().parse(input);

	    XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();
	    
	    if ("aspect".equalsIgnoreCase(doc.getDocumentElement().getLocalName())) {
	    	return this.addXmlAspect(lexicon, doc.getDocumentElement());
	    }
	    
	    Node lexiconNode = (Node)xpath.compile("/lexicon").evaluate(doc, XPathConstants.NODE);
	    if (lexiconNode == null) {
	    	lexiconNode = Validate.notNull(doc.getDocumentElement(), CannedMessages.NULL_ARGUMENT, "/lexicon");
	    }
	    
	    String title = (String)xpath.compile("./@title").evaluate(lexiconNode, XPathConstants.STRING);
	    String description = (String)xpath.compile("./@description").evaluate(lexiconNode, XPathConstants.STRING);
	    
	    if (StringUtils.isNotEmpty(title)) {
	    	lexicon.setTitle(title);
	    }
	    
	    if (StringUtils.isNotEmpty(description)) {
	    	lexicon.setDescription(description);
	    }
		
	    return this.addXmlAspect(lexicon, lexiconNode);
	}
	
	protected AspectLexiconController addXmlAspect(AspectLexicon lexicon, Node aspectNode)
		throws XPathExpressionException {
		
		Validate.notNull(lexicon, CannedMessages.NULL_ARGUMENT, "lexicon");
		Validate.notNull(aspectNode, CannedMessages.NULL_ARGUMENT, "node");

	    XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();
	    
	    // if the node is called "lexicon" then we're at the root, so we won't need to add an aspect and its expressions.
		AspectLexicon aspect = lexicon;
		if (!"lexicon".equalsIgnoreCase(aspectNode.getLocalName())) {
			String title = Validate.notEmpty((String)xpath.compile("./@title").evaluate(aspectNode, XPathConstants.STRING),
				CannedMessages.EMPTY_ARGUMENT, "./aspect/@title");;
			
			// fetch or create aspect.
			aspect = lexicon.findAspect(title);
			if (aspect == null) {
				aspect = lexicon.addAspect(title);
			}
			
			// get all expressions or keywords, whatever they're called.
		    NodeList expressionNodes = (NodeList)xpath.compile("./expressions/expression").evaluate(aspectNode, XPathConstants.NODESET);
		    if (expressionNodes == null || expressionNodes.getLength() == 0) {
		    	expressionNodes = (NodeList)xpath.compile("./keywords/keyword").evaluate(aspectNode, XPathConstants.NODESET);
		    }
		    
		    // add each of them if they don't exist.
		    if (expressionNodes != null) {
		    	for (int index=0; index<expressionNodes.getLength(); index++) {
		    		String expression = expressionNodes.item(index).getTextContent().trim();
		    		if (!aspect.hasExpression(expression)) {
		    			aspect.addExpression(expression);
		    		}
		    	}
		    }
		}
		
		// get all sub-aspects and add them recursively.
		NodeList subAspectNodes = (NodeList)xpath.compile("./aspects/aspect").evaluate(aspectNode, XPathConstants.NODESET);
	    if (subAspectNodes != null) {
		    for (int index=0; index<subAspectNodes.getLength(); index++) {
		    	this.addXmlAspect(aspect, subAspectNodes.item(index));
		    }
	    }
	    
		return this;
	}
	
	@Override
	protected AspectLexiconController addTextPacket(AspectLexicon lexicon, InputStream input, String delimiter)
		throws IOException {
		
		throw new IllegalFactoryOptionsException();
	}

	@Override
	protected AspectLexicon createNew() {
		return new AspectLexicon();
	}
	
	@Override
	protected AspectLexicon createPrivate(AspectLexicon lexicon)
		throws IllegalFactoryOptionsException {
		
		if (lexicon == null) {
			lexicon = new AspectLexicon(this.getBaseStore());
		}
		
		super.createPrivate(lexicon);
		
		return lexicon;
	}

	/**
	 * Gets the base store that will be set for the lexicon.
	 * @return the {@link PersistentDocumentStore} object representing the base store.
	 */
	public PersistentDocumentStore getBaseStore() {
		return this.baseStore;
	}

	/**
	 * Sets the base store to set for the lexicon (can be null).
	 * @param baseStore the {@link PersistentDocumentStore} object representing the base store to be set.
	 * @return the {@code this} object.
	 */
	public AspectLexiconController setBaseStore(PersistentDocumentStore baseStore) {
		this.baseStore = baseStore;
		return this;
	}
}