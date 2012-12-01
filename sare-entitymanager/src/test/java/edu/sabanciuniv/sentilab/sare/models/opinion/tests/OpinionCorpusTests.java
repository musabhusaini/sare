package edu.sabanciuniv.sentilab.sare.models.opinion.tests;

import static org.junit.Assert.*;

import org.junit.*;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.base.ModelTestsBase;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

public class OpinionCorpusTests
	extends ModelTestsBase {

	private OpinionCorpus testCorpus;
	private OpinionCorpus testBaseCorpus;
	private OpinionCorpus testDerivedCorpus;
	private OpinionDocument testDocument;
	
	@Before
	public void setUp() throws Exception {
		em = emFactory.createEntityManager();
		
		testCorpus = new OpinionCorpus();
		testDocument = (OpinionDocument)new OpinionDocument()
			.setStore(testCorpus);
	}

	@After
	public void tearDown() throws Exception {
		if (em.getTransaction().isActive()) {
			em.getTransaction().rollback();
		}
		
		em.getTransaction().begin();
		em.remove(testCorpus);
		
		if (testBaseCorpus != null && em.contains(testBaseCorpus)) {
			em.remove(testBaseCorpus);
		}
		
		if (testDerivedCorpus != null && em.contains(testDerivedCorpus)) {
			em.remove(testDerivedCorpus);
		}
		
		em.getTransaction().commit();
		
		em.close();
	}

	@Test
	public void testDocuments() {
		testCorpus.setDocuments(Lists.newArrayList(testDocument));
		
		em.getTransaction().begin();
		em.persist(testCorpus);
		em.persist(testDocument);
		em.getTransaction().commit();
		
		OpinionCorpus actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		
		assertNotNull(actualCorpus);
		assertNotNull(actualCorpus.getDocuments());
		assertEquals(Iterables.size(testCorpus.getDocuments()), Iterables.size(actualCorpus.getDocuments()));
		
		OpinionDocument actualDocument = Iterables.getFirst(actualCorpus.getDocuments(), null);
		assertNotNull(actualDocument);
		assertEquals(testDocument.getIdentifier(), actualDocument.getIdentifier());
	}

	@Test
	public void testAddDocument() {
		testCorpus.addDocument(testDocument);
		assertTrue(Iterables.contains(testCorpus.getDocuments(), testDocument));
	}

	@Test
	public void testRemoveDocument() {
		testCorpus.addDocument(testDocument);
		testCorpus.removeDocument(testDocument);
		assertFalse(Iterables.contains(testCorpus.getDocuments(), testDocument));
	}

	@Test
	public void testBaseStore() {
		testBaseCorpus = new OpinionCorpus();
		testCorpus.setBaseStore(testBaseCorpus);
		
		assertTrue(testBaseCorpus.hasDerivedStore(testCorpus));
		
		em.getTransaction().begin();
		em.persist(testBaseCorpus);
		em.persist(testCorpus);
		em.getTransaction().commit();
		
		OpinionCorpus actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		
		assertNotNull(actualCorpus);
		assertNotNull(actualCorpus.getBaseStore());
		assertEquals(testCorpus.getBaseStore().getIdentifier(), actualCorpus.getBaseStore().getIdentifier());
	}

	@Test
	public void testTitle() {
		testCorpus.setTitle("test corpus");
		
		em.getTransaction().begin();
		em.persist(testCorpus);
		em.getTransaction().commit();
		
		OpinionCorpus actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		
		assertNotNull(actualCorpus);
		assertEquals(testCorpus.getTitle(), actualCorpus.getTitle());
	}

	@Test
	public void testLanguage() {
		testCorpus.setLanguage("en");
		
		em.getTransaction().begin();
		em.persist(testCorpus);
		em.getTransaction().commit();
		
		OpinionCorpus actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		
		assertNotNull(actualCorpus);
		assertEquals(testCorpus.getLanguage(), actualCorpus.getLanguage());
	}

	@Test
	public void testDescription() {
		testCorpus.setDescription("this is a test corpus");
		
		em.getTransaction().begin();
		em.persist(testCorpus);
		em.getTransaction().commit();
		
		OpinionCorpus actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		
		assertNotNull(actualCorpus);
		assertEquals(testCorpus.getDescription(), actualCorpus.getDescription());
	}
}