package edu.sabanciuniv.sentilab.sare.controllers.entitymanagers;

import java.util.Map;

import javax.persistence.*;

import com.google.common.collect.Maps;

/**
 * Provides static accessor for creating SARE entity managers.
 * @author Mus'ab Husaini
 */
public class SareEntityManagerFactory {

	private static Map<DataMode, EntityManagerFactory> emFactories;

	/**
	 * Represents a data mode that can be used to retrieve entity data.
	 * @author Mus'ab Husaini
	 */
	public static enum DataMode {
		PROD,
		DEV,
		TEST
	}
	
	static {
		emFactories = Maps.newEnumMap(DataMode.class);
	}
	
	/**
	 * Creates and returns a new SARE entity manager.
	 * @param mode the {@link DataMode} value indicating the type of data to use.
	 * @return a new {@link EntityManager} instance for SARE entities.
	 */
	public static EntityManager createEntityManager(DataMode mode) {
		EntityManagerFactory factory = emFactories.get(mode);
		if (factory == null) {
			factory = Persistence.createEntityManagerFactory("edu.sabanciuniv.sentilab.sare.data." +
				mode.toString().toLowerCase());
			emFactories.put(mode, factory);
		}
		
		return factory.createEntityManager();
	}
	
	/**
	 * Creates and returns a new SARE entity manager.
	 * @param mode the type of data to use.
	 * @return a new {@link EntityManager} instance for SARE entities.
	 */
	public static EntityManager createEntityManager(String mode) {
		DataMode enumMode = DataMode.valueOf(mode.toUpperCase());
		if (enumMode == null) {
			throw new IllegalArgumentException(mode + " is not a valid mode value");
		}
		
		return createEntityManager(enumMode);
	}
	
	/**
	 * Creates and returns a new SARE entity manager.
	 * @return a new {@link EntityManager} instance for SARE entities.
	 */
	public static EntityManager createEntityManager() {
		return createEntityManager(DataMode.DEV);
	}
}