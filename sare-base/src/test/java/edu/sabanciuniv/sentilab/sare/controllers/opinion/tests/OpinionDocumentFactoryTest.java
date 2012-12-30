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

public class OpinionDocumentFactoryTest {

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