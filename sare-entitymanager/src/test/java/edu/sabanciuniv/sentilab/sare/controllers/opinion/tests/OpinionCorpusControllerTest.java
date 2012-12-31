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

package edu.sabanciuniv.sentilab.sare.controllers.opinion.tests;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionCorpusFactory;
import edu.sabanciuniv.sentilab.sare.models.base.ModelTestsBase;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

public class OpinionCorpusControllerTest extends ModelTestsBase {

	private OpinionCorpus testCorpus;
	private OpinionCorpusFactory testFactory;
	private OpinionCorpusFactoryOptions testOptions;
	
	@Before
	public void setUp() throws Exception {
		testFactory = new OpinionCorpusFactory();
		testCorpus = new OpinionCorpus();
		testOptions = new OpinionCorpusFactoryOptions()
			.setExistingId(testCorpus.getId())
			.setEm(em);
	}
	
	@Test
	public void testCreateWithExistingIdGetsExistingObject() {
		em.getTransaction().begin();
		persist(testCorpus);
		em.getTransaction().commit();
		em.clear();
		
		OpinionCorpus actualCorpus = testFactory.create(testOptions);
		assertNotNull(actualCorpus);
		assertEquals(testCorpus.getIdentifier(), actualCorpus.getIdentifier());
	}
	
	@Test
	public void testCreateWithNonExistingIdCreatesNewObject() {
		testOptions
			.setExistingId(UUID.randomUUID())
			.setContent("some content")
			.setFormat("txt");
		OpinionCorpus actualCorpus = testFactory.create(testOptions);
		assertNotNull(actualCorpus);
	}
}