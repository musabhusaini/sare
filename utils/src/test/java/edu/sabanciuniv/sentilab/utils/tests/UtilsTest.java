package edu.sabanciuniv.sentilab.utils.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.sabanciuniv.sentilab.utils.text.nlp.factory.tests.LinguisticProcessorFactoryTest;
import edu.sabanciuniv.sentilab.utils.text.nlp.stanford.tests.StanfordEnglishProcessorTest;

@RunWith(Suite.class)
@SuiteClasses({
	LinguisticProcessorFactoryTest.class,
	StanfordEnglishProcessorTest.class
})
public class UtilsTest {
}
