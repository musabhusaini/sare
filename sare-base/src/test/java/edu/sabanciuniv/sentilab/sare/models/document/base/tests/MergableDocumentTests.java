package edu.sabanciuniv.sentilab.sare.models.document.base.tests;

import static org.junit.Assert.*;

import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;

import edu.sabanciuniv.sentilab.sare.models.document.base.TokenizingOptions;
import edu.sabanciuniv.sentilab.sare.models.document.base.TokenizingOptions.TagCaptureOptions;
import edu.sabanciuniv.sentilab.sare.models.document.OpinionDocument;
import edu.sabanciuniv.sentilab.sare.models.documentStore.OpinionCorpus;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticToken;

public class MergableDocumentTests {
	
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
		double weight1 = testDocument1
			.getMergedWeight(testDocument2);
		double weight2 = testDocument2
			.getMergedWeight(testDocument1);
		
		assertFalse(weight1 == weight2);
		assertEquals(11.0, weight1, 0);
		assertEquals(9.0, weight2, 0);
	}
	
	@Test
	public void testMerge() {
		Map<LinguisticToken, Double> mergeMap1 = testDocument1
			.merge(testDocument2)
			.getTokenWeightMap();
		
		Map<LinguisticToken, Double> weightMap2 = testDocument2.getTokenWeightMap();
		for (Entry<LinguisticToken, Double> entry : mergeMap1.entrySet()) {
			Double value = weightMap2.get(entry.getKey());
			assertTrue(value == null || value == 0.0);
		}
		
		resetDocuments();
	
		Map<LinguisticToken, Double> mergeMap2 = (testDocument2)
			.merge(testDocument1)
			.getTokenWeightMap();
		
		Map<LinguisticToken, Double> weightMap1 = testDocument1.getTokenWeightMap();
		for (Entry<LinguisticToken, Double> entry : mergeMap2.entrySet()) {
			Double value = weightMap1.get(entry.getKey());
			assertTrue(value == null || value == 0.0);
		}
		
		assertFalse(mergeMap1.size() == mergeMap2.size());
		
		assertMergeMapEqual(testMergeMap1, mergeMap1);
		assertMergeMapEqual(testMergeMap2, mergeMap2);
	}
}