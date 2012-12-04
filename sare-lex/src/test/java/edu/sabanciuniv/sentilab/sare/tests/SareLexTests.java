package edu.sabanciuniv.sentilab.sare.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import edu.sabanciuniv.sentilab.sare.controllers.setcover.tests.SetCoverControllerTest;
import edu.sabanciuniv.sentilab.sare.models.base.document.tests.MergableDocumentTest;

@RunWith(Suite.class)
@SuiteClasses({
	MergableDocumentTest.class,
	SetCoverControllerTest.class
})
public class SareLexTests {
}
