package edu.sabanciuniv.sentilab.sare.controllers.documentStore;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.sabanciuniv.sentilab.sare.controllers.document.OpinionDocumentFactory;
import edu.sabanciuniv.sentilab.sare.controllers.documentStore.base.DocumentStoreController;
import edu.sabanciuniv.sentilab.sare.models.documentStore.OpinionCorpus;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A factory for creating {@link OpinionCorpus} objects.
 * @author Mus'ab Husaini
 */
public class OpinionCorpusFactory
	extends DocumentStoreController {

	private OpinionCorpus addXmlPacket(OpinionCorpus corpus, InputStream input)
		throws ParserConfigurationException, SAXException, IOException, XPathException {
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	    domFactory.setNamespaceAware(true);
	    DocumentBuilder builder = domFactory.newDocumentBuilder();
	    Document doc = builder.parse(input);

	    XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();
	    
	    Node corpusNode = (Node)xpath.compile("/corpus").evaluate(doc, XPathConstants.NODE);
	    if (corpusNode == null) {
	    	corpusNode = doc.getDocumentElement();
	    	Validate.notNull(corpusNode, CannedMessages.NULL_ARGUMENT, "/corpus");
	    }
	    
	    String title = (String)xpath.compile("./@title").evaluate(corpusNode, XPathConstants.STRING);
	    String description = (String)xpath.compile("./@description").evaluate(corpusNode, XPathConstants.STRING);
	    String language = (String)xpath.compile("./@language").evaluate(corpusNode, XPathConstants.STRING);
	    
	    if (StringUtils.isEmpty(corpus.getTitle())) {
	    	corpus.setTitle(title);
	    }
	    
	    if (StringUtils.isEmpty(corpus.getDescription())) {
	    	corpus.setDescription(description);
	    }
	    
	    if (StringUtils.isEmpty(corpus.getLanguage())) {
	    	corpus.setLanguage(language);
	    }
	    
	    OpinionDocumentFactory opinionFactory = new OpinionDocumentFactory();
	    
	    NodeList documentNodes = (NodeList)xpath.compile("./document").evaluate(corpusNode, XPathConstants.NODESET);
	    if (documentNodes == null || documentNodes.getLength() == 0) {
	    	documentNodes = corpusNode.getChildNodes();
	    	Validate.isTrue(documentNodes != null && documentNodes.getLength() > 0, CannedMessages.NULL_ARGUMENT, "/corpus/document");
	    }
	    
	    for (int index=0; index<documentNodes.getLength(); index++) {
	    	corpus.addDocument(opinionFactory.create(corpus, documentNodes.item(index)));
	    }
		
		return corpus;
	}
	
	/**
	 * Creates a {@link OpinionCorpus} object from a given stream in a given format.
	 * @param input an {@link InputStream} containing the corpus description.
	 * @param format the format that the stream is in.
	 * @return the newly-created {@link OpinionCorpus} object.
	 * @throws IOException when there is an error reading the stream.
	 */
	public OpinionCorpus create(InputStream input, String format)
		throws IOException {
		
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		OpinionCorpus corpus = new OpinionCorpus();
		
		switch(format) {
		case "xml":
			try {
				corpus = this.addXmlPacket(corpus, input);
			} catch (ParserConfigurationException | SAXException | XPathException e) {
				throw new IllegalArgumentException("Error reading input.", e);
			}
			break;
		}
		
		return corpus;
	}
	
	/**
	 * Creates a {@link OpinionCorpus} object from a given byte array in a given format.
	 * @param input the byte array containing the corpus description.
	 * @param format the format of the byte array.
	 * @return the newly-created {@link OpinionCorpus} object.
	 */
	public OpinionCorpus create(byte[] input, String format) {
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		try {
			return this.create(new ByteArrayInputStream(input), format);
		} catch (IOException e) {
			// unlikely that this will ever happen since we're using a byte stream.
			throw new IllegalArgumentException("Error reading input.", e);
		}
	}
	
	/**
	 * Creates a {@link OpinionCorpus} object from a given {@link File} in a given format.
	 * @param input the {@link File} containing the corpus.
	 * @param format the format to read the file in.
	 * @return the newly-created {@link OpinionCorpus} object.
	 * @throws IOException when there is an error reading the stream.
	 */
	public OpinionCorpus create(File input, String format)
		throws IOException {
		
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		InputStream stream = FileUtils.openInputStream(input);
		OpinionCorpus corpus = this.create(stream, format);
		stream.close();
		return corpus;
	}
	
	/**
	 * Creates a {@link OpinionCorpus} object from a given {@link File} in the default format of the file.
	 * @param input the {@link File} containing the corpus.
	 * @return the newly-created {@link OpinionCorpus} object.
	 * @throws IOException when there is an error reading the stream.
	 */
	public OpinionCorpus create(File input)
		throws IOException {
		
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		return this.create(input, FilenameUtils.getExtension(input.getPath()));
	}
}