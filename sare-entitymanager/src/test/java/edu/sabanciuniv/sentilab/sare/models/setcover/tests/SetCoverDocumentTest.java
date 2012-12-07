package edu.sabanciuniv.sentilab.sare.models.setcover.tests;

import static org.junit.Assert.*;

import java.util.EnumSet;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.models.base.ModelTestsBase;
import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.base.document.TokenizingOptions.TagCaptureOptions;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.sare.models.setcover.*;

public class SetCoverDocumentTest
	extends ModelTestsBase {

	private OpinionCorpus testCorpus;
	private OpinionDocument testDocument;
	
	private DocumentSetCover testSetCover;
	private SetCoverDocument testSetCoverDocument;
	
	private TokenizingOptions testTokenizingOptions;
	
	@Before
	public void setUp() throws Exception {
		testTokenizingOptions = new TokenizingOptions()
			.setLemmatized(true)
			.setTags(EnumSet.of(TagCaptureOptions.STARTS_WITH, TagCaptureOptions.IGNORE_CASE), "nn");
			
		testCorpus = (OpinionCorpus)new OpinionCorpus()
			.setLanguage("en")
			.setTitle("test corpus");
		testDocument = (OpinionDocument)new OpinionDocument()
			.setContent("this is a test document")
			.setStore(testCorpus)
			.setTokenizingOptions(testTokenizingOptions);
		testCorpus.addDocument(testDocument);
		
		em.getTransaction().begin();
		persist(testCorpus);
		persist(testDocument);
		em.getTransaction().commit();
		
		testSetCover = new DocumentSetCover(testCorpus);
		testSetCoverDocument = (SetCoverDocument)new SetCoverDocument(testDocument)
			.setStore(testSetCover)
			.setTokenizingOptions(testTokenizingOptions);
	}

	@After
	public void tearDown() throws Exception {
		//
	}

	@Test
	public void testContent() {
		assertEquals(testDocument.getContent(), testSetCoverDocument.getContent());
	}

	@Test
	public void testWeight() {
		em.getTransaction().begin();
		persist(testSetCover);
		persist(testSetCoverDocument);
		em.getTransaction().commit();
		
		assertEquals(testSetCoverDocument.getTotalTokenWeight(), testSetCoverDocument.getWeight(), 0);
		
		em.clear();
		SetCoverDocument actualSetCoverDocument = em.find(SetCoverDocument.class, testSetCoverDocument.getId());
		
		assertNotNull(actualSetCoverDocument);
		assertEquals(testSetCoverDocument.getTotalTokenWeight(), actualSetCoverDocument.getWeight(), 0);
	}

	@Test
	public void testResetWeight() {
		OpinionDocument anotherDocument = (OpinionDocument)new OpinionDocument()
			.setContent("this is another test document")
			.setStore(testCorpus)
			.setTokenizingOptions(testTokenizingOptions);
		
		testSetCoverDocument.merge(anotherDocument);
		
		assertFalse(testSetCoverDocument.getWeight() == testDocument.getTotalTokenWeight());
		
		em.getTransaction().begin();
		persist(testSetCover);
		persist(testSetCoverDocument);
		em.getTransaction().commit();
		
		em.clear();
		SetCoverDocument actualSetCoverDocument = em.find(SetCoverDocument.class, testSetCoverDocument.getId());
		
		assertNotNull(actualSetCoverDocument);
		
		actualSetCoverDocument
			.setTokenizingOptions(testTokenizingOptions)
			.retokenize();
		
		actualSetCoverDocument.resetWeight();
		assertEquals(testDocument.getTotalTokenWeight(), actualSetCoverDocument.getWeight(), 0);
	}
}