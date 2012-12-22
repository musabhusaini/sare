package edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.tests;

import static org.junit.Assert.*;

import java.util.List;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.PersistenceDocumentStoreController;
import edu.sabanciuniv.sentilab.sare.models.base.ModelTestsBase;
import edu.sabanciuniv.sentilab.sare.models.base.UniquelyIdentifiableObject;
import edu.sabanciuniv.sentilab.sare.models.opinion.OpinionCorpus;
import edu.sabanciuniv.sentilab.sare.models.opinion.OpinionDocument;

public class PersistentDocumentStoreControllerTest extends ModelTestsBase {

	private PersistenceDocumentStoreController testController;
	private OpinionCorpus testCorpus1;
	private OpinionCorpus testCorpus2;
	private String testOwner;
	
	@Before
	public void setUp() throws Exception {
		testController = new PersistenceDocumentStoreController();
		
		testOwner = UniquelyIdentifiableObject.normalizeUuidString(UUID.randomUUID());
		
		testCorpus1 = (OpinionCorpus)new OpinionCorpus()
			.addDocument(new OpinionDocument())
			.addDocument(new OpinionDocument())
			.setOwnerId(testOwner);
		
		testCorpus2 = (OpinionCorpus)new OpinionCorpus()
			.setOwnerId(testOwner);
		
		em.getTransaction().begin();
		persist(testCorpus1);
		persist(testCorpus2);
		em.getTransaction().commit();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAllUuids() {
		List<String> actualUuids = testController.getAllUuids(em, testOwner);
		
		assertNotNull(actualUuids);
		assertEquals(2, actualUuids.size());
		assertEquals(UniquelyIdentifiableObject.normalizeUuidString(testCorpus1.getIdentifier()), actualUuids.get(0));
	}

	@Test
	public void testGetSize() {
		long actualSize = testController.getSize(em, testCorpus1.getIdentifier().toString());
		assertEquals(Iterables.size(testCorpus1.getDocuments()), actualSize);
	}

}
