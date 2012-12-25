package edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.tests;

import static org.junit.Assert.*;

import static com.google.common.collect.Iterables.*;

import static edu.sabanciuniv.sentilab.utils.UuidUtils.*;

import java.util.*;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.*;
import edu.sabanciuniv.sentilab.sare.models.base.ModelTestsBase;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

public class PersistentDocumentControllerTest
	extends ModelTestsBase {

	private PersistentDocumentController testController;
	private OpinionCorpus testCorpus;

	@Before
	public void setUp() throws Exception {
		testController = new PersistentDocumentController();
		
		testCorpus = (OpinionCorpus)new OpinionCorpus()
			.addDocument(new OpinionDocument())
			.addDocument(new OpinionDocument());
		
		em.getTransaction().begin();
		persist(testCorpus);
		em.getTransaction().commit();
		em.clear();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAllUuids() {
		Iterable<String> expectedUuids = transform(testCorpus.getDocuments(), toUuidStringFunction());
		List<String> actualUuids = testController.getAllUuids(em, testCorpus.getIdentifier().toString());
		
		assertNotNull(actualUuids);
		assertEquals(size(testCorpus.getDocuments()), actualUuids.size());
		
		for (String uuid : actualUuids) {
			assertTrue(contains(expectedUuids, uuid));
		}
	}
}