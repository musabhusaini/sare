package edu.sabanciuniv.sentilab.sare.controllers.document;

import javax.xml.xpath.*;

import org.apache.commons.lang3.Validate;
import org.w3c.dom.Node;

import edu.sabanciuniv.sentilab.sare.controllers.document.base.DocumentController;
import edu.sabanciuniv.sentilab.sare.models.document.OpinionDocument;
import edu.sabanciuniv.sentilab.sare.models.documentStore.OpinionCorpus;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A factory for creating {@link OpinionDocument} objects.
 * @author Mus'ab Husaini
 */
public class OpinionDocumentFactory
	extends DocumentController {

	private OpinionDocument create(OpinionCorpus corpus, String content, double polarity) {
		return (OpinionDocument)new OpinionDocument()
			.setContent(content)
			.setPolarity(polarity)
			.setStore(corpus);
	}

	/**
	 * Creates a {@link OpinionDocument} object within a given corpus from a given XML node.
	 * @param corpus the {@link OpinionCorpus} object to store the document under.
	 * @param node the {@link Node} object containing the XML representation of the document.
	 * @return the newly-created {@link OpinionDocument} object.
	 * @throws XPathException when there is an error parsing the XML element.
	 */
	public OpinionDocument create(OpinionCorpus corpus, Node node)
		throws XPathException {
		
		Validate.notNull(node, CannedMessages.NULL_ARGUMENT, "node");
		
		XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();
	    Double polarity = (Double)xpath.compile("./@polarity").evaluate(node, XPathConstants.NUMBER);
	    
	    return this.create(corpus, node.getTextContent().trim(), polarity);
	}
}