package edu.sabanciuniv.sentilab.sare.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.tests.*;
import edu.sabanciuniv.sentilab.sare.models.base.tests.*;
import edu.sabanciuniv.sentilab.sare.models.opinion.tests.*;
import edu.sabanciuniv.sentilab.sare.models.setcover.tests.*;

@RunWith(Suite.class)
@SuiteClasses({
	PersistentObjectTest.class,
	OpinionDocumentTest.class,
	OpinionCorpusTest.class,
	SetCoverDocumentTest.class,
	DocumentSetCoverTest.class,
	PersistentDocumentControllerTest.class,
	PersistentDocumentStoreControllerTest.class
})
public class ModelsTests {

}
