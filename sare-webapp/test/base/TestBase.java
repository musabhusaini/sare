package base;

import org.junit.*;

import play.test.*;

public class TestBase {
	public static FakeApplication app;
	 
	  @Before
	  public void startTest() {
	    app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
	    Helpers.start(app);
	  }
	 
	  @After
	  public void stopTest() {
	    Helpers.stop(app);
	  }
}