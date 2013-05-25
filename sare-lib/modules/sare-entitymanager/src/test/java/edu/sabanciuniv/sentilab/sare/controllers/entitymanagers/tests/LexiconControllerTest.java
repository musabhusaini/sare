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

package edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.tests;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.LexiconController;
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.sabanciuniv.sentilab.sare.tests.PersistenceTestsBase;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

public class LexiconControllerTest
	extends PersistenceTestsBase {

	private String testOwnerId;
	private LexiconController testController;
	private AspectLexicon testLexicon;
	
	@Before
	public void setUp() throws Exception {
		testController = new LexiconController();
		
		testOwnerId = UUID.randomUUID().toString();
		testLexicon = (AspectLexicon)new AspectLexicon()
			.setTitle("test aspect")
			.setOwnerId(testOwnerId);
		testLexicon.addAspect("test aspect 1");
		testLexicon.addAspect("test aspect 2");
		
		em.getTransaction().begin();
		persist(testLexicon);
		em.getTransaction().commit();
		em.clear();
	}

	@Test
	public void testGetAllLexicaGetTopLevelLexica() {
		List<String> uuids = testController.getAllLexica(em, testOwnerId, AspectLexicon.class);
		
		assertNotNull(uuids);
		assertEquals(1, uuids.size());
		assertEquals(UuidUtils.normalize(testLexicon.getIdentifier()), UuidUtils.normalize(uuids.get(0)));
	}
}