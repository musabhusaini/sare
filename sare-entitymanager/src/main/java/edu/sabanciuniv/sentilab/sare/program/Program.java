package edu.sabanciuniv.sentilab.sare.program;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Program {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		EntityManagerFactory factory = Persistence.createEntityManagerFactory("edu.sabanciuniv.sentilab.sare.data");
		EntityManager em = factory.createEntityManager();
		em.close();
		
		System.out.println();
	}
}