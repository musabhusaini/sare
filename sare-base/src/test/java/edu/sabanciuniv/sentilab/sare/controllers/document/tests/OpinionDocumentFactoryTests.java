package edu.sabanciuniv.sentilab.sare.controllers.document.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.sabanciuniv.sentilab.sare.controllers.document.OpinionDocumentFactory;
import edu.sabanciuniv.sentilab.sare.models.document.OpinionDocument;
import edu.sabanciuniv.sentilab.sare.models.documentStore.OpinionCorpus;

public class OpinionDocumentFactoryTests {

	private OpinionDocumentFactory testController;
	private String testContent;
	private double testPolarity;
	private OpinionCorpus testCorpus;
	
	@Before
	public void setUp() throws Exception {
		testController = new OpinionDocumentFactory();
		testContent = "This is a test document.";
		testPolarity = 0.748;
		testCorpus = new OpinionCorpus();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateWithString() {
		String xml = String.format("", testContent);
		OpinionDocument doc = testController.create(testCorpus, xml, "xml");
		
		assertNotNull(doc);
		assertEquals(testContent, doc.getContent());
		assertEquals(testPolarity, doc.getPolarity(), 0.0);
		assertSame(testCorpus, doc.getStore());
	}
}