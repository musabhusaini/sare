package edu.sabanciuniv.sentilab.sare.controllers.entitymanagers;

import java.util.List;

import javax.persistence.*;
import javax.persistence.criteria.*;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.controllers.base.ControllerBase;
import edu.sabanciuniv.sentilab.sare.models.base.UniquelyIdentifiableObject;
import edu.sabanciuniv.sentilab.sare.models.base.document.PersistentDocument;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * An entity controller for {@link PersistentDocumentStore} entities.
 * @author Mus'ab Husaini
 */
public class PersistentDocumentStoreController
	extends ControllerBase {
	
	/**
	 * Gets all UUIDs for {@link PersistentDocumentStore} objects owned by the given owner.
	 * @param em the {@link EntityManager} to use.
	 * @param ownerId the ID of the owner.
	 * @return a {@link List} containing {@link String} representations of the UUIDs.
	 */
	public List<String> getAllUuids(EntityManager em, String ownerId) {
		Validate.notNull(em, CannedMessages.NULL_ARGUMENT, "em");
		Validate.notNull(ownerId, CannedMessages.NULL_ARGUMENT, "ownerId");
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<byte[]> cq = cb.createQuery(byte[].class);
		Root<PersistentDocumentStore> store = cq.from(PersistentDocumentStore.class);
		cq.multiselect(store.get("id")).where(cb.equal(store.get("ownerId"), cb.parameter(String.class, "ownerId")));
		TypedQuery<byte[]> tq = em.createQuery(cq);
		tq.setParameter("ownerId", ownerId);
		return Lists.newArrayList(Iterables.transform(tq.getResultList(), UniquelyIdentifiableObject.uuidBytesToStringFunction()));
	}
	
	/**
	 * Gets the size of a document store (number of documents contained therein).
	 * @param em the {@link EntityManager} to use.
	 * @param storeId the ID of the store to look for.
	 * @return the {@link Long} count of documents.
	 */
	public long getSize(EntityManager em, String storeId) {
		Validate.notNull(em, CannedMessages.NULL_ARGUMENT, "em");
		Validate.notNull(storeId, CannedMessages.NULL_ARGUMENT, "id");
		
		byte[] uuidBytes = UniquelyIdentifiableObject.getUuidBytes(storeId);
		PersistentDocumentStore store = em.find(PersistentDocumentStore.class, uuidBytes);
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<PersistentDocument> doc = cq.from(PersistentDocument.class);
		cq.select(cb.count(doc)).where(cb.equal(doc.get("store"), cb.parameter(PersistentDocumentStore.class, "store")));
		TypedQuery<Long> tq = em.createQuery(cq);
		tq.setParameter("store", store);
		return tq.getSingleResult();
	}
}