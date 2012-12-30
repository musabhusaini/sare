/*
 * Sentilab SARE: a Sentiment Analysis Research Environment
 * Copyright (C) 2013 Sabanci University Sentilab
 * http://sentilab.sabanciuniv.edu
 * 
 * This file is part of SARE.
 * 
 * SARE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * SARE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

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