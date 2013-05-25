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

package edu.sabanciuniv.sentilab.sare.controllers.opinion.tests;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.lang3.*;
import org.junit.*;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionCorpusFactory;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

public class OpinionCorpusFactoryTest {
	
	private String testXmlCorpusFilename;
	private OpinionCorpus expectedXmlCorpus;
	
	private String testZipCorpusFilename;
	private OpinionCorpus expectedZipCorpus;
	
	private String testTextCorpusFilename;
	private OpinionCorpus expectedTextCorpus;
	
	private String testContent;
	private OpinionCorpus expectedContentCorpus;
	
	private String testNoPolarityContent;
	private OpinionCorpus expectedNoPolarityCorpus;
	
	@Before
	public void setUp() throws Exception {
		testXmlCorpusFilename = "/test-corpus.xml";
		
		expectedXmlCorpus = (OpinionCorpus)new OpinionCorpus()
			.setDocuments(Lists.newArrayList(new OpinionDocument(), new OpinionDocument()))
			.setTitle("test-xml-corpus")
			.setDescription("test")
			.setLanguage("en");
		
		testZipCorpusFilename = "/test-corpus.zip";
		expectedZipCorpus = (OpinionCorpus)new OpinionCorpus()
			.setDocuments(Lists.newArrayList(new OpinionDocument(), new OpinionDocument(), new OpinionDocument(), new OpinionDocument(), new OpinionDocument()))
			.setTitle("test-zip-corpus")
			.setDescription("test")
			.setLanguage("en");
		
		testTextCorpusFilename = "/test-corpus.txt";
		expectedTextCorpus = (OpinionCorpus)new OpinionCorpus()
			.setDocuments(Lists.newArrayList(
				new OpinionDocument().setPolarity(0.7).setContent("nice hotel"),
				new OpinionDocument().setPolarity(-0.8).setContent("terrible, hotel")));
		
		String content = "this is a great test document";
		double polarity = 0.9;
		
		testContent = String.format("%s|%f", content, polarity);
		expectedContentCorpus = (OpinionCorpus)new OpinionCorpus()
			.addDocument((OpinionDocument)new OpinionDocument().setPolarity(polarity).setContent(content));
		
		testNoPolarityContent = content;
		expectedNoPolarityCorpus = (OpinionCorpus)new OpinionCorpus()
			.addDocument((OpinionDocument)new OpinionDocument().setContent(content));
	}

	@Test
	public void testCreateFromXmlFile() {
		OpinionCorpus actualCorpus = null;
		try {
			actualCorpus = new OpinionCorpusFactory()
				.setFile(new File(getClass().getResource(testXmlCorpusFilename).getPath()))
				.create();
		} catch (IllegalFactoryOptionsException e) {
			fail("could not open file");
		}
		
		assertNotNull(actualCorpus);
		assertEquals(expectedXmlCorpus.getTitle(), actualCorpus.getTitle());
		assertEquals(expectedXmlCorpus.getDescription(), actualCorpus.getDescription());
		assertEquals(expectedXmlCorpus.getLanguage(), actualCorpus.getLanguage());
		assertEquals(Iterables.size(expectedXmlCorpus.getDocuments()), Iterables.size(actualCorpus.getDocuments()));
	}
	
	@Test
	public void testCreateFromZipFile() {
		OpinionCorpus actualCorpus = null;
		try {
			actualCorpus = new OpinionCorpusFactory()
				.setFile(new File(getClass().getResource(testZipCorpusFilename).getPath()))
				.create();
		} catch (IllegalFactoryOptionsException e) {
			fail("error reading input file");
		}
		
		assertNotNull(actualCorpus);
		assertEquals(expectedZipCorpus.getTitle(), actualCorpus.getTitle());
		assertEquals(expectedZipCorpus.getDescription(), actualCorpus.getDescription());
		assertEquals(expectedZipCorpus.getLanguage(), actualCorpus.getLanguage());
		assertEquals(Iterables.size(expectedZipCorpus.getDocuments()), Iterables.size(actualCorpus.getDocuments()));
	}
	
	@Test
	public void testCreateFromTextFile() {
		OpinionCorpus actualCorpus = null;
		try {
			actualCorpus = new OpinionCorpusFactory()
				.setFile(new File(getClass().getResource(testTextCorpusFilename).getPath()))
				.setTextDelimiter(",")
				.create();
		} catch (IllegalFactoryOptionsException e) {
			fail("error reading input file");
		}
		
		assertNotNull(actualCorpus);
		assertEquals(Iterables.size(expectedTextCorpus.getDocuments()), Iterables.size(actualCorpus.getDocuments()));
		
		for (OpinionDocument expectedDocument : expectedTextCorpus.getDocuments(OpinionDocument.class)) {
			boolean found = false;
			for (OpinionDocument actualDocument : actualCorpus.getDocuments(OpinionDocument.class)) {
				if (StringUtils.equals(expectedDocument.getContent(), actualDocument.getContent())
					&& ObjectUtils.equals(expectedDocument.getPolarity(), actualDocument.getPolarity())) {
					found = true;
					break;
				}
			}
			
			assertTrue(found);
		}
	}
	
	@Test
	public void testCreateFromContent() {
		OpinionCorpus actualCorpus = null;
		try {
			actualCorpus = new OpinionCorpusFactory()
				.setContent(testContent)
				.setFormat("txt")
				.setTextDelimiter("|")
				.create();
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create");
		}
		
		assertNotNull(actualCorpus);
		assertEquals(Iterables.size(expectedContentCorpus.getDocuments()), Iterables.size(actualCorpus.getDocuments()));
		
		OpinionDocument expectedDocument = Iterables.getFirst(expectedContentCorpus.getDocuments(OpinionDocument.class),
			null);
		OpinionDocument actualDocument = Iterables.getFirst(actualCorpus.getDocuments(OpinionDocument.class),
			null);
		assertNotNull(actualDocument);
		
		assertEquals(expectedDocument.getContent(), actualDocument.getContent());
		assertEquals(expectedDocument.getPolarity(), actualDocument.getPolarity(), 0.005);
	}
	
	@Test
	public void testCreateFromNoPolarityContent() {
		OpinionCorpus actualCorpus = null;
		try {
			actualCorpus = new OpinionCorpusFactory()
				.setContent(testNoPolarityContent)
				.setFormat("txt")
				.setTextDelimiter("|")
				.create();
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create");
		}
		
		assertNotNull(actualCorpus);
		assertEquals(Iterables.size(expectedNoPolarityCorpus.getDocuments()), Iterables.size(actualCorpus.getDocuments()));
		
		OpinionDocument expectedDocument = Iterables.getFirst(expectedNoPolarityCorpus.getDocuments(OpinionDocument.class),
			null);
		OpinionDocument actualDocument = Iterables.getFirst(actualCorpus.getDocuments(OpinionDocument.class),
			null);
		assertNotNull(actualDocument);
		
		assertEquals(expectedDocument.getContent(), actualDocument.getContent());
		assertNull(actualDocument.getPolarity());
	}
	
	@Test
	public void testCreateFromNothing() {
		try {
			assertNotNull(new OpinionCorpusFactory().create());
		} catch (IllegalFactoryOptionsException e) {
			fail("could not create from nothing");
		}
	}
}