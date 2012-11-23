package edu.sabanciuniv.sentilab.utils.text.nlp.stanford.tests;

import java.util.*;

import edu.sabanciuniv.sentilab.utils.text.nlp.base.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.factory.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.stanford.english.StanfordEnglishProcessor;

import static org.junit.Assert.*;

import org.junit.*;

import com.google.common.base.Joiner;
import com.google.common.collect.*;

public class StanfordEnglishProcessorTests {

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