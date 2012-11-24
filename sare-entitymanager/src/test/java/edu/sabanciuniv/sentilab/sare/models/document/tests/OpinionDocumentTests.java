package edu.sabanciuniv.sentilab.sare.models.document.tests;

import static org.junit.Assert.*;

import org.junit.*;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.base.ModelTestsBase;
import edu.sabanciuniv.sentilab.sare.models.document.OpinionDocument;
import edu.sabanciuniv.sentilab.sare.models.document.base.PersistentDocument;
import edu.sabanciuniv.sentilab.sare.models.documentStore.OpinionCorpus;

public class OpinionDocumentTests
	extends ModelTestsBase {
	
	private String testContent;
	private double testPolarity;
	
	private OpinionCorpus testCorpus;
	private OpinionDocument testDocument;
	
	@Before
	public void setUp() throws Exception {
		em = emFactory.createEntityManager();
		
		testContent = "this is a test";
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
		
		assertTrue(testBaseDocument.getDerivedDocuments().contains(testDocument));
		
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
	
	@Test
	public void testDerivedDocuments() {
		OpinionDocument testDerivedDocument = (OpinionDocument)new OpinionDocument()
			.setStore(testCorpus);
		testCorpus.addDocument(testDerivedDocument);
		testDocument.setDerivedDocuments(Lists.newArrayList((PersistentDocument)testDerivedDocument));
		
		assertEquals(testDocument, testDerivedDocument.getBaseDocument());
		
		em.getTransaction().begin();
		em.persist(testDocument);
		em.persist(testDerivedDocument);
		em.getTransaction().commit();
		
		OpinionDocument actualDocument = em.find(OpinionDocument.class, testDocument.getId());
		
		assertNotNull(actualDocument);
		assertNotNull(actualDocument.getDerivedDocuments());
		assertEquals(testDocument.getDerivedDocuments().size(), actualDocument.getDerivedDocuments().size());
		
		PersistentDocument actualDerivedDocument = Iterables.getFirst(actualDocument.getDerivedDocuments(), null);
		assertNotNull(actualDerivedDocument);
		assertEquals(testDerivedDocument.getIdentifier(), actualDerivedDocument.getIdentifier());
	}
}