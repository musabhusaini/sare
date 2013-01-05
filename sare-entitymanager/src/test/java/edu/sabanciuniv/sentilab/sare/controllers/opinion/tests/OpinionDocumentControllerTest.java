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

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionDocumentFactory;
import edu.sabanciuniv.sentilab.sare.models.base.ModelTestsBase;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

public class OpinionDocumentControllerTest extends ModelTestsBase {

	private OpinionCorpus testCorpus;
	private OpinionDocument testDocument;
	private OpinionDocumentFactory testFactory;
	private OpinionDocumentFactoryOptions testOptions;
	
	@Before
	public void setUp() throws Exception {
		testCorpus = new OpinionCorpus();
		testFactory = new OpinionDocumentFactory();
		testOptions = new OpinionDocumentFactoryOptions()
			.setPolarity(0.8)
			.setContent("some content")
			.setCorpus(testCorpus);
		
		testDocument = testFactory.create(testOptions);
	}

	@Test
	public void testFactoryCreatedDataIsPersisted() {
		em.getTransaction().begin();
		persist(testCorpus);
		em.getTransaction().commit();
		em.clear();
		
		OpinionDocument actualDocument = em.find(OpinionDocument.class, testDocument.getId());
		assertNotNull(actualDocument);
		assertEquals(testDocument.getPolarity(), actualDocument.getPolarity(), 0);
		assertEquals(testDocument.getContent(), actualDocument.getContent());
	}
}