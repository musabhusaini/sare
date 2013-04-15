package edu.sabanciuniv.sentilab.models.opinion.tests;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.*;

import com.google.common.collect.Maps;

import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.sabanciuniv.sentilab.sare.models.opinion.AspectOpinionMinedDocument;

public class AspectOpinionMinedDocumentTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testSetAspectPolarities() {
		Map<AspectLexicon, Double> testPolarities = Maps.newHashMap();
		testPolarities.put(new AspectLexicon(), 0.4675);
		AspectOpinionMinedDocument document = new AspectOpinionMinedDocument()
			.setAspectPolarities(testPolarities);
		
		Map<AspectLexicon, Double> actualPolarities = document.getAspectPolarities();
		assertNotNull(actualPolarities);
		assertEquals(testPolarities.size(), actualPolarities.size());
		for (Entry<AspectLexicon, Double> actualEntry : actualPolarities.entrySet()) {
			assertTrue(testPolarities.containsKey(actualEntry.getKey()));
			assertEquals(testPolarities.get(actualEntry.getKey()), actualEntry.getValue());
		}
	}
}
