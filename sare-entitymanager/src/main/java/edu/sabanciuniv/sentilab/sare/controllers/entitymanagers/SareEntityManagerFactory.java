package edu.sabanciuniv.sentilab.sare.controllers.entitymanagers;

import javax.persistence.*;

/**
 * Provides static accessor for creating SARE entity managers.
 * @author Mus'ab Husaini
 */
public class SareEntityManagerFactory {

	private static EntityManagerFactory emFactory;
	
	static {
		emFactory = Persistence.createEntityManagerFactory("edu.sabanciuniv.sentilab.sare.data");
	}
	
	/**
	 * Creates and returns a new SARE entity manager.
	 * @return a new {@link EntityManager} instance for SARE entities.
	 */
	public static EntityManager createEntityManager() {
		return emFactory.createEntityManager();
	}
}