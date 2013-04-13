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

package models.documentStore;

import static controllers.base.SareTransactionalAction.*;

import java.util.*;

import javax.persistence.EntityManager;

import models.document.AspectOpinionMinedDocumentModel;

import org.apache.commons.lang3.*;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

public class AspectOpinionMinedCorpusModel
		extends PersistentDocumentStoreModel {

	public DocumentCorpusModel corpus;
	public AspectLexiconModel lexicon;
	public String engineCode;
	public Map<String, List<AspectOpinionMinedDocumentModel>> documents;
	
	public AspectOpinionMinedCorpusModel(AspectOpinionMinedCorpus minedCorpus) {
		super(minedCorpus);
		
		if (minedCorpus != null) {
			if (minedCorpus.getCorpus() != null) {
				this.corpus = (DocumentCorpusModel)createViewModel(minedCorpus.getCorpus());
			}
			if (minedCorpus.getLexicon() != null && minedCorpus.getLexicon() instanceof AspectLexicon) {
				this.lexicon = (AspectLexiconModel)createViewModel(minedCorpus.getLexicon());
			}
			
			this.engineCode = minedCorpus.getEngineCode();
		}
	}
	
	public AspectOpinionMinedCorpusModel() {
		this(null);
	}
	
	@Override
	public long populateSize(EntityManager em, PersistentDocumentStore store) {
		Validate.notNull(em);
		super.populateSize(em, store);
		
		if (store != null && store instanceof AspectOpinionMinedCorpus) {
			DocumentCorpus corpus = ((AspectOpinionMinedCorpus)store).getCorpus();
			this.corpus = new DocumentCorpusModel(corpus);
			this.corpus.populateSize(em, corpus);
		}
		
		return this.size;
	}

	public AspectOpinionMinedCorpusModel populateDocuments(AspectOpinionMinedCorpus corpus) {
		Validate.isTrue(hasEntityManager());
		this.populateSize(em(), corpus);
		
		this.documents = Maps.newLinkedHashMap();
		if (corpus != null) {
			for (AspectOpinionMinedDocument document : corpus.getDocuments(AspectOpinionMinedDocument.class)) {
				String orientation = null;
				double polarity = ObjectUtils.defaultIfNull(document.getPolarity(), 0.0);
				if (polarity < 0) {
					orientation = "Negative";
				} else if (polarity == 0) {
					orientation = "Neutral";
				} else {
					orientation = "Positive";
				}
				
				List<AspectOpinionMinedDocumentModel> docs = ObjectUtils.defaultIfNull(this.documents.get(orientation),
						Lists.<AspectOpinionMinedDocumentModel>newArrayList());
				docs.add(new AspectOpinionMinedDocumentModel(document));
				this.documents.put(orientation, docs);
			}
		}
		
		return this;
	}
}