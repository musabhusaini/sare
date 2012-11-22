package edu.sabanciuniv.sentilab.sare.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.sabanciuniv.sentilab.sare.controllers.documentStore.tests.SetCoverControllerTests;
import edu.sabanciuniv.sentilab.sare.models.document.base.tests.MergableDocumentTests;

@RunWith(Suite.class)
@SuiteClasses({
	MergableDocumentTests.class,
	SetCoverControllerTests.class
})
public class SareLexTests {
}
