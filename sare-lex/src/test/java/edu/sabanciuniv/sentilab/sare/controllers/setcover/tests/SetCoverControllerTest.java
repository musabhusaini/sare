package edu.sabanciuniv.sentilab.sare.controllers.setcover.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.EnumSet;
import java.util.Map;

import org.junit.*;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionCorpusFactory;
import edu.sabanciuniv.sentilab.sare.controllers.setcover.SetCoverController;
import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.base.document.TokenizingOptions.TagCaptureOptions;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.sare.models.setcover.*;

public class SetCoverControllerTest {

	private String testXmlCorpusFilename;
	private OpinionCorpus testCorpus;
	private TokenizingOptions testTokenizingOptions;
	private SetCoverController testController;
	
	@Before
	public void setUp() throws Exception {
		testXmlCorpusFilename = "/test-small-corpus.xml";
		
		OpinionCorpusFactory factory = new OpinionCorpusFactory();
		testCorpus = factory.create(new OpinionCorpusFactoryOptions()
			.setFile(new File(getClass().getResource(testXmlCorpusFilename).getPath())));
		
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
		DocumentSetCover setCover;
		try {
			setCover = testController.create(new SetCoverFactoryOptions()
				.setStore(testCorpus).setTokenizingOptions(testTokenizingOptions));
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
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
	
	@Test
	public void testCreateWithWeightRatio() {
		DocumentSetCover setCover;
		try {
			setCover = testController.create(new SetCoverFactoryOptions()
				.setStore(testCorpus)
				.setTokenizingOptions(testTokenizingOptions)
				.setWeightCoverage(0.8));
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
		assertNotNull(setCover);
		assertEquals(5, Iterables.size(setCover.getDocuments()));
		
		int index=0;
		SetCoverDocument firstDoc = Iterables.get(setCover.getDocuments(), index);
		assertNotNull(firstDoc);
		
		double firstWeight = 94.0;
		assertEquals(firstWeight, firstDoc.getWeight(), 0);
		
		for (SetCoverDocument doc : setCover.getDocuments()) {
			assertFalse(doc.getContent().equals("This hotel was great; I loved the bathroom!"));
		}
	}
	
	@Test
	public void testCalculateCoverageMatrix() {
		DocumentSetCover setCover;
		try {
			setCover = testController.create(new SetCoverFactoryOptions()
				.setStore(testCorpus).setTokenizingOptions(testTokenizingOptions));
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
		assertNotNull(setCover);
		
		Map<Integer, Double> matrix = testController.calculateCoverageMatrix(setCover, 10);
		
		assertNotNull(matrix);
		assertEquals(11, matrix.size());
		assertNotNull(matrix.get(50));
		assertEquals(0.2, matrix.get(50), 0.0005);
	}
}