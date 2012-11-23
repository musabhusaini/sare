package edu.sabanciuniv.sentilab.sare.controllers.documentStore;

import java.io.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.apache.commons.io.*;
import org.apache.commons.lang3.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import edu.sabanciuniv.sentilab.sare.controllers.document.OpinionDocumentFactory;
import edu.sabanciuniv.sentilab.sare.controllers.documentStore.base.DocumentStoreController;
import edu.sabanciuniv.sentilab.sare.controllers.factory.base.IFactory;
import edu.sabanciuniv.sentilab.sare.models.document.OpinionDocumentFactoryOptions;
import edu.sabanciuniv.sentilab.sare.models.documentStore.*;
import edu.sabanciuniv.sentilab.sare.models.factory.base.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A factory for creating {@link OpinionCorpus} objects.
 * @author Mus'ab Husaini
 */
public class OpinionCorpusFactory
	extends DocumentStoreController
	implements IFactory<OpinionCorpus, OpinionCorpusFactoryOptions> {

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
	    
	    if (StringUtils.isNotEmpty(title)) {
	    	corpus.setTitle(title);
	    }
	    
	    if (StringUtils.isNotEmpty(description)) {
	    	corpus.setDescription(description);
	    }
	    
	    if (StringUtils.isNotEmpty(language)) {
	    	corpus.setLanguage(language);
	    }
	    
	    OpinionDocumentFactory opinionFactory = new OpinionDocumentFactory();
	    
	    NodeList documentNodes = (NodeList)xpath.compile("./document").evaluate(corpusNode, XPathConstants.NODESET);
	    if (documentNodes == null || documentNodes.getLength() == 0) {
	    	documentNodes = corpusNode.getChildNodes();
	    	Validate.isTrue(documentNodes != null && documentNodes.getLength() > 0, CannedMessages.NULL_ARGUMENT, "/corpus/document");
	    }
	    
	    for (int index=0; index<documentNodes.getLength(); index++) {
	    	corpus.addDocument(opinionFactory.create(new OpinionDocumentFactoryOptions()
	    		.setCorpus(corpus).setXmlNode(documentNodes.item(index))));
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
	private OpinionCorpus create(InputStream input, String format)
		throws IOException {
		
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		OpinionCorpus corpus = new OpinionCorpus();
		
		switch(format) {
		case "xml":
			try {
				corpus = this.addXmlPacket(corpus, input);
			} catch (ParserConfigurationException | SAXException | XPathException e) {
				throw new IOException("error reading input", e);
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
	private OpinionCorpus create(byte[] input, String format)
		throws IOException {
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		try {
			return this.create(new ByteArrayInputStream(input), format);
		} catch (IOException e) {
			// unlikely that this will ever happen since we're using a byte stream.
			throw new IOException("error reading input", e);
		}
	}
	
	/**
	 * Creates a {@link OpinionCorpus} object from a given {@link File} in a given format.
	 * @param input the {@link File} containing the corpus.
	 * @param format the format to read the file in.
	 * @return the newly-created {@link OpinionCorpus} object.
	 * @throws IOException when there is an error reading the stream.
	 */
	private OpinionCorpus create(File input, String format)
		throws IOException {
		
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		InputStream stream = FileUtils.openInputStream(input);
		OpinionCorpus corpus = this.create(stream, format);
		stream.close();
		return corpus;
	}
	
	@Override
	public OpinionCorpus create(OpinionCorpusFactoryOptions options)
		throws IllegalFactoryOptionsException {
		
		OpinionCorpus corpus = null;
		
		try {
			Validate.notNull(options, CannedMessages.NULL_ARGUMENT, "options");
			
			String format = StringUtils.isNotEmpty(options.getFormat()) ? options.getFormat() :
				(options.getFile() != null ? FilenameUtils.getExtension(options.getFile().getPath()) : null);
			
			try {
				if (options.getBytes() != null) {
					Validate.notNull(format, CannedMessages.EMPTY_ARGUMENT, "options.format");
					
					corpus = this.create(options.getBytes(), format);
				} else if (options.getInputStream() != null) {
					Validate.notNull(format, CannedMessages.EMPTY_ARGUMENT, "options.format");
					
					corpus = this.create(options.getInputStream(), format);
				} else if (options.getFile() != null) {
					corpus = this.create(options.getFile(), format);
				}
			} catch (IOException e) {
				//
			}
		} catch (NullPointerException e) {
			throw new IllegalFactoryOptionsException(e);
		}
		
		if (corpus == null) {
			throw new IllegalFactoryOptionsException("options did not have enough or correct information to create this object");
		}
		
		if (StringUtils.isNotEmpty(options.getTitle())) {
			corpus.setTitle(options.getTitle());
		}
		
		if (StringUtils.isNotEmpty(options.getDescription())) {
			corpus.setDescription(options.getDescription());
		}
		
		if (StringUtils.isNotEmpty(options.getLanguage())) {
			corpus.setLanguage(options.getLanguage());
		}
		
		return corpus;
	}
}