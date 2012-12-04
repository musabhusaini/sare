package edu.sabanciuniv.sentilab.sare.models.opinion.tests;

import static org.junit.Assert.*;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.models.base.ModelTestsBase;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

public class OpinionDocumentTest
	extends ModelTestsBase {
	
	private String testContent;
	private double testPolarity;
	
	private OpinionCorpus testCorpus;
	private OpinionDocument testDocument;
	
	@Before
	public void setUp() throws Exception {
		em = emFactory.createEntityManager();
		
		testContent = "this is a test";
		testPolarity = 0.8796;
		testCorpus = (OpinionCorpus)new OpinionCorpus()
			.setTitle("test corpus")
			.setLanguage("en");
		
		em.getTransaction().begin();
		em.persist(testCorpus);
		em.getTransaction().commit();
		
		testDocument = (OpinionDocument)new OpinionDocument().setStore(testCorpus);
		testCorpus.addDocument(testDocument);
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
		
		em.getTransaction().commit();
		
		em.close();
	}

	@Test
	public void testContent() {
		testDocument.setContent(testContent);
		
		em.getTransaction().begin();
		em.persist(testDocument);
		em.getTransaction().commit();
		
		OpinionDocument actualDocument = em.find(OpinionDocument.class, testDocument.getId());
		
		assertNotNull(actualDocument);
		assertEquals(testDocument.getContent(), actualDocument.getContent());
	}

	@Test
	public void testPolarity() {
		testDocument.setPolarity(testPolarity);
		
		em.getTransaction().begin();
		em.persist(testDocument);
		em.getTransaction().commit();
		
		OpinionDocument actualDocument = em.find(OpinionDocument.class, testDocument.getId());
		
		assertNotNull(actualDocument);
		assertEquals(testDocument.getPolarity(), actualDocument.getPolarity(), 0);
	}

	@Test
	public void testStore() {
		em.getTransaction().begin();
		em.persist(testDocument);
		em.getTransaction().commit();
		
		OpinionDocument actualDocument = em.find(OpinionDocument.class, testDocument.getId());
		
		assertNotNull(actualDocument);
		assertEquals(testDocument.getStore(), actualDocument.getStore());
	}

	@Test
	public void testBaseDocument() {
		OpinionDocument testBaseDocument = (OpinionDocument)new OpinionDocument()
			.setStore(testCorpus);
		testCorpus.addDocument(testBaseDocument);
		testDocument.setBaseDocument(testBaseDocument);
		
		em.getTransaction().begin();
		em.persist(testBaseDocument);
		em.persist(testDocument);
		em.getTransaction().commit();
		
		OpinionDocument actualDocument = em.find(OpinionDocument.class, testDocument.getId());
		
		assertNotNull(actualDocument);
		assertNotNull(actualDocument.getBaseDocument());
		
		assertNotNull(actualDocument.getBaseDocument());
		assertEquals(testBaseDocument.getIdentifier(), actualDocument.getBaseDocument().getIdentifier());		
	}
}