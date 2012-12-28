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