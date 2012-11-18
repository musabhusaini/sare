package edu.sabanciuniv.sentilab.sare.controllers.document.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import edu.sabanciuniv.sentilab.sare.controllers.document.OpinionDocumentFactory;
import edu.sabanciuniv.sentilab.sare.models.document.OpinionDocument;
import edu.sabanciuniv.sentilab.sare.models.documentStore.OpinionCorpus;

public class OpinionDocumentFactoryTests {

	private OpinionDocumentFactory testFactory;
	private String testXmlCorpusFilename;
	private OpinionCorpus testCorpus;
	private OpinionDocument expectedXmlDocument;
	
	@Before
	public void setUp() throws Exception {
		testFactory = new OpinionDocumentFactory();
		testXmlCorpusFilename = "/test-corpus.xml";
		testCorpus = new OpinionCorpus();
		expectedXmlDocument = new OpinionDocument()
			.setContent("nice hotel")
			.setPolarity(0.7);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateFromXml() {
		try {
			DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
		    domFactory.setNamespaceAware(true);
		    DocumentBuilder builder = domFactory.newDocumentBuilder();
		    Document doc = builder.parse(getClass().getResourceAsStream(testXmlCorpusFilename));
		    
		    XPathFactory factory = XPathFactory.newInstance();
		    XPath xpath = factory.newXPath();
		    
		    NodeList documentNodes = (NodeList)xpath.compile("/corpus/document").evaluate(doc, XPathConstants.NODESET);
		    OpinionDocument opinionDoc = testFactory.create(testCorpus, documentNodes.item(0));
			
			assertNotNull(opinionDoc);
			assertEquals(expectedXmlDocument.getContent(), opinionDoc.getContent());
			assertEquals(expectedXmlDocument.getPolarity(), opinionDoc.getPolarity(), 0.0);
			assertSame(testCorpus, opinionDoc.getStore());
		}
		catch(IllegalArgumentException | ParserConfigurationException | SAXException | IOException | XPathException e) {
			fail("error reading file");
		}
	}
}