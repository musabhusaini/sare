package edu.sabanciuniv.sentilab.sare.models.opinion.tests;

import static org.junit.Assert.*;

import org.junit.*;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.base.ModelTestsBase;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

public class OpinionCorpusTest
	extends ModelTestsBase {

	private OpinionCorpus testCorpus;
	private OpinionCorpus testBaseCorpus;
	private OpinionDocument testDocument;
	
	@Before
	public void setUp() throws Exception {
		testCorpus = new OpinionCorpus();
		testDocument = (OpinionDocument)new OpinionDocument()
			.setStore(testCorpus);
	}

	@After
	public void tearDown() throws Exception {
		//
	}

	@Test
	public void testDocuments() {
		testCorpus.setDocuments(Lists.newArrayList(testDocument));
		
		em.getTransaction().begin();
		persist(testCorpus);
		persist(testDocument);
		em.getTransaction().commit();
		
		em.clear();
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
		persist(testBaseCorpus);
		persist(testCorpus);
		em.getTransaction().commit();
		
		em.clear();
		OpinionCorpus actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		
		assertNotNull(actualCorpus);
		assertNotNull(actualCorpus.getBaseStore());
		assertEquals(testCorpus.getBaseStore().getIdentifier(), actualCorpus.getBaseStore().getIdentifier());
	}

	@Test
	public void testTitle() {
		testCorpus.setTitle("test corpus");
		
		em.getTransaction().begin();
		persist(testCorpus);
		em.getTransaction().commit();
		
		em.clear();
		OpinionCorpus actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		
		assertNotNull(actualCorpus);
		assertEquals(testCorpus.getTitle(), actualCorpus.getTitle());
	}

	@Test
	public void testLanguage() {
		testCorpus.setLanguage("en");
		
		em.getTransaction().begin();
		persist(testCorpus);
		em.getTransaction().commit();
		
		em.clear();
		OpinionCorpus actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		
		assertNotNull(actualCorpus);
		assertEquals(testCorpus.getLanguage(), actualCorpus.getLanguage());
	}

	@Test
	public void testDescription() {
		testCorpus.setDescription("this is a test corpus");
		
		em.getTransaction().begin();
		persist(testCorpus);
		em.getTransaction().commit();
		
		em.clear();
		OpinionCorpus actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		
		assertNotNull(actualCorpus);
		assertEquals(testCorpus.getDescription(), actualCorpus.getDescription());
	}
}