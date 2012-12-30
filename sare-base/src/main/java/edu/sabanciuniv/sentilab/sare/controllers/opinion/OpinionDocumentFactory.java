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

	private OpinionDocument create(OpinionCorpus corpus, String content, Double polarity) {
		return (OpinionDocument)new OpinionDocument()
			.setPolarity(polarity)
			.setContent(content)
			.setStore(corpus);
	}

	private OpinionDocument create(OpinionCorpus corpus, Node node)
		throws XPathException {

		try {
			Validate.notNull(node, CannedMessages.NULL_ARGUMENT, "node");
		} catch (NullPointerException e) {
			throw new IllegalFactoryOptionsException(e);
		}
		
		XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();
	    Double polarity = (Double)xpath.compile("./@polarity").evaluate(node, XPathConstants.NUMBER);
	    
	    return this.create(corpus, node.getTextContent().trim(), polarity);
	}

	@Override
	protected OpinionDocument createPrivate(OpinionDocumentFactoryOptions options)
		throws IllegalFactoryOptionsException {
		
		Validate.notNull(options, CannedMessages.NULL_ARGUMENT, "options");
		
		if (options.getContent() != null) {
			return this.create(options.getCorpus(), options.getContent(), options.getPolarity());
		}
		
		if (options.getXmlNode() != null) {
			try {
				return this.create(options.getCorpus(), options.getXmlNode());
			} catch (XPathException e) {
				throw new IllegalFactoryOptionsException("options.xmlNode is not a valid input", e);
			}
		}
		
		throw new IllegalFactoryOptionsException("options did not have enough information to create this object");
	}
}