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

package edu.sabanciuniv.sentilab.sare.models.base.documentStore.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

public class LexiconBuilderDocumentStoreTest {

	OpinionCorpus testCorpus;
	Lexicon testLexicon;
	LexiconBuilderDocumentStore testBuilder;
	
	@Before
	public void setUp() throws Exception {
		testCorpus = (OpinionCorpus)new OpinionCorpus()
			.addDocument(new OpinionDocument().setContent("this is the first test."))
			.addDocument(new OpinionDocument().setContent("this is the second test."));
		testLexicon = new Lexicon() {
			private static final long serialVersionUID = 1L;
		};
		
		testBuilder = new LexiconBuilderDocumentStore(testCorpus, testLexicon) {
			private static final long serialVersionUID = 1L;
		};
	}

	@Test
	public void testConstructorWithCorpusSetsBase() {
		assertEquals(testCorpus, testBuilder.getBaseStore());
	}
	
	@Test
	public void testConstructorWithCorpusAddsDocuments() {
		List<PersistentDocument> docs = Lists.newArrayList(testCorpus.getDocuments());
		assertEquals(docs.size(), Iterables.size(testBuilder.getDocuments()));
		
		for (PersistentDocument doc : testBuilder.getDocuments()) {
			assertTrue(doc instanceof LexiconBuilderDocument);
			assertTrue(docs.contains(doc.getBaseDocument()));
			docs.remove(doc.getBaseDocument());
		}
	}
	
	@Test
	public void testConstructorWithLexiconAddsReference() {
		assertTrue(Iterables.contains(testBuilder.getBaseStores(Lexicon.class), testLexicon));
	}
}