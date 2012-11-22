package edu.sabanciuniv.sentilab.sare.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.sabanciuniv.sentilab.sare.controllers.document.tests.OpinionDocumentFactoryTests;
import edu.sabanciuniv.sentilab.sare.controllers.documentStore.tests.OpinionCorpusFactoryTests;
import edu.sabanciuniv.sentilab.sare.models.document.base.tests.TokenizedDocumentTests;

@RunWith(Suite.class)
@SuiteClasses({
	TokenizedDocumentTests.class,
	OpinionDocumentFactoryTests.class,
	OpinionCorpusFactoryTests.class
})
public class SareBaseTests {
}
