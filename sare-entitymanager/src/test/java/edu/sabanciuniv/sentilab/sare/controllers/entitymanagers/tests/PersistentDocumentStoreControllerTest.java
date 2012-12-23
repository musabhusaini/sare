package edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.tests;

import static org.junit.Assert.*;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;

import static edu.sabanciuniv.sentilab.sare.models.base.UniquelyIdentifiableObject.*;

import java.util.*;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.PersistentDocumentStoreController;
import edu.sabanciuniv.sentilab.sare.models.base.*;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

public class PersistentDocumentStoreControllerTest extends ModelTestsBase {

	private PersistentDocumentStoreController testController;
	private List<OpinionCorpus> testCorpora;
	private String testOwner;
	
	@Before
	public void setUp() throws Exception {
		testController = new PersistentDocumentStoreController();
		
		testOwner = normalizeUuidString(UUID.randomUUID());
		
		testCorpora = newArrayList();
		testCorpora.add((OpinionCorpus)new OpinionCorpus()
			.addDocument(new OpinionDocument())
			.addDocument(new OpinionDocument())
			.setOwnerId(testOwner));
		
		testCorpora.add((OpinionCorpus)new OpinionCorpus()
			.setOwnerId(testOwner));
		
		em.getTransaction().begin();
		for (OpinionCorpus testCorpus : testCorpora) {
			persist(testCorpus);
		}
		em.getTransaction().commit();
		em.clear();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAllUuids() {
		Iterable<String> expectedUuids = transform(testCorpora, toUuidStringFunction());
		List<String> actualUuids = testController.getAllUuids(em, testOwner);
		
		assertNotNull(actualUuids);
		assertEquals(testCorpora.size(), actualUuids.size());
		for (String uuid : actualUuids) {
			assertTrue(contains(expectedUuids, uuid));
		}
	}

	@Test
	public void testGetSize() {
		for (OpinionCorpus testCorpus : testCorpora) {
			long actualSize = testController.getSize(em, testCorpus.getIdentifier().toString());
			assertEquals(size(testCorpus.getDocuments()), actualSize);
		}
	}
}