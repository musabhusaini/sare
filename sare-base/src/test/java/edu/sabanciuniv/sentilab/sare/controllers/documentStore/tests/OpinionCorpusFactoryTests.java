package edu.sabanciuniv.sentilab.sare.controllers.documentStore.tests;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.controllers.documentStore.OpinionCorpusFactory;
import edu.sabanciuniv.sentilab.sare.models.document.OpinionDocument;
import edu.sabanciuniv.sentilab.sare.models.documentStore.*;
import edu.sabanciuniv.sentilab.sare.models.factory.base.IllegalFactoryOptionsException;

public class OpinionCorpusFactoryTests {
	
	private OpinionCorpusFactory testFactory;
	private String testXmlCorpusFilename;
	private OpinionCorpus expectedXmlCorpus;
	
	@Before
	public void setUp() throws Exception {
		testFactory = new OpinionCorpusFactory();
		testXmlCorpusFilename = "/test-corpus.xml";
		
		expectedXmlCorpus = (OpinionCorpus)new OpinionCorpus()
			.addDocument(new OpinionDocument())
			.addDocument(new OpinionDocument())
			.setTitle("test-xml-corpus")
			.setDescription("test")
			.setLanguage("en");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateFromXmlFile() {
		OpinionCorpus actualCorpus = null;
		try {
			actualCorpus = testFactory.create(new OpinionCorpusFactoryOptions()
				.setFile(new File(getClass().getResource(testXmlCorpusFilename).getPath())));
		} catch (IllegalFactoryOptionsException e) {
			fail("could not open file.");
		}
		
		assertNotNull(actualCorpus);
		assertEquals(expectedXmlCorpus.getTitle(), actualCorpus.getTitle());
		assertEquals(expectedXmlCorpus.getDescription(), actualCorpus.getDescription());
		assertEquals(expectedXmlCorpus.getLanguage(), actualCorpus.getLanguage());
		assertEquals(Iterables.size(expectedXmlCorpus.getDocuments()), Iterables.size(actualCorpus.getDocuments()));
	}
}