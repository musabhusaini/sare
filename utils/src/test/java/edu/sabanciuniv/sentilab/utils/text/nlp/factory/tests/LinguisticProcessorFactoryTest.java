/*
 * Sentilab SARE: a Sentiment Analysis Research Environment
 * Copyright (C) 2013 Sabanci University Sentilab
 * http://sentilab.sabanciuniv.edu
 * 
 * This file is part of SARE.
 * 
 * SARE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * SARE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sabanciuniv.sentilab.utils.text.nlp.factory.tests;

import static org.junit.Assert.*;

import org.junit.*;

import edu.sabanciuniv.sentilab.utils.text.nlp.annotations.LinguisticProcessorInfo;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.factory.*;

public class LinguisticProcessorFactoryTest {

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
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions()
			.setLanguage("xx");
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory();
		ILinguisticProcessor processor = factory.create(options);
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}
	
	@Test
	public void testCreateWithOnlyLanguageCased() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions()
			.setLanguage("Xx");
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory();
		ILinguisticProcessor processor = factory.create(options);
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}
	
	@Test
	public void testCreateWithOnlyName() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions()
			.setName("test");
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory();
		ILinguisticProcessor processor = factory.create(options);
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}

	@Test
	public void testCreateWithOnlyNameIgnoreCase() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions()
			.setName("TEsT")
			.setIgnoreNameCase(true);
		
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory();
		ILinguisticProcessor processor = factory.create(options);
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}
	
	@Test
	public void testCreateWithIncorrectNameIgnoreCase() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions()
			.setName("TET")
			.setIgnoreNameCase(true);
		
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory();
		ILinguisticProcessor processor = factory.create(options);
		
		assertNull(processor);
	}
	
	@Test
	public void testCreateWithIncorrectlyCasedNameNoIgnoreCase() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions()
			.setName("TEsT")
			.setIgnoreNameCase(false);
		
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory();
		ILinguisticProcessor processor = factory.create(options);
		
		assertNull(processor);
	}
	
	@Test
	public void testCreateWithNameAndLanguage() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions()
			.setName("test")
			.setLanguage("xx");
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory();
		ILinguisticProcessor processor = factory.create(options);
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}
	
	@Test
	public void testCreateWithNameAndIncorrectLanguage() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions()
			.setName("test")
			.setLanguage("xy");
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory();
		ILinguisticProcessor processor = factory.create(options);
		
		assertNull(processor);
	}
	
	@Test
	public void testCreateWithLanguageAndIncorrectName() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions()
			.setName("test1")
			.setLanguage("xx");
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory();
		ILinguisticProcessor processor = factory.create(options);
		
		assertNull(processor);
	}
	
	@Test
	public void testCreateWithIncorrectNameAndLanguage() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions()
			.setName("test1")
			.setLanguage("xy");
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory();
		ILinguisticProcessor processor = factory.create(options);
		
		assertNull(processor);
	}
	
	@Test
	public void testCreateWithNoNameAndLanguage() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions();
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory();
		ILinguisticProcessor processor = factory.create(options);
		
		assertNotNull(processor);
	}
	
	@Test
	public void testCreateWithNameAndLanguageAndMustTag() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions()
			.setName("test")
			.setLanguage("xx")
			.setMustTag(true);
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory();
		ILinguisticProcessor processor = factory.create(options);
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}

	@Test
	public void testCreateWithNameAndLanguageAndMustTagAndMustParse() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions()
			.setName("test")
			.setLanguage("xx")
			.setMustTag(true)
			.setMustParse(true);
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory();
		ILinguisticProcessor processor = factory.create(options);
		
		assertNull(processor);
	}

	@Test
	public void testCreateWithNameAndLanguageAndMustParse() {
		LinguisticProcessorFactoryOptions options = new LinguisticProcessorFactoryOptions()
			.setName("test")
			.setLanguage("xx")
			.setMustParse(true);
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory();
		ILinguisticProcessor processor = factory.create(options);
		
		assertNull(processor);
	}
}