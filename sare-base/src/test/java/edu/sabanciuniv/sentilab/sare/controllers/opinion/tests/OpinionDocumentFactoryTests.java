package edu.sabanciuniv.sentilab.sare.controllers.opinion.tests;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.xml.parsers.*;
import javax.xml.xpath.*;

import org.junit.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionDocumentFactory;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

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
		expectedXmlDocument = (OpinionDocument)new OpinionDocument()
			.setPolarity(0.7)
			.setContent("nice hotel");
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
		    OpinionDocument opinionDoc = testFactory.create(new OpinionDocumentFactoryOptions()
		    	.setCorpus(testCorpus).setXmlNode(documentNodes.item(0)));
			
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