package edu.sabanciuniv.sentilab.sare.controllers.entitymanagers;

import java.util.*;

import javax.persistence.*;
import javax.persistence.criteria.*;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.controllers.base.ControllerBase;
import edu.sabanciuniv.sentilab.sare.models.base.UniquelyIdentifiableObject;
import edu.sabanciuniv.sentilab.sare.models.base.document.PersistentDocument;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

public class PersistentDocumentController
	extends ControllerBase {

	public List<String> getAllUuids(EntityManager em, String storeId) {
		Validate.notNull(em, CannedMessages.NULL_ARGUMENT, "em");
		Validate.notNull(storeId, CannedMessages.NULL_ARGUMENT, "storeId");
		
		byte[] uuidBytes = UniquelyIdentifiableObject.getUuidBytes(storeId);
		PersistentDocumentStore store = em.find(PersistentDocumentStore.class, uuidBytes);
		
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<byte[]> cq = cb.createQuery(byte[].class);
		Root<PersistentDocument> doc = cq.from(PersistentDocument.class);
		cq.multiselect(doc.get("id")).where(cb.equal(doc.get("store"), cb.parameter(PersistentDocumentStore.class, "store")));
		TypedQuery<byte[]> tq = em.createQuery(cq);
		tq.setParameter("store", store);
		return Lists.newArrayList(Iterables.transform(tq.getResultList(), UniquelyIdentifiableObject.uuidBytesToStringFunction()));
	}
}