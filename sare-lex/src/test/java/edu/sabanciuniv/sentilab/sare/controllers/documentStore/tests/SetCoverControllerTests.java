package edu.sabanciuniv.sentilab.sare.controllers.documentStore.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.EnumSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.controllers.documentStore.OpinionCorpusFactory;
import edu.sabanciuniv.sentilab.sare.controllers.documentStore.SetCoverController;
import edu.sabanciuniv.sentilab.sare.models.document.SetCoverDocument;
import edu.sabanciuniv.sentilab.sare.models.document.base.TokenizingOptions;
import edu.sabanciuniv.sentilab.sare.models.document.base.TokenizingOptions.TagCaptureOptions;
import edu.sabanciuniv.sentilab.sare.models.documentStore.DocumentSetCover;
import edu.sabanciuniv.sentilab.sare.models.documentStore.OpinionCorpus;

public class SetCoverControllerTests {

	private String testXmlCorpusFilename;
	private OpinionCorpus testCorpus;
	private TokenizingOptions testTokenizingOptions;
	private SetCoverController testController;
	
	@Before
	public void setUp() throws Exception {
		testXmlCorpusFilename = "/test-small-corpus.xml";
		
		OpinionCorpusFactory factory = new OpinionCorpusFactory();
		testCorpus = factory.create(new File(getClass().getResource(testXmlCorpusFilename).getPath()));
		
		testTokenizingOptions = new TokenizingOptions()
			.setLemmatized(true)
			.setTags(EnumSet.of(TagCaptureOptions.IGNORE_CASE, TagCaptureOptions.STARTS_WITH), "NN");
		
		testController = new SetCoverController();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateWithTokenizingOptions() {
		DocumentSetCover setCover = testController.create(testCorpus, testTokenizingOptions);
		
		assertNotNull(setCover);
		assertEquals(8, Iterables.size(setCover.getDocuments()));
		
		int index=0;
		SetCoverDocument firstDoc = Iterables.get(setCover.getDocuments(), index);
		assertNotNull(firstDoc);
		assertEquals(94.0, firstDoc.getWeight(), 0);
		
		for (SetCoverDocument doc : setCover.getDocuments()) {
			assertFalse(doc.getContent().equals("This hotel was great; I loved the bathroom!"));
		}
	}
}