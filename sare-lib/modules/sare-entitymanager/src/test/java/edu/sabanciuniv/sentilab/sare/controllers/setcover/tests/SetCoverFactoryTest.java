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

import org.junit.*;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionCorpusFactory;
import edu.sabanciuniv.sentilab.sare.controllers.setcover.SetCoverFactory;
import edu.sabanciuniv.sentilab.sare.models.base.document.TokenizingOptions;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.sare.models.setcover.*;
import edu.sabanciuniv.sentilab.sare.tests.PersistenceTestsBase;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.PosTag;

public class SetCoverFactoryTest
		extends PersistenceTestsBase {

	private String testXmlCorpusFilename;
	private OpinionCorpus testCorpus;
	private TokenizingOptions testTokenizingOptions;
	private SetCoverFactory testController;

	@Before
	public void setUp() throws Exception {
		testXmlCorpusFilename = "/test-corpus.xml";
		
		OpinionCorpusFactory factory = (OpinionCorpusFactory)new OpinionCorpusFactory()
			.setFile(new File(getClass().getResource(testXmlCorpusFilename).getPath()));
		testCorpus = factory.create();
		
		testTokenizingOptions = new TokenizingOptions()
			.setLemmatized(true)
			.setTags(PosTag.NOUN);
		
		testController = new SetCoverFactory();
	}

	@Test
	public void testSetCoverControllerWithExistingIdGetsExisting() {
		em.getTransaction().begin();
		em.persist(testCorpus);
		em.getTransaction().commit();
		em.refresh(testCorpus);

		DocumentSetCover setcover;
		try {
			setcover = testController
				.setStore(testCorpus)
				.setTokenizingOptions(testTokenizingOptions)
				.create();
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}

		em.getTransaction().begin();
		em.persist(setcover);
		em.getTransaction().commit();
		em.clear();
		
		DocumentSetCover actualSetCover;
		try {
			actualSetCover = testController
				.setStore(testCorpus)
				.setEm(em)
				.setExistingId(setcover.getId())
				.create();
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
		assertNotNull(actualSetCover);
		assertEquals(setcover.getIdentifier(), actualSetCover.getIdentifier());
		assertEquals(setcover.getBaseStore().getIdentifier(), actualSetCover.getBaseStore().getIdentifier());
	}
	
	@Test
	public void testCreateWithDifferentCoverageAdjusts() {
		em.getTransaction().begin();
		em.persist(testCorpus);
		em.getTransaction().commit();
		em.refresh(testCorpus);
		
		DocumentSetCover setcover;
		try {
			setcover = testController
				.setStore(testCorpus)
				.setTokenizingOptions(testTokenizingOptions)
				.create();
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
		assertEquals(3, Iterables.size(setcover.getDocuments()));
		
		em.getTransaction().begin();
		em.persist(setcover);
		em.getTransaction().commit();
		em.clear();
		
		DocumentSetCover actualSetCover;
		try {
			actualSetCover = testController
				.setStore(testCorpus)
				.setWeightCoverage(0.1)
				.setEm(em)
				.setExistingId(setcover.getId())
				.create();
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
		em.getTransaction().begin();
		em.merge(actualSetCover);
		em.getTransaction().commit();
		em.clear();
		
		assertNotNull(actualSetCover);
		assertEquals(1, Iterables.size(actualSetCover.getDocuments()));
		
		setcover = actualSetCover;
		actualSetCover = em.find(DocumentSetCover.class, setcover.getId());
		assertNotNull(actualSetCover);
		assertEquals(Iterables.size(setcover.getDocuments()), Iterables.size(actualSetCover.getDocuments()));
	}
}