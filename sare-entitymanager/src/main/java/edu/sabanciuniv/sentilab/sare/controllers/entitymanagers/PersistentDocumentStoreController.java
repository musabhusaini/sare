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

import java.util.List;

import javax.persistence.*;
import javax.persistence.criteria.*;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.controllers.base.ControllerBase;
import edu.sabanciuniv.sentilab.sare.models.base.document.PersistentDocument;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;
import edu.sabanciuniv.sentilab.utils.*;

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
		return Lists.newArrayList(Iterables.transform(tq.getResultList(), UuidUtils.uuidBytesToStringFunction()));
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
		
		byte[] uuidBytes = UuidUtils.toBytes(storeId);
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