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

package edu.sabanciuniv.sentilab.utils.text.nlp.stanford.tests;

import java.util.*;

import edu.sabanciuniv.sentilab.utils.text.nlp.base.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.factory.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.stanford.english.StanfordEnglishProcessor;

import static org.junit.Assert.*;

import org.junit.*;

import com.google.common.base.Joiner;
import com.google.common.collect.*;

public class StanfordEnglishProcessorTest {

	private StanfordEnglishProcessor processor;
	private List<List<String>> testSentenceTokens;
	private List<String> testTokens;
	private List<String> testSentences;
	private String testString;
	
	@Before
	public void setUp() throws Exception {
		this.processor = new StanfordEnglishProcessor();
		
		this.testSentenceTokens = Lists.newArrayList();
		this.testSentences = Lists.newArrayList();
		
		List<String> tokens = Arrays.asList(new String[] {"The", "quick", "brown", "fox", "jumps", "over", "the", "lazy", "dog", "."});
		this.testSentenceTokens.add(tokens);
		this.testSentences.add(Joiner.on(" ").join(Iterables.limit(tokens, tokens.size() - 1)) + Iterables.getLast(tokens));
		
		tokens = Arrays.asList(new String[] {"Why", "would", "the", "lazy", "dog", "take", "this", "humiliation", "?"});
		this.testSentenceTokens.add(tokens);
		this.testSentences.add(Joiner.on(" ").join(Iterables.limit(tokens, tokens.size() - 1)) + Iterables.getLast(tokens));
		
		this.testTokens = Lists.newArrayList(Iterables.concat(this.testSentenceTokens));
		this.testString = Joiner.on(" ").join(this.testSentences);
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testFactoryCreate() {
		LinguisticProcessorFactoryOptions factoryOptions = new LinguisticProcessorFactoryOptions().setLanguage("en");
		ILinguisticProcessor processor = new LinguisticProcessorFactory().create(factoryOptions);
		assertNotNull(processor);
		assertEquals(processor.getClass(), StanfordEnglishProcessor.class);
	}
	
	public void testDecompose(LinguisticText lingText) {
		assertNotNull(lingText);
		
		Iterable<LinguisticSentence> sentences = lingText.getSentences();
		assertEquals(Iterables.size(lingText.getSentences()), this.testSentences.size());
		
		Iterable<LinguisticToken> tokens = lingText.getTokens();
		assertEquals(Iterables.size(tokens), this.testTokens.size());
		
		for (int tokenIndex=0; tokenIndex<this.testTokens.size(); tokenIndex++) {
			assertEquals(this.testTokens.get(tokenIndex), Iterables.get(tokens, tokenIndex).getText());
		}
		
		for (int sentenceIndex=0; sentenceIndex<this.testSentences.size(); sentenceIndex++) {
			LinguisticSentence sentence = Iterables.get(sentences, sentenceIndex);
			assertNotNull(sentence);
			
			Iterable<LinguisticToken> sentenceTokens = sentence.getTokens();
			assertEquals(Iterables.size(sentenceTokens), this.testSentenceTokens.get(sentenceIndex).size());
			
			for (int tokenIndex=0; tokenIndex<this.testSentenceTokens.get(sentenceIndex).size(); tokenIndex++) {
				assertEquals(this.testSentenceTokens.get(sentenceIndex).get(tokenIndex), Iterables.get(sentenceTokens, tokenIndex).getText());
			}
		}
	}

	@Test
	public void testDecompose() {
		testDecompose(this.processor.decompose(this.testString));
	}
	
	@Test
	public void testTag() {
		LinguisticText lingText = this.processor.tag(this.testString);
		testDecompose(lingText);
	}
	
	@Test
	public void testParse() {
		LinguisticText lingText = this.processor.parse(this.testString);
		testDecompose(lingText);
	}
}