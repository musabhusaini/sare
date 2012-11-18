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

public class TokenizedDocumentTests {

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