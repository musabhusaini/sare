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