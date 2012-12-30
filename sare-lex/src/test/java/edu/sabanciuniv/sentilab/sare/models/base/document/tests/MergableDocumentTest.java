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
import edu.sabanciuniv.sentilab.sare.models.setcover.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticToken;

public class MergableDocumentTest {
	
	private static void assertMergeMapEqual(Map<String, Double> expected, Map<LinguisticToken, Double> actual) {
		for (Entry<LinguisticToken, Double> weightEntry : actual.entrySet()) {
			Double testValue = expected.get(weightEntry.getKey().toString());
			
			assertNotNull(testValue);
			assertEquals(testValue, weightEntry.getValue());
		}
	}
	
	private String testContent1;
	private String testContent2;
	
	private OpinionDocument testDocument1;
	private OpinionDocument testDocument2;
	
	private MergableDocument<?> testMergableDocument1;
	private MergableDocument<?> testMergableDocument2;
	
	private Map<String, Double> testMergeMap1;
	private Map<String, Double> testMergeMap2;
	
	private TokenizingOptions testTokenizingOptions;

	private void resetDocuments() {
		testDocument1
			.setContent(testContent1)
			.retokenize();
		testDocument2
			.setContent(testContent2)
			.retokenize();
		
		DocumentSetCover setCover = new DocumentSetCover(testDocument1.getStore());
		testMergableDocument1 = (SetCoverDocument)new SetCoverDocument(testDocument1)
			.setStore(setCover)
			.setTokenizingOptions(testTokenizingOptions);
		testMergableDocument2 = (SetCoverDocument)new SetCoverDocument(testDocument2)
			.setStore(setCover)
			.setTokenizingOptions(testTokenizingOptions);
	}

	@Before
	public void setUp() throws Exception {
		OpinionCorpus store = (OpinionCorpus)new OpinionCorpus()
			.setLanguage("en");
		
		testTokenizingOptions = new TokenizingOptions()
			.setLemmatized(true)
			.setTags(EnumSet.of(TagCaptureOptions.STARTS_WITH, TagCaptureOptions.IGNORE_CASE), "NN", "JJ");
		
		testContent1 = "The quick brown fox jumps over lazy dogs. Why would a lazy dog take this humiliation?";
		testDocument1 = (OpinionDocument)new OpinionDocument()
			.setStore(store)
			.setTokenizingOptions(testTokenizingOptions);
		
		testMergeMap1 = Maps.newHashMap();
		testMergeMap1.put("dog/NN", 3.0);
		testMergeMap1.put("fox/NN", 2.0);
		testMergeMap1.put("lazy/JJ", 3.0);
		
		testMergeMap2 = Maps.newHashMap(testMergeMap1);
		
		testMergeMap1.put("humiliation/NN", 1.0);
		testMergeMap1.put("quick/JJ", 1.0);
		testMergeMap1.put("brown/JJ", 1.0);
		
		testContent2 = "Why must the fox humiliate the lazy dog in this way?";
		testDocument2 = (OpinionDocument)new OpinionDocument()
			.setStore(store)
			.setTokenizingOptions(testTokenizingOptions);
		
		testMergeMap2.put("way/NN", 1.0);
		
		resetDocuments();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetMergedWeight() {
		double weight1 = testMergableDocument1
			.getMergedWeight(testDocument2);
		double weight2 = testMergableDocument2
			.getMergedWeight(testDocument1);
		
		assertFalse(weight1 == weight2);
		assertEquals(11.0, weight1, 0);
		assertEquals(9.0, weight2, 0);
	}
	
	@Test
	public void testMerge() {
		Map<LinguisticToken, Double> mergeMap1 = testMergableDocument1
			.merge(testMergableDocument2)
			.getTokenWeightMap();
		
		Map<LinguisticToken, Double> weightMap2 = testMergableDocument2.getTokenWeightMap();
		for (Entry<LinguisticToken, Double> entry : mergeMap1.entrySet()) {
			Double value = weightMap2.get(entry.getKey());
			assertTrue(value == null || value == 0.0);
		}
		
		resetDocuments();
	
		Map<LinguisticToken, Double> mergeMap2 = (testMergableDocument2)
			.merge(testMergableDocument1)
			.getTokenWeightMap();
		
		Map<LinguisticToken, Double> weightMap1 = testMergableDocument1.getTokenWeightMap();
		for (Entry<LinguisticToken, Double> entry : mergeMap2.entrySet()) {
			Double value = weightMap1.get(entry.getKey());
			assertTrue(value == null || value == 0.0);
		}
		
		assertFalse(mergeMap1.size() == mergeMap2.size());
		
		assertMergeMapEqual(testMergeMap1, mergeMap1);
		assertMergeMapEqual(testMergeMap2, mergeMap2);
	}
}