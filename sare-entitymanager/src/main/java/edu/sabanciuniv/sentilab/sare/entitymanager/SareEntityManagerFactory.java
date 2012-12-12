package edu.sabanciuniv.sentilab.sare.entitymanager;

import javax.persistence.*;

public class SareEntityManagerFactory {

	private static EntityManagerFactory emFactory;
	
	static {
		emFactory = Persistence.createEntityManagerFactory("edu.sabanciuniv.sentilab.sare.data");
	}
	
	public static EntityManager createEntityManager() {
		return emFactory.createEntityManager();
	}
}