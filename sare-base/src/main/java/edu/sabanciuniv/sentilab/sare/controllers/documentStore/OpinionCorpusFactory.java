package edu.sabanciuniv.sentilab.sare.controllers.documentStore;

import java.io.*;
import java.util.*;
import java.util.zip.*;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.apache.commons.io.*;
import org.apache.commons.lang3.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import com.google.common.base.*;
import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.core.controllers.factory.IFactory;
import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.document.OpinionDocumentFactory;
import edu.sabanciuniv.sentilab.sare.controllers.documentStore.base.DocumentStoreController;
import edu.sabanciuniv.sentilab.sare.models.document.OpinionDocumentFactoryOptions;
import edu.sabanciuniv.sentilab.sare.models.documentStore.*;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A factory for creating {@link OpinionCorpus} objects.
 * @author Mus'ab Husaini
 */
public class OpinionCorpusFactory
	extends DocumentStoreController
	implements IFactory<OpinionCorpus, OpinionCorpusFactoryOptions> {

	private OpinionCorpusFactory addXmlPacket(OpinionCorpus corpus, InputStream input, OpinionCorpusFactoryOptions options)
		throws ParserConfigurationException, SAXException, IOException, XPathException {
		
		Validate.notNull(corpus, CannedMessages.NULL_ARGUMENT, "corpus");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
	    domFactory.setNamespaceAware(true);
	    Document doc = domFactory.newDocumentBuilder().parse(input);

	    XPathFactory factory = XPathFactory.newInstance();
	    XPath xpath = factory.newXPath();
	    
	    OpinionDocumentFactory opinionFactory = new OpinionDocumentFactory();
	    
	    if ("document".equals(doc.getDocumentElement().getLocalName())) {
	    	corpus.addDocument(opinionFactory.create(new OpinionDocumentFactoryOptions()
    			.setCorpus(corpus).setXmlNode(doc.getDocumentElement())));
	    	return this;
	    }
	    
	    Node corpusNode = (Node)xpath.compile("/corpus").evaluate(doc, XPathConstants.NODE);
	    if (corpusNode == null) {
	    	corpusNode = Validate.notNull(doc.getDocumentElement(), CannedMessages.NULL_ARGUMENT, "/corpus");
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
	    
	    NodeList documentNodes = (NodeList)xpath.compile("./document").evaluate(corpusNode, XPathConstants.NODESET);
	    if (documentNodes == null || documentNodes.getLength() == 0) {
	    	documentNodes = corpusNode.getChildNodes();
	    	Validate.isTrue(documentNodes != null && documentNodes.getLength() > 0, CannedMessages.NULL_ARGUMENT, "/corpus/document");
	    }
	    
	    for (int index=0; index<documentNodes.getLength(); index++) {
	    	corpus.addDocument(opinionFactory.create(new OpinionDocumentFactoryOptions()
	    		.setCorpus(corpus).setXmlNode(documentNodes.item(index))));
	    }
		
		return this;
	}
	
	private OpinionCorpusFactory addZipPacket(OpinionCorpus corpus, InputStream input, OpinionCorpusFactoryOptions options)
		throws IOException {
		
		Validate.notNull(corpus, CannedMessages.NULL_ARGUMENT, "corpus");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		ZipInputStream zipStream = new ZipInputStream(input);
		ZipEntry zipEntry;
		while ((zipEntry = zipStream.getNextEntry()) != null) {
			if (!zipEntry.isDirectory()) {
				// we create a byte stream so that the input stream is not closed by the underlying methods.
				this.createSpecific(corpus,
					new ByteArrayInputStream(IOUtils.toByteArray(zipStream)), FilenameUtils.getExtension(zipEntry.getName()), options);
			}
		}
		
		return this;
	}
	
	private OpinionCorpusFactory addTextPacket(OpinionCorpus corpus, InputStream input, String delimiter, OpinionCorpusFactoryOptions options)
		throws IOException {
		
		Validate.notNull(corpus, CannedMessages.NULL_ARGUMENT, "corpus");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
				
		OpinionDocumentFactory opinionFactory = new OpinionDocumentFactory();
		BufferedReader reader = new BufferedReader(new InputStreamReader(input));
		String line;
		
		while ((line = reader.readLine()) != null) {
			List<String> columns = StringUtils.isNotEmpty(delimiter) ?
				Lists.newArrayList(Splitter.on(delimiter).split(line)) : Lists.newArrayList(line);
			if (columns.size() < 1) {
				continue;
			}
			
			OpinionDocumentFactoryOptions opinionOptions = new OpinionDocumentFactoryOptions()
				.setCorpus(corpus)
				.setContent(columns.get(0));
			
			if (columns.size() > 1) {
				try {
					opinionOptions.setPolarity(Double.parseDouble(columns.get(1)));
				} catch (NumberFormatException e) {
					opinionOptions.setPolarity(null);
				}
			}
			
			corpus.addDocument(opinionFactory.create(opinionOptions));
		}
		
		return this;
	}
	
	private OpinionCorpus createSpecific(OpinionCorpus corpus, InputStream input, String format, OpinionCorpusFactoryOptions options)
		throws IOException {
		
		Validate.notNull(corpus, CannedMessages.NULL_ARGUMENT, "corpus");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		switch(format) {
		case "xml":
			try {
				this.addXmlPacket(corpus, input, options);
			} catch (ParserConfigurationException | SAXException | XPathException e) {
				throw new IOException("error reading input", e);
			}
			break;
		case "zip":
			this.addZipPacket(corpus, input, options);
			break;
		case "text":
		case "csv":
		default:
			this.addTextPacket(corpus, input, options.getTextDelimiter(), options);
		}
		
		return corpus;
	}
	
	private OpinionCorpus createSpecific(OpinionCorpus corpus, byte[] input, String format, OpinionCorpusFactoryOptions options)
		throws IOException {
		
		Validate.notNull(corpus, CannedMessages.NULL_ARGUMENT, "corpus");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		try {
			return this.createSpecific(corpus, new ByteArrayInputStream(input), format, options);
		} catch (IOException e) {
			// unlikely that this will ever happen since we're using a byte stream.
			throw new IOException("error reading input", e);
		}
	}
	
	private OpinionCorpus createSpecific(OpinionCorpus corpus, File input, String format, OpinionCorpusFactoryOptions options)
		throws IOException {
		
		Validate.notNull(corpus, CannedMessages.NULL_ARGUMENT, "corpus");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		InputStream stream = FileUtils.openInputStream(input);
		corpus = this.createSpecific(corpus, stream, format, options);
		stream.close();
		return corpus;
	}
	
	@Override
	public OpinionCorpus create(OpinionCorpusFactoryOptions options)
		throws IllegalFactoryOptionsException {
		
		OpinionCorpus corpus = new OpinionCorpus();
		
		try {
			Validate.notNull(options, CannedMessages.NULL_ARGUMENT, "options");
			
			String format = StringUtils.isNotEmpty(options.getFormat()) ? options.getFormat() :
				(options.getFile() != null ? FilenameUtils.getExtension(options.getFile().getPath()) : null);
			
			try {
				if (options.getBytes() != null) {
					Validate.notNull(format, CannedMessages.EMPTY_ARGUMENT, "options.format");
					
					corpus = this.createSpecific(corpus, options.getBytes(), format, options);
				} else if (options.getInputStream() != null) {
					Validate.notNull(format, CannedMessages.EMPTY_ARGUMENT, "options.format");
					
					this.createSpecific(corpus, options.getInputStream(), format, options);
				} else if (options.getFile() != null) {
					this.createSpecific(corpus, options.getFile(), format, options);
				}
			} catch (IOException e) {
				throw new IllegalFactoryOptionsException("there was an error reading the input", e);
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