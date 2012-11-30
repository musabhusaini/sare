package edu.sabanciuniv.sentilab.sare.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.sabanciuniv.sentilab.sare.controllers.setcover.tests.SetCoverControllerTests;
import edu.sabanciuniv.sentilab.sare.models.base.document.tests.MergableDocumentTests;

@RunWith(Suite.class)
@SuiteClasses({
	MergableDocumentTests.class,
	SetCoverControllerTests.class
})
public class SareLexTests {
}
