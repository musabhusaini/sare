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

package edu.sabanciuniv.sentilab.sare.models.base.document.tests;

import static org.junit.Assert.*;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.models.opinion.OpinionDocument;

public class OpinionDocumentTest {

	OpinionDocument testDocument;
	
	@Before
	public void setUp() throws Exception {
		testDocument = new OpinionDocument();
	}

	@Test
	public void testSetGetPolarity() {
		Double testPolarity = 0.98;
		testDocument.setPolarity(testPolarity);
		assertEquals(testPolarity, testDocument.getPolarity(), 0.0);
	}
	
	@Test
	public void testSetGetPolarityNaN() {
		testDocument.setPolarity(Double.NaN);
		assertNull(testDocument.getPolarity());
	}
	
	@Test
	public void testSetGetPolarityNull() {
		testDocument.setPolarity(null);
		assertNull(testDocument.getPolarity());
	}
}