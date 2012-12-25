package edu.sabanciuniv.sentilab.sare.models.base;

import java.util.*;

import javax.persistence.*;

import org.junit.*;

import com.google.common.collect.Lists;

import edu.sabanciuniv.sentilab.utils.UuidUtils;

public class ModelTestsBase {

	protected EntityManager em;
	protected static EntityManagerFactory emFactory;
	protected List<UUID> persistedUuids;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		emFactory = Persistence.createEntityManagerFactory("edu.sabanciuniv.sentilab.sare.tests.data");
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		emFactory.close();
	}
	
	@Before
	public void superSetUp() throws Exception {
		em = emFactory.createEntityManager();
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