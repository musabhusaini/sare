package edu.sabanciuniv.sentilab.utils.text.nlp.factory.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.sabanciuniv.sentilab.utils.text.nlp.annotations.LinguisticProcessorInfo;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.ILinguisticProcessor;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticProcessor;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticText;
import edu.sabanciuniv.sentilab.utils.text.nlp.factory.LinguisticProcessorFactory;
import edu.sabanciuniv.sentilab.utils.text.nlp.factory.LinguisticProcessorFactoryOptions;

public class LinguisticProcessorFactoryTests {

	@LinguisticProcessorInfo(
		name = "test",
		language = "xx"
	)
	public static class TestLinguisticProcessor
		extends LinguisticProcessor {

		@Override
		public LinguisticText decompose(String text) {
			return null;
		}

		@Override
		public LinguisticText tag(String text) {
			return null;
		}

		@Override
		public LinguisticText parse(String text) {
			return null;
		}
		
	}
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateWithOnlyLanguage() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions();
		options.setLanguage("xx");
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory(options);
		ILinguisticProcessor processor = factory.create();
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}
	
	@Test
	public void testCreateWithOnlyLanguageCased() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions();
		options.setLanguage("Xx");
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory(options);
		ILinguisticProcessor processor = factory.create();
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}
	
	@Test
	public void testCreateWithOnlyName() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions();
		options.setName("test");
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory(options);
		ILinguisticProcessor processor = factory.create();
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}

	@Test
	public void testCreateWithOnlyNameIgnoreCase() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions();
		options
			.setName("TEsT")
			.setIgnoreNameCase(true);
		
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory(options);
		ILinguisticProcessor processor = factory.create();
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}
	
	@Test
	public void testCreateWithIncorrectNameIgnoreCase() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions();
		options
			.setName("TET")
			.setIgnoreNameCase(true);
		
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory(options);
		ILinguisticProcessor processor = factory.create();
		
		assertNull(processor);
	}
	
	@Test
	public void testCreateWithIncorrectlyCasedNameNoIgnoreCase() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions();
		options
			.setName("TEsT")
			.setIgnoreNameCase(false);
		
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory(options);
		ILinguisticProcessor processor = factory.create();
		
		assertNull(processor);
	}
	
	@Test
	public void testCreateWithNameAndLanguage() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions();
		options
			.setName("test")
			.setLanguage("xx");
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory(options);
		ILinguisticProcessor processor = factory.create();
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}
	
	@Test
	public void testCreateWithNameAndIncorrectLanguage() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions();
		options
			.setName("test")
			.setLanguage("xy");
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory(options);
		ILinguisticProcessor processor = factory.create();
		
		assertNull(processor);
	}
	
	@Test
	public void testCreateWithLanguageAndIncorrectName() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions();
		options
			.setName("test1")
			.setLanguage("xx");
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory(options);
		ILinguisticProcessor processor = factory.create();
		
		assertNull(processor);
	}
	
	@Test
	public void testCreateWithIncorrectNameAndLanguage() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions();
		options
			.setName("test1")
			.setLanguage("xy");
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory(options);
		ILinguisticProcessor processor = factory.create();
		
		assertNull(processor);
	}
	
	@Test
	public void testCreateWithNoNameAndLanguage() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions();
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory(options);
		ILinguisticProcessor processor = factory.create();
		
		assertNotNull(processor);
	}
	
	@Test
	public void testCreateWithNameAndLanguageAndMustTag() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions();
		options
			.setName("test")
			.setLanguage("xx")
			.setMustTag(true);
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory(options);
		ILinguisticProcessor processor = factory.create();
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}

	@Test
	public void testCreateWithNameAndLanguageAndMustTagAndMustParse() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions();
		options
			.setName("test")
			.setLanguage("xx")
			.setMustTag(true)
			.setMustParse(true);
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory(options);
		ILinguisticProcessor processor = factory.create();
		
		assertNull(processor);
	}

	@Test
	public void testCreateWithNameAndLanguageAndMustParse() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions();
		options
			.setName("test")
			.setLanguage("xx")
			.setMustParse(true);
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory(options);
		ILinguisticProcessor processor = factory.create();
		
		assertNull(processor);
	}
}