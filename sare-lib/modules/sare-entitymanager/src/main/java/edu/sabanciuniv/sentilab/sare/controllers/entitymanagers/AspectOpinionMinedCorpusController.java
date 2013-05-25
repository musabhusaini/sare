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

import javax.persistence.*;

import org.apache.commons.lang3.*;

import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.DocumentCorpus;
import edu.sabanciuniv.sentilab.sare.models.opinion.AspectOpinionMinedCorpus;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A controller for {@link AspectOpinionMinedCorpus} operations.
 * @author Mus'ab Husaini
 */
public class AspectOpinionMinedCorpusController
		extends PersistentDocumentStoreController {

	/**
	 * Finds the opinion mined corpus associated with a given corpus, lexicon, and engine code.
	 * @param em the {@link EntityManager} to use.
	 * @param corpus the {@link DocumentCorpus} being used to mine opinion.
	 * @param lexicon the {@link AspectLexicon} being built.
	 * @return the {@link AspectOpinionMinedCorpus} object found, if any; {@code null} otherwise.
	 */
	public AspectOpinionMinedCorpus findMinedCorpus(EntityManager em, DocumentCorpus corpus, AspectLexicon lexicon, String engineCode) {
		Validate.notNull(em, CannedMessages.NULL_ARGUMENT, "em");
		Validate.notNull(corpus, CannedMessages.NULL_ARGUMENT, "corpus");
		Validate.notNull(lexicon, CannedMessages.NULL_ARGUMENT, "lexicon");
		
		TypedQuery<AspectOpinionMinedCorpus> query = em.createQuery("SELECT mc FROM AspectOpinionMinedCorpus mc " +
			"WHERE mc.baseStore=:corpus AND :lexicon MEMBER OF mc.referencedObjects", AspectOpinionMinedCorpus.class);
		query
			.setParameter("corpus", corpus)
			.setParameter("lexicon", lexicon);
		
		for (AspectOpinionMinedCorpus minedCorpus : query.getResultList()) {
			if (StringUtils.equals(engineCode, minedCorpus.getEngineCode())) {
				return minedCorpus;
			}
		}
		
		return null;
	}
}