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

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionCorpusFactory;
import edu.sabanciuniv.sentilab.sare.controllers.setcover.SetCoverController;
import edu.sabanciuniv.sentilab.sare.models.base.document.TokenizingOptions;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.sare.models.setcover.*;
import edu.sabanciuniv.sentilab.sare.tests.PersistenceTestsBase;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.PosTag;

public class SetCoverControllerTest
	extends PersistenceTestsBase {

	private String testXmlCorpusFilename;
	private OpinionCorpus testCorpus;
	private TokenizingOptions testTokenizingOptions;
	private SetCoverController testController;

	@Before
	public void setUp() throws Exception {
		testXmlCorpusFilename = "/test-corpus.xml";
		
		OpinionCorpusFactory factory = new OpinionCorpusFactory();
		testCorpus = factory.create((OpinionCorpusFactoryOptions)new OpinionCorpusFactoryOptions()
			.setFile(new File(getClass().getResource(testXmlCorpusFilename).getPath())));
		
		testTokenizingOptions = new TokenizingOptions()
			.setLemmatized(true)
			.setTags(PosTag.NOUN);
		
		testController = new SetCoverController();
	}

	@Test
	public void testSetCoverControllerWithExistingIdGetsExisting() {
		DocumentSetCover setCover;
		try {
			setCover = testController.create(new SetCoverFactoryOptions()
				.setStore(testCorpus).setTokenizingOptions(testTokenizingOptions));
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
		em.getTransaction().begin();
		persist(testCorpus);
		persist(setCover);
		em.getTransaction().commit();
		em.clear();
		
		DocumentSetCover actualSetCover;
		try {
			actualSetCover = testController.create((SetCoverFactoryOptions)new SetCoverFactoryOptions()
				.setStore(testCorpus)
				.setTokenizingOptions(testTokenizingOptions)
				.setEm(em)
				.setExistingId(setCover.getId()));
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create set cover");
			return;
		}
		
		assertNotNull(actualSetCover);
		assertEquals(setCover.getIdentifier(), actualSetCover.getIdentifier());
		assertEquals(setCover.getBaseStore().getIdentifier(), actualSetCover.getBaseStore().getIdentifier());
	}
}