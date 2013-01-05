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

package edu.sabanciuniv.sentilab.sare.controllers.opinion;

import javax.xml.xpath.*;

import org.apache.commons.lang3.Validate;
import org.w3c.dom.Node;

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.base.PersistentObjectFactory;
import edu.sabanciuniv.sentilab.sare.controllers.base.document.IDocumentController;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A factory for creating {@link OpinionDocument} objects.
 * @author Mus'ab Husaini
 */
public class OpinionDocumentFactory
	extends PersistentObjectFactory<OpinionDocument, OpinionDocumentFactoryOptions>
	implements IDocumentController {

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
	protected OpinionDocument createPrivate(OpinionDocumentFactoryOptions options)
		throws IllegalFactoryOptionsException {
		
		Validate.notNull(options, CannedMessages.NULL_ARGUMENT, "options");
		
		boolean existing = true;
		OpinionDocument document = null;
		if (options.getExistingId() != null && options.getEm() != null) {
			// if not found, we simply fall-back to the default behavior.
			try {
				document = options.getEm().find(OpinionDocument.class, options.getExistingId());
			} catch (IllegalArgumentException e) {
				document = null;
			}
		}
		
		if (document == null) {
			document = new OpinionDocument();
			existing = false;
		}
		
		if (options.getContent() != null) {
			return this.create(document, options.getCorpus(), options.getContent(), options.getPolarity());
		}
		if (options.getXmlNode() != null) {
			try {
				return this.create(document, options.getCorpus(), options.getXmlNode());
			} catch (XPathException e) {
				throw new IllegalFactoryOptionsException("options.xmlNode is not a valid input", e);
			}
		}
		
		if (!existing) {
			throw new IllegalFactoryOptionsException("options did not have enough information to create this object");
		}
		
		return document;
	}
}