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

package edu.sabanciuniv.sentilab.sare.controllers.setcover.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.*;

import org.junit.*;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionCorpusFactory;
import edu.sabanciuniv.sentilab.sare.controllers.setcover.SetCoverController;
import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.sare.models.setcover.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.PosTag;

public class SetCoverControllerTest {

	private String testXmlCorpusFilename;
	private OpinionCorpus testCorpus;
	private TokenizingOptions testTokenizingOptions;
	
	@Before
	public void setUp() throws Exception {
		testXmlCorpusFilename = "/test-small-corpus.xml";
		
		OpinionCorpusFactory factory = (OpinionCorpusFactory)new OpinionCorpusFactory()
			.setFile(new File(getClass().getResource(testXmlCorpusFilename).getPath()));
		testCorpus = factory.create();
		
		testTokenizingOptions = new TokenizingOptions()
			.setLemmatized(true)
			.setTags(PosTag.NOUN);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCreateWithOnlyTitleSetsTitle() {
		String testTitle = "a test";
		DocumentSetCover setcover;
		try {
			SetCoverController testController = (SetCoverController)new SetCoverController()
				.setStore(testCorpus).setTitle(testTitle);
			setcover = testController.create();
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
		assertNotNull(setcover);
		assertEquals(testTitle, setcover.getTitle());
		assertEquals(0, Iterables.size(setcover.getDocuments()));
	}
	
	// TODO: move this to a new DocumentSetCoverTest.
	@Test
	public void testClearClears() {
		DocumentSetCover setcover;
		try {
			setcover = new SetCoverController()
				.setStore(testCorpus).setTokenizingOptions(testTokenizingOptions)
				.create();
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
		assertTrue(Iterables.size(setcover.getDocuments()) > 0);
		setcover.clear();
		assertFalse(Iterables.size(setcover.getDocuments()) > 0);
		assertNull(setcover.getTokenizingOptions());
		assertNull(setcover.getWeightCoverage());
	}
	
	@Test
	public void testCreateWithTokenizingOptions() {
		DocumentSetCover setcover;
		try {
			setcover = new SetCoverController()
				.setStore(testCorpus).setTokenizingOptions(testTokenizingOptions)
				.create();
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
		assertNotNull(setcover);
		assertEquals(8, Iterables.size(setcover.getDocuments()));
		
		int index=0;
		SetCoverDocument firstDoc = Iterables.get(setcover.getDocuments(SetCoverDocument.class), index);
		assertNotNull(firstDoc);
		assertNotNull(setcover.getTokenizingOptions());
		assertTrue(Iterables.size(setcover.getTokenizingOptions().getTags()) > 0);
		assertEquals(94.0, firstDoc.getWeight(), 0);
		
		for (SetCoverDocument doc : setcover.getDocuments(SetCoverDocument.class)) {
			assertFalse(doc.getContent().equals("This hotel was great; I loved the bathroom!"));
		}
	}
	
	@Test
	public void testCreateWithWeightRatio() {
		double weightCoverage = 0.8;
		DocumentSetCover setCover;
		try {
			setCover = new SetCoverController()
				.setStore(testCorpus)
				.setTokenizingOptions(testTokenizingOptions)
				.setWeightCoverage(weightCoverage)
				.create();
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
		assertNotNull(setCover);
		assertEquals(4, Iterables.size(setCover.getDocuments()));
		assertEquals(weightCoverage, setCover.getWeightCoverage(), 0);
		
		int index=0;
		SetCoverDocument firstDoc = Iterables.get(setCover.getDocuments(SetCoverDocument.class), index);
		assertNotNull(firstDoc);
		
		double firstWeight = 94.0;
		assertEquals(firstWeight, firstDoc.getWeight(), 0);
		
		for (SetCoverDocument doc : setCover.getDocuments(SetCoverDocument.class)) {
			assertFalse(doc.getContent().equals("This hotel was great; I loved the bathroom!"));
		}
	}
	
	@Test
	public void testAdjustCoverage() {
		double weightCoverage = 0.5;
		DocumentSetCover setcover;
		try {
			setcover = new SetCoverController()
				.setStore(testCorpus)
				.setTokenizingOptions(testTokenizingOptions)
				.setWeightCoverage(weightCoverage)
				.create();
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}

		assertNotNull(setcover);
		assertEquals(2, Iterables.size(setcover.getDocuments()));
		assertEquals(weightCoverage, setcover.getWeightCoverage(), 0);
		
		weightCoverage = 0.8;
		setcover = setcover.adjustCoverage(weightCoverage);
		
		assertNotNull(setcover);
		assertEquals(4, Iterables.size(setcover.getDocuments()));
		assertEquals(weightCoverage, setcover.getWeightCoverage(), 0);
		
		int index=0;
		SetCoverDocument firstDoc = Iterables.get(setcover.getDocuments(SetCoverDocument.class), index);
		assertNotNull(firstDoc);
		
		double firstWeight = 94.0;
		assertEquals(firstWeight, firstDoc.getWeight(), 0);
		
		for (SetCoverDocument doc : setcover.getDocuments(SetCoverDocument.class)) {
			assertFalse(doc.getContent().equals("This hotel was great; I loved the bathroom!"));
		}
	}
	
	@Test
	public void testCalculateCoverageMatrix() {
		DocumentSetCover setcover;
		try {
			setcover = new SetCoverController()
				.setStore(testCorpus)
				.setTokenizingOptions(testTokenizingOptions)
				.create();
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
		assertNotNull(setcover);
		
		Map<Integer, Double> matrix = setcover.calculateCoverageMatrix(10);
		
		assertNotNull(matrix);
		assertEquals(11, matrix.size());
		assertNotNull(matrix.get(50));
		assertEquals(0.2, matrix.get(50), 0.0005);
	}
}