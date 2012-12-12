package edu.sabanciuniv.sentilab.sare.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.sabanciuniv.sentilab.sare.controllers.opinion.tests.*;
import edu.sabanciuniv.sentilab.sare.models.base.document.tests.*;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.tests.*;
import edu.sabanciuniv.sentilab.sare.models.base.tests.*;

@RunWith(Suite.class)
@SuiteClasses({
	UniquelyIdentifiableObjectTest.class,
	PersistentObjectTest.class,
	PersistentDocumentTest.class,
	TokenizedDocumentTest.class,
	GenericDocumentTest.class,
	PersistentDocumentStoreTest.class,
	OpinionDocumentFactoryTest.class,
	OpinionCorpusFactoryTest.class
})
public class SareBaseTests {
}
