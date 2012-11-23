package edu.sabanciuniv.sentilab.sare.controllers.document;

import javax.xml.xpath.*;

import org.apache.commons.lang3.Validate;
import org.w3c.dom.Node;

import edu.sabanciuniv.sentilab.core.controllers.factory.IFactory;
import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.document.base.DocumentController;
import edu.sabanciuniv.sentilab.sare.models.document.*;
import edu.sabanciuniv.sentilab.sare.models.documentStore.OpinionCorpus;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A factory for creating {@link OpinionDocument} objects.
 * @author Mus'ab Husaini
 */
public class OpinionDocumentFactory
	extends DocumentController
	implements IFactory<OpinionDocument, OpinionDocumentFactoryOptions> {

	private OpinionDocument create(OpinionCorpus corpus, String content, double polarity) {
		return (OpinionDocument)new OpinionDocument()
			.setContent(content)
			.setPolarity(polarity)
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
	public OpinionDocument create(OpinionDocumentFactoryOptions options)
		throws IllegalFactoryOptionsException {
		
		try {
			Validate.notNull(options, CannedMessages.NULL_ARGUMENT, "options");
			//Validate.notNull(options.getCorpus(), CannedMessages.NULL_ARGUMENT, "options.corpus");
		} catch (NullPointerException e) {
			throw new IllegalFactoryOptionsException(e);
		}
			
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