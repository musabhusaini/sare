package edu.sabanciuniv.sentilab.utils.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.sabanciuniv.sentilab.utils.text.nlp.factory.tests.LinguisticProcessorFactoryTests;
import edu.sabanciuniv.sentilab.utils.text.nlp.stanford.tests.StanfordEnglishProcessorTests;

@RunWith(Suite.class)
@SuiteClasses({
	LinguisticProcessorFactoryTests.class,
	StanfordEnglishProcessorTests.class
})
public class UtilsTests {
}
