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
		em = emFactory.createEntityManager();
		
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
		em.persist(testCorpus);
		em.persist(testDocument);
		em.getTransaction().commit();
		
		testSetCover = new DocumentSetCover(testCorpus);
		testSetCoverDocument = (SetCoverDocument)new SetCoverDocument(testDocument)
			.setStore(testSetCover)
			.setTokenizingOptions(testTokenizingOptions);
	}

	@After
	public void tearDown() throws Exception {
		if (em.getTransaction().isActive()) {
			em.getTransaction().rollback();
		}
		
		em.getTransaction().begin();
		
		if (em.contains(testCorpus)) {
			em.remove(testCorpus);
		}
		
		if (em.contains(testSetCover)) {
			em.remove(testSetCover);
		}
		
		em.getTransaction().commit();
		
		em.close();
	}

	@Test
	public void testContent() {
		assertEquals(testDocument.getContent(), testSetCoverDocument.getContent());
	}

	@Test
	public void testWeight() {
		em.getTransaction().begin();
		em.persist(testSetCover);
		em.persist(testSetCoverDocument);
		em.getTransaction().commit();
		
		assertEquals(testSetCoverDocument.getTotalTokenWeight(), testSetCoverDocument.getWeight(), 0);
		
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
		em.persist(testSetCover);
		em.persist(testSetCoverDocument);
		em.getTransaction().commit();
		
		em.detach(testSetCoverDocument);
		testSetCover.removeDocument(testSetCoverDocument);
		
		SetCoverDocument actualSetCoverDocument = em.find(SetCoverDocument.class, testSetCoverDocument.getId());
		testSetCover.addDocument(actualSetCoverDocument);
		em.refresh(testSetCover);
		em.refresh(testCorpus);
		
		assertNotNull(actualSetCoverDocument);
		
		actualSetCoverDocument
			.setTokenizingOptions(testTokenizingOptions)
			.retokenize();
		
		actualSetCoverDocument.resetWeight();
		assertEquals(testDocument.getTotalTokenWeight(), actualSetCoverDocument.getWeight(), 0);
	}
}