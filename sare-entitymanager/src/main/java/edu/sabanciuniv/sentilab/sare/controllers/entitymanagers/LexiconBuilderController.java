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

import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang3.Validate;

import com.google.common.base.Predicate;
import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;
import edu.sabanciuniv.sentilab.utils.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticToken;

/**
 * A controller for {@link LexiconBuilderDocumentStore} operations.
 * @author Mus'ab Husaini
 */
public class LexiconBuilderController
	extends PersistentDocumentStoreController {
	
	/**
	 * Finds the builder associated with a given corpus and lexicon.
	 * @param em the {@link EntityManager} to use.
	 * @param corpus the {@link DocumentCorpus} being used to build the lexicon.
	 * @param lexicon the {@link Lexicon} being built.
	 * @return the {@link LexiconBuilderDocumentStore} object found, if any; {@code null} otherwise.
	 */
	public LexiconBuilderDocumentStore findBuilder(EntityManager em, DocumentCorpus corpus, Lexicon lexicon) {
		Validate.notNull(em, CannedMessages.NULL_ARGUMENT, "em");
		Validate.notNull(corpus, CannedMessages.NULL_ARGUMENT, "corpus");
		Validate.notNull(lexicon, CannedMessages.NULL_ARGUMENT, "lexicon");
		
		Query query = em.createQuery("SELECT b FROM LexiconBuilderDocumentStore b " +
			"WHERE b.baseStore=:corpus AND :lexicon MEMBER OF b.referencedObjects");
		query
			.setMaxResults(1)
			.setParameter("corpus", corpus)
			.setParameter("lexicon", lexicon);
		
		return query.getResultList().size() > 0 ? (LexiconBuilderDocumentStore)query.getSingleResult() : null;
	}
	
	/**
	 * Gets all the documents being used to build the lexicon.
	 * @param em the {@link EntityManager} to use.
	 * @param builder the {@link LexiconBuilderDocumentStore} to use.
	 * @param seen whether to show only seen or unseen documents; {@code null} means no filtering.
	 * @return the {@link List} of {@link LexiconBuilderDocument} objects.
	 */
	@SuppressWarnings("unchecked")
	public List<LexiconBuilderDocument> getDocuments(EntityManager em, LexiconBuilderDocumentStore builder, Boolean seen) {
		Validate.notNull(em, CannedMessages.NULL_ARGUMENT, "em");
		Validate.notNull(builder, CannedMessages.NULL_ARGUMENT, "builder");
		
		String qs = "SELECT doc FROM LexiconBuilderDocument doc WHERE doc.store=:builder";
		if (seen != null) {
			qs += " AND doc.isSeen=:seen";
		}
		Query query = em.createQuery(qs);
		query.setParameter("builder", builder);
		if (seen != null) {
			query.setParameter("seen", seen);
		}
		
		return (List<LexiconBuilderDocument>)query.getResultList();
	}
	
	/**
	 * Gets all the documents being used to build the lexicon.
	 * @param em the {@link EntityManager} to use.
	 * @param builder the {@link LexiconBuilderDocumentStore} to use.
	 * @return the {@link List} of {@link LexiconBuilderDocument} objects.
	 */
	public List<LexiconBuilderDocument> getDocuments(EntityManager em, LexiconBuilderDocumentStore builder) {
		return this.getDocuments(em, builder, null);
	}

	/**
	 * Gets the previously seen tokens for the given {@link LexiconBuilderDocumentStore}.
	 * @param em the {@link EntityManager} to use.
	 * @param builder the identifier of the {@link LexiconBuilderDocumentStore} to use.
	 * @return the {@link List} of {@link LexiconDocument} objects.
	 */
	@SuppressWarnings("unchecked")
	public List<LexiconDocument> getSeenTokens(EntityManager em, LexiconBuilderDocumentStore builder) {
		Validate.notNull(em, CannedMessages.NULL_ARGUMENT, "em");
		Validate.notNull(builder, CannedMessages.NULL_ARGUMENT, "builder");
		
		Query query = em.createQuery("SELECT doc FROM LexiconDocument doc WHERE doc.store.id=:builder");
		query.setParameter("builder", builder);
		
		return (List<LexiconDocument>)query.getResultList();
	}
	
	/**
	 * Gets a value indicating whether the provided token has been previously seen or not.
	 * @param em the {@link EntityManager} to use.
	 * @param builder the {@link LexiconBuilderDocumentStore} to use.
	 * @param token the token to look for.
	 * @return {@code true} if the token was seen; {@code false} otherwise.
	 */
	public boolean isSeenToken(EntityManager em, LexiconBuilderDocumentStore builder, String token) {
		Validate.notNull(em, CannedMessages.NULL_ARGUMENT, "em");
		Validate.notNull(builder, CannedMessages.NULL_ARGUMENT, "builder");
		
		Query query = em.createQuery("SELECT doc FROM LexiconDocument doc " +
			"WHERE doc.store=:builder AND doc.content=:token");
		query
			.setMaxResults(1)
			.setParameter("builder", builder)
			.setParameter("token", token);
		
		return query.getSingleResult() != null;
	}
	
	/**
	 * Gets the unseen document with the next highest weight.
	 * @param em the {@link EntityManager} to use.
	 * @param builder the {@link LexiconBuilderDocumentStore} to use.
	 * @return the {@link LexiconBuilderDocument} with the highest weight among unseen documents.
	 */
	public LexiconBuilderDocument getNextDocument(EntityManager em, LexiconBuilderDocumentStore builder) {
		Validate.notNull(em, CannedMessages.NULL_ARGUMENT, "em");
		Validate.notNull(builder, CannedMessages.NULL_ARGUMENT, "builder");
		
		return Ordering.from(new Comparator<LexiconBuilderDocument>() {
			@Override
			public int compare(LexiconBuilderDocument o1, LexiconBuilderDocument o2) {
				double w1 = 0;
				double w2 = 0;
				
				if (o1.getBaseDocument() instanceof IWeightedDocument) {
					w1 = ((IWeightedDocument)o1.getBaseDocument()).getWeight();
				} else if (o1.getBaseDocument() instanceof FullTextDocument) {
					w1 = ((FullTextDocument)o1.getBaseDocument()).getTotalTokenWeight();
				} else {
					w1 = o1.getTotalTokenWeight();
				}
				
				if (o2.getBaseDocument() instanceof IWeightedDocument) {
					w2 = ((IWeightedDocument)o2.getBaseDocument()).getWeight();
				} else if (o2.getBaseDocument() instanceof FullTextDocument) {
					w2 = ((FullTextDocument)o2.getBaseDocument()).getTotalTokenWeight();
				} else {
					w2 = o2.getTotalTokenWeight();
				}
				
				return (int)Math.round(w1 - w2);
			}
		}).max(this.getDocuments(em, builder, false));
	}
	
	/**
	 * Sets the provided document and all tokens contained therein as having been seen.
	 * @param em the {@link EntityManager} to use.
	 * @param document the {@link LexiconBuilderDocument} object to mark as seen.
	 * @return the supplied {@link LexiconBuilderDocument}.
	 */
	public LexiconBuilderDocument setSeenDocument(EntityManager em, LexiconBuilderDocument document) {
		Validate.notNull(em, CannedMessages.NULL_ARGUMENT, "em");
		Validate.notNull(document, CannedMessages.NULL_ARGUMENT, "document");
		Validate.notNull(document.getStore(), CannedMessages.NULL_ARGUMENT, "document.store");
		Validate.notNull(document.getBaseDocument(), CannedMessages.NULL_ARGUMENT, "document.baseDocument");
		
		document.setSeen(true);
		
		if (document.getBaseDocument() instanceof FullTextDocument && document.getStore() instanceof LexiconBuilderDocumentStore) {
			List<LexiconDocument> seenTokens = this.getSeenTokens(em, (LexiconBuilderDocumentStore)document.getStore());
			FullTextDocument ftDoc = (FullTextDocument)document.getBaseDocument();
			Set<LinguisticToken> tokens = ftDoc.getTokenWeightMap().keySet();
			for (final LinguisticToken token : tokens) {
				LexiconDocument seenToken = Iterables.find(seenTokens, new Predicate<LexiconDocument>() {
					@Override
					public boolean apply(LexiconDocument seenToken) {
						return seenToken.getContent().equalsIgnoreCase(token.getWord());
					}
				});
				
				if (seenToken == null) {
					seenToken = (LexiconDocument)new LexiconDocument()
						.setContent(token.getWord())
						.setStore(document.getStore());
					
					em.persist(seenToken);
				}
			}
		}
		
		if (em.contains(document)) {
			em.refresh(document);
		} else {
			em.persist(document);
		}
		
		return document;
	}
}