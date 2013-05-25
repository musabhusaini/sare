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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sabanciuniv.sentilab.sare.controllers.entitymanagers;

import java.util.List;

import javax.persistence.*;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.base.documentStore.DocumentCorpus;
import edu.sabanciuniv.sentilab.sare.models.setcover.DocumentSetCover;
import edu.sabanciuniv.sentilab.utils.*;

/**
 * An entity controller for {@link DocumentSetCover} entities.
 * TODO: write tests for this class.
 * @author Mus'ab Husaini
 */
public class DocumentSetCoverController
	extends PersistentDocumentStoreController {

	/**
	 * Gets all relevant set covers.
	 * @param em the {@link EntityManager} to use.
	 * @param ownerId the ID of the owner.
	 * @param baseCorpus the {@link DocumentCorpus} which is the base corpus for the set cover.
	 * @param entityClass the specific type of set covers to be retrieved; must be annotated with the {@link Entity} annotation.
	 * @return a {@link List} containing {@link String} representations of the UUIDs.
	 */
	public <T extends DocumentSetCover> List<String> getAllSetCovers(EntityManager em, String ownerId, DocumentCorpus baseCorpus, Class<T> entityClass) {
		Validate.notNull(em, CannedMessages.NULL_ARGUMENT, "em");
		Validate.notNull(ownerId, CannedMessages.NULL_ARGUMENT, "ownerId");
		Validate.notNull(baseCorpus, CannedMessages.NULL_ARGUMENT, "baseCorpus");
		Validate.notNull(entityClass, CannedMessages.NULL_ARGUMENT, "entityClass");
		
		TypedQuery<byte[]> query = em.createQuery("SELECT sc.id FROM DocumentSetCover sc " +
			"WHERE sc.ownerId=:ownerId " +
			"AND TYPE(sc)=:type " +
			"AND sc.baseStore=:baseCorpus", byte[].class);
		query
			.setParameter("ownerId", ownerId)
			.setParameter("type", entityClass)
			.setParameter("baseCorpus", baseCorpus);
		
		List<byte[]> results = query.getResultList();
		return Lists.newArrayList(Iterables.transform(results, UuidUtils.uuidBytesToStringFunction()));
	}

	/**
	 * Gets all relevant set covers.
	 * @param em the {@link EntityManager} to use.
	 * @param ownerId the ID of the owner.
	 * @param baseCorpus the {@link DocumentCorpus} which is the base corpus for the set cover.
	 * @return a {@link List} containing {@link String} representations of the UUIDs.
	 */
	public List<String> getAllSetCovers(EntityManager em, String ownerId, DocumentCorpus baseCorpus) {
		return this.getAllSetCovers(em, ownerId, baseCorpus, DocumentSetCover.class);
	}
	
	/**
	 * Gets the size of a set cover (number of covered documents).
	 * @param em the {@link EntityManager} to use.
	 * @param setCover the {@link DocumentSetCover} whose size is required.
	 * @return the {@link Long} count of documents.
	 */
	public long getCoverSize(EntityManager em, DocumentSetCover setCover) {
		Validate.notNull(em, CannedMessages.NULL_ARGUMENT, "em");
		Validate.notNull(setCover, CannedMessages.NULL_ARGUMENT, "setCover");
		
		TypedQuery<byte[]> query = em.createQuery("SELECT scd.id FROM SetCoverDocument scd " +
			"WHERE scd.store=:sc " +
			"AND scd.flag=true", byte[].class);
		query.setParameter("sc", setCover);
		
		return query.getResultList().size();
	}
}