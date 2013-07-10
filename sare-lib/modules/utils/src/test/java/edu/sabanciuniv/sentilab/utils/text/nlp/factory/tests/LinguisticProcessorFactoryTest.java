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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sabanciuniv.sentilab.utils.text.nlp.factory.tests;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.*;

import edu.sabanciuniv.sentilab.utils.text.nlp.annotations.LinguisticProcessorInfo;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.factory.*;

public class LinguisticProcessorFactoryTest {

	@LinguisticProcessorInfo(
		name = "test",
		languageCode = "xx"
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

		@Override
		public Map<String, String> getBasicPosTags() {
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
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory().setLanguage("xx");
		LinguisticProcessorLike processor = factory.create();
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}
	
	@Test
	public void testCreateWithOnlyLanguageCased() {
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory().setLanguage("Xx");
		LinguisticProcessorLike processor = factory.create();
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}
	
	@Test
	public void testCreateWithOnlyName() {
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory().setName("test");
		LinguisticProcessorLike processor = factory.create();
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}

	@Test
	public void testCreateWithOnlyNameIgnoreCase() {
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory()
			.setName("TEsT")
			.setIgnoreNameCase(true);
		LinguisticProcessorLike processor = factory.create();
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}
	
	@Test
	public void testCreateWithIncorrectNameIgnoreCase() {
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory()
			.setName("TET")
			.setIgnoreNameCase(true);
		LinguisticProcessorLike processor = factory.create();
		
		assertNull(processor);
	}
	
	@Test
	public void testCreateWithIncorrectlyCasedNameNoIgnoreCase() {
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory()
			.setName("TEsT")
			.setIgnoreNameCase(false);
		LinguisticProcessorLike processor = factory.create();
		
		assertNull(processor);
	}
	
	@Test
	public void testCreateWithNameAndLanguage() {
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory()
			.setName("test")
			.setLanguage("xx");
		LinguisticProcessorLike processor = factory.create();
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}
	
	@Test
	public void testCreateWithNameAndIncorrectLanguage() {
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory()
			.setName("test")
			.setLanguage("xy");
		LinguisticProcessorLike processor = factory.create();
		
		assertNull(processor);
	}
	
	@Test
	public void testCreateWithLanguageAndIncorrectName() {
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory()
			.setName("test1")
			.setLanguage("xx");
		LinguisticProcessorLike processor = factory.create();
		
		assertNull(processor);
	}
	
	@Test
	public void testCreateWithIncorrectNameAndLanguage() {
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory()
			.setName("test1")
			.setLanguage("xy");
		LinguisticProcessorLike processor = factory.create();
		
		assertNull(processor);
	}
	
	@Test
	public void testCreateWithNoNameAndLanguage() {
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory();
		LinguisticProcessorLike processor = factory.create();
		
		assertNotNull(processor);
	}
	
	@Test
	public void testCreateWithNameAndLanguageAndMustTag() {
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory()
			.setName("test")
			.setLanguage("xx")
			.setMustTag(true);
		LinguisticProcessorLike processor = factory.create();
		
		assertNotNull(processor);
		assertEquals(processor.getClass(), TestLinguisticProcessor.class);
	}

	@Test
	public void testCreateWithNameAndLanguageAndMustTagAndMustParse() {
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory()
			.setName("test")
			.setLanguage("xx")
			.setMustTag(true)
			.setMustParse(true);
		LinguisticProcessorLike processor = factory.create();
		
		assertNull(processor);
	}

	@Test
	public void testCreateWithNameAndLanguageAndMustParse() {
		LinguisticProcessorFactory factory = new LinguisticProcessorFactory()
			.setName("test")
			.setLanguage("xx")
			.setMustParse(true);
		LinguisticProcessorLike processor = factory.create();
		
		assertNull(processor);
	}
}