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

package edu.sabanciuniv.sentilab.sare.models.base;

import java.util.*;

import javax.persistence.*;

import org.junit.*;

import com.google.common.collect.Lists;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.SareEntityManagerFactory;
import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.SareEntityManagerFactory.DataMode;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

public class ModelTestsBase {

	protected EntityManager em;
	protected List<UUID> persistedUuids;
	
	@Before
	public void superSetUp() throws Exception {
		em = SareEntityManagerFactory.createEntityManager(DataMode.TEST);
		persistedUuids = Lists.newArrayList();
	}
	
	@After
	public void superTearDown() throws Exception {
		if (em.getTransaction().isActive()) {
			em.getTransaction().rollback();
		}
		
		em.getTransaction().begin();
		
		for (UUID uuid : persistedUuids) {
			PersistentObject obj;
			
			try {
				obj = em.find(PersistentObject.class, UuidUtils.toBytes(uuid));
			} catch (EntityNotFoundException e) {
				continue;
			}
			
			if (obj != null) {
				em.remove(obj);
			}
		}
		
		em.getTransaction().commit();
		em.close();
	}
	
	public void persist(PersistentObject obj) {
		persistedUuids.add(obj.getIdentifier());
		em.persist(obj);
	}
	
	public void remove(PersistentObject obj) {
		persistedUuids.remove(obj.getIdentifier());
		em.remove(obj);
	}
}