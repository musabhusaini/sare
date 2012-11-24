package edu.sabanciuniv.sentilab.sare.models.base;

import javax.persistence.*;

import org.junit.*;

public class ModelTestsBase {

	protected EntityManager em;
	protected static EntityManagerFactory emFactory;

	@BeforeClass
	public static void setUpBeforeClass()
		throws Exception {
		
		emFactory = Persistence.createEntityManagerFactory("edu.sabanciuniv.sentilab.sare.tests.data");
	}
	
	@AfterClass
	public static void tearDownAfterClass()
		throws Exception {
		
		emFactory.close();
	}
}
