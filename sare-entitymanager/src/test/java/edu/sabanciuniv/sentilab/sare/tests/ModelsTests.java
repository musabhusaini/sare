package edu.sabanciuniv.sentilab.sare.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.sabanciuniv.sentilab.sare.models.opinion.tests.*;
import edu.sabanciuniv.sentilab.sare.models.setcover.tests.*;

@RunWith(Suite.class)
@SuiteClasses({
	OpinionDocumentTests.class,
	OpinionCorpusTests.class,
	SetCoverDocumentTests.class,
	DocumentSetCoverTests.class
})
public class ModelsTests {

}
