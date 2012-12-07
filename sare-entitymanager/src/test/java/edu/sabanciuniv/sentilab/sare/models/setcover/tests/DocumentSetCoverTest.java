package edu.sabanciuniv.sentilab.sare.models.setcover.tests;

import static org.junit.Assert.*;

import java.util.EnumSet;

import org.junit.*;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.models.base.ModelTestsBase;
import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.base.document.TokenizingOptions.TagCaptureOptions;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.sare.models.setcover.*;

public class DocumentSetCoverTest extends ModelTestsBase {

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
			.setTitle("test corpus")
			.setDescription("this is a test corpus");
		testDocument = (OpinionDocument)new OpinionDocument()
			.setContent("this is a test document")
			.setStore(testCorpus)
			.setTokenizingOptions(testTokenizingOptions);
		testCorpus.addDocument(testDocument);
		
//		em.getTransaction().begin();
//		persist(testCorpus);
//		persist(testDocument);
//		em.getTransaction().commit();
		
		testSetCover = new DocumentSetCover(testCorpus);
		testSetCoverDocument = (SetCoverDocument)new SetCoverDocument(testDocument)
			.setStore(testSetCover)
			.setTokenizingOptions(testTokenizingOptions);
		testSetCover.addDocument(testSetCoverDocument);

	}

	@After
	public void tearDown() throws Exception {
		//
	}

	@Test
	public void testTitle() {
		assertEquals(testCorpus.getTitle(), testSetCover.getTitle());
		
		testSetCover.setTitle("test set cover");
		
		assertFalse(testCorpus.getTitle().equals(testSetCover.getTitle()));
	}

	@Test
	public void testLanguage() {
		assertEquals(testCorpus.getLanguage(), testSetCover.getLanguage());
		
		testSetCover.setLanguage("tr");
		
		assertFalse(testCorpus.getLanguage().equals(testSetCover.getLanguage()));
	}

	@Test
	public void testDescription() {
		assertEquals(testCorpus.getDescription(), testSetCover.getDescription());
		
		testSetCover.setDescription("this is a set cover");
		
		assertFalse(testCorpus.getDescription().equals(testSetCover.getDescription()));
	}

	@Test
	public void testReplaceDocuments() {
		SetCoverDocument anotherSetCoverDocument = new SetCoverDocument();
		
		boolean replaced = testSetCover.replaceDocuments(testSetCoverDocument, anotherSetCoverDocument);
		
		assertTrue(replaced);
		assertFalse(Iterables.contains(testSetCover.getDocuments(), testSetCoverDocument));
		assertTrue(Iterables.contains(testSetCover.getDocuments(), anotherSetCoverDocument));
	}
}