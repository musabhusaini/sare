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

package edu.sabanciuniv.sentilab.sare.models.base.document.tests;

import static org.junit.Assert.*;

import java.util.*;
import java.util.Map.Entry;

import org.junit.*;

import com.google.common.collect.Maps;

import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.base.document.TokenizingOptions.TagCaptureOptions;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticToken;

public class FullTextDocumentTest {

	private String testContent;
	private TokenizingOptions testTokenizingOptions;
	private OpinionDocument testDocument;
	private Map<String, Double> testNnjjMap;
	private Map<String, Double> testNnjjLemmMap;
	
	@Before
	public void setUp() throws Exception {
		OpinionCorpus store = (OpinionCorpus)new OpinionCorpus()
			.setLanguage("en");
		
		this.testContent = "The quick brown fox jumps over lazy dogs. Why would a lazy dog take this humiliation?";
		this.testNnjjMap = Maps.newHashMap();
		this.testNnjjMap.put("humiliation/NN", 1.0);
		this.testNnjjMap.put("quick/JJ", 1.0);
		this.testNnjjMap.put("fox/NN", 1.0);
		this.testNnjjMap.put("lazy/JJ", 2.0);
		this.testNnjjMap.put("brown/JJ", 1.0);

		this.testNnjjLemmMap = Maps.newHashMap(this.testNnjjMap);
		
		this.testNnjjMap.put("dog/NN", 1.0);
		this.testNnjjMap.put("dogs/NNS", 1.0);
		
		this.testNnjjLemmMap.put("dog/NN", 2.0);

		this.testTokenizingOptions = new TokenizingOptions()
			.setTags(EnumSet.of(TagCaptureOptions.STARTS_WITH, TagCaptureOptions.IGNORE_CASE), "NN", "JJ");
		
		this.testDocument = (OpinionDocument)new OpinionDocument()
			.setStore(store);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetTokenWeightMap() {
		Map<LinguisticToken, Double> weightMap = this.testDocument
			.setContent(this.testContent)
			.setTokenizingOptions(this.testTokenizingOptions)
			.getTokenWeightMap();
		
		assertNotNull(weightMap);
		assertEquals(this.testNnjjMap.size(), weightMap.size());
		
		for (Entry<LinguisticToken, Double> weightEntry : weightMap.entrySet()) {
			Double testValue = this.testNnjjMap.get(weightEntry.getKey().toString());
			
			assertNotNull(testValue);
			assertEquals(testValue, weightEntry.getValue());
		}
	}
	
	@Test
	public void testGetLemmatizedTokenWeightMap() {
		Map<LinguisticToken, Double> weightMap = this.testDocument
			.setContent(this.testContent)
			.setTokenizingOptions(this.testTokenizingOptions.clone().setLemmatized(true))
			.getTokenWeightMap();
		
		assertNotNull(weightMap);
		assertEquals(this.testNnjjLemmMap.size(), weightMap.size());
		
		for (Entry<LinguisticToken, Double> weightEntry : weightMap.entrySet()) {
			Double testValue = this.testNnjjLemmMap.get(weightEntry.getKey().toString());
			
			assertNotNull(testValue);
			assertEquals(testValue, weightEntry.getValue());
		}
	}
	
	@Test
	public void testSetContentChangesMap() {
		this.testDocument
			.setContent(this.testContent)
			.setTokenizingOptions(this.testTokenizingOptions)
			.retokenize();
		
		Map<LinguisticToken, Double> weightMap = this.testDocument
			.setContent("Rest of the animals were not as lazy.")
			.getTokenWeightMap();
		
		assertNotNull(weightMap);
		assertTrue(weightMap.size() > 0);
		assertFalse(this.testNnjjMap.size() == weightMap.size());
	}
	
	@Test
	public void testSetTokenizingOptionsChangesMap() {
		this.testDocument
			.setContent(this.testContent)
			.setTokenizingOptions(this.testTokenizingOptions)
			.retokenize();
		
		String posTag = "nn";
		Map<LinguisticToken, Double> weightMap = this.testDocument
			.setTokenizingOptions(new TokenizingOptions()
				.setTags(EnumSet.of(TagCaptureOptions.STARTS_WITH, TagCaptureOptions.IGNORE_CASE), posTag))
			.getTokenWeightMap();
		
		assertNotNull(weightMap);
		assertTrue(weightMap.size() > 0);
		assertFalse(this.testNnjjMap.size() == weightMap.size());
		
		for (Entry<LinguisticToken, Double> weightEntry : weightMap.entrySet()) {
			assertTrue(weightEntry.getKey().getPosTag().toLowerCase().startsWith(posTag));
		}
	}
}