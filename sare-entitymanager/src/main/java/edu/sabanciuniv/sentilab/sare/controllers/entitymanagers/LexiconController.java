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

import org.apache.commons.lang3.Validate;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;
import edu.sabanciuniv.sentilab.utils.*;

/**
 * An entity controller for {@link AspectLexicon} entities.
 * @author Mus'ab Husaini
 */
public class LexiconController
	extends PersistentDocumentController {
	
	/**
	 * Gets all UUIDs for {@code T} type lexica owned by the given owner.
	 * @param em the {@link EntityManager} to use.
	 * @param ownerId the ID of the owner.
	 * @param entityClass the specific type of lexica to be retrieved; must be annotated with the {@link Entity} annotation.
	 * @return a {@link List} containing {@link String} representations of the UUIDs.
	 */
	public <T extends Lexicon> List<String> getAllLexica(EntityManager em, String ownerId, Class<T>	 entityClass) {
		Validate.notNull(em, CannedMessages.NULL_ARGUMENT, "em");
		Validate.notNull(ownerId, CannedMessages.NULL_ARGUMENT, "ownerId");
		Validate.notNull(entityClass, CannedMessages.NULL_ARGUMENT, "entityClass");
		
		Query query = em.createQuery("SELECT lex.id FROM Lexicon lex " +
			"WHERE lex.ownerId=:ownerId " +
			"AND TYPE(lex)=:type AND (lex.baseStore IS NULL " +
			"OR lex.baseStore IN (SELECT d FROM DocumentCorpus d))");
		query
			.setParameter("ownerId", ownerId)
			.setParameter("type", entityClass);
		
		@SuppressWarnings("unchecked")
		List<byte[]> results = (List<byte[]>)query.getResultList();
		return Lists.newArrayList(Iterables.transform(results, UuidUtils.uuidBytesToStringFunction()));
	}
	
	/**
	 * Gets all UUIDs for {@link Lexicon} objects owned by the given owner.
	 * @param em the {@link EntityManager} to use.
	 * @param ownerId the ID of the owner.
	 * @return a {@link List} containing {@link String} representations of the UUIDs.
	 */
	public List<String> getAllLexica(EntityManager em, String ownerId) {
		return this.getAllLexica(em, ownerId, Lexicon.class);
	}
}