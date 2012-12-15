package edu.sabanciuniv.sentilab.sare.controllers.opinion.tests;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.*;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionCorpusFactory;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

public class OpinionCorpusFactoryTest {
	
	private OpinionCorpusFactory testFactory;
	
	private String testXmlCorpusFilename;
	private OpinionCorpus expectedXmlCorpus;
	
	private String testZipCorpusFilename;
	private OpinionCorpus expectedZipCorpus;
	
	private String testTextCorpusFilename;
	private OpinionCorpus expectedTextCorpus;
	
	private String testContent;
	private OpinionCorpus expectedContentCorpus;
	
	private String testNoPolarityContent;
	private OpinionCorpus expectedNoPolarityCorpus;
	
	@Before
	public void setUp() throws Exception {
		testFactory = new OpinionCorpusFactory();
		
		testXmlCorpusFilename = "/test-corpus.xml";
		
		expectedXmlCorpus = (OpinionCorpus)new OpinionCorpus()
			.setDocuments(Lists.newArrayList(new OpinionDocument(), new OpinionDocument()))
			.setTitle("test-xml-corpus")
			.setDescription("test")
			.setLanguage("en");
		
		testZipCorpusFilename = "/test-corpus.zip";
		expectedZipCorpus = (OpinionCorpus)new OpinionCorpus()
			.setDocuments(Lists.newArrayList(new OpinionDocument(), new OpinionDocument(), new OpinionDocument(), new OpinionDocument(), new OpinionDocument()))
			.setTitle("test-zip-corpus")
			.setDescription("test")
			.setLanguage("en");
		
		testTextCorpusFilename = "/test-corpus.txt";
		expectedTextCorpus = (OpinionCorpus)new OpinionCorpus()
			.setDocuments(Lists.newArrayList(new OpinionDocument(), new OpinionDocument()));
		
		String content = "this is a great test document";
		double polarity = 0.9;
		
		testContent = String.format("%s|%f", content, polarity);
		expectedContentCorpus = (OpinionCorpus)new OpinionCorpus()
			.addDocument((OpinionDocument)new OpinionDocument().setPolarity(polarity).setContent(content));
		
		testNoPolarityContent = content;
		expectedNoPolarityCorpus = (OpinionCorpus)new OpinionCorpus()
			.addDocument((OpinionDocument)new OpinionDocument().setContent(content));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateFromXmlFile() {
		OpinionCorpus actualCorpus = null;
		try {
			actualCorpus = testFactory.create(new OpinionCorpusFactoryOptions()
				.setFile(new File(getClass().getResource(testXmlCorpusFilename).getPath())));
		} catch (IllegalFactoryOptionsException e) {
			fail("could not open file");
		}
		
		assertNotNull(actualCorpus);
		assertEquals(expectedXmlCorpus.getTitle(), actualCorpus.getTitle());
		assertEquals(expectedXmlCorpus.getDescription(), actualCorpus.getDescription());
		assertEquals(expectedXmlCorpus.getLanguage(), actualCorpus.getLanguage());
		assertEquals(Iterables.size(expectedXmlCorpus.getDocuments()), Iterables.size(actualCorpus.getDocuments()));
	}
	
	@Test
	public void testCreateFromZipFile() {
		OpinionCorpus actualCorpus = null;
		try {
			actualCorpus = testFactory.create(new OpinionCorpusFactoryOptions()
				.setFile(new File(getClass().getResource(testZipCorpusFilename).getPath())));
		} catch (IllegalFactoryOptionsException e) {
			fail("error reading input file");
		}
		
		assertNotNull(actualCorpus);
		assertEquals(expectedZipCorpus.getTitle(), actualCorpus.getTitle());
		assertEquals(expectedZipCorpus.getDescription(), actualCorpus.getDescription());
		assertEquals(expectedZipCorpus.getLanguage(), actualCorpus.getLanguage());
		assertEquals(Iterables.size(expectedZipCorpus.getDocuments()), Iterables.size(actualCorpus.getDocuments()));
	}
	
	@Test
	public void testCreateFromTextFile() {
		OpinionCorpus actualCorpus = null;
		try {
			actualCorpus = testFactory.create(new OpinionCorpusFactoryOptions()
				.setFile(new File(getClass().getResource(testTextCorpusFilename).getPath()))
				.setTextDelimiter("|"));
		} catch (IllegalFactoryOptionsException e) {
			fail("error reading input file");
		}
		
		assertNotNull(actualCorpus);
		assertEquals(Iterables.size(expectedTextCorpus.getDocuments()), Iterables.size(actualCorpus.getDocuments()));
	}
	
	@Test
	public void testCreateFromContent() {
		OpinionCorpus actualCorpus = null;
		try {
			actualCorpus = testFactory.create(new OpinionCorpusFactoryOptions()
				.setContent(testContent)
				.setFormat("txt")
				.setTextDelimiter("|"));
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create");
		}
		
		assertNotNull(actualCorpus);
		assertEquals(Iterables.size(expectedContentCorpus.getDocuments()), Iterables.size(actualCorpus.getDocuments()));
		
		OpinionDocument expectedDocument = Iterables.getFirst(expectedContentCorpus.getDocuments(), null);
		OpinionDocument actualDocument = Iterables.getFirst(actualCorpus.getDocuments(), null);
		assertNotNull(actualDocument);
		
		assertEquals(expectedDocument.getContent(), actualDocument.getContent());
		assertEquals(expectedDocument.getPolarity(), actualDocument.getPolarity(), 0.005);
	}
	
	@Test
	public void testCreateFromNoPolarityContent() {
		OpinionCorpus actualCorpus = null;
		try {
			actualCorpus = testFactory.create(new OpinionCorpusFactoryOptions()
				.setContent(testNoPolarityContent)
				.setFormat("txt")
				.setTextDelimiter("|"));
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create");
		}
		
		assertNotNull(actualCorpus);
		assertEquals(Iterables.size(expectedNoPolarityCorpus.getDocuments()), Iterables.size(actualCorpus.getDocuments()));
		
		OpinionDocument expectedDocument = Iterables.getFirst(expectedNoPolarityCorpus.getDocuments(), null);
		OpinionDocument actualDocument = Iterables.getFirst(actualCorpus.getDocuments(), null);
		assertNotNull(actualDocument);
		
		assertEquals(expectedDocument.getContent(), actualDocument.getContent());
		assertNull(actualDocument.getPolarity());
	}
	
	@Test
	public void testCreateFromNothingFails() {
		boolean thrown = false;
		
		try {
			testFactory.create(new OpinionCorpusFactoryOptions());
		} catch (IllegalFactoryOptionsException e) {
			thrown = true;
		}
		
		assertTrue(thrown);
	}
}