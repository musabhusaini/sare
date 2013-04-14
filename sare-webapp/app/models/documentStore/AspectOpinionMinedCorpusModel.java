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
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;

import models.document.AspectOpinionMinedDocumentModel;

import org.apache.commons.lang3.*;

import scala.util.*;

import com.google.common.base.Function;
import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

public class AspectOpinionMinedCorpusModel
		extends PersistentDocumentStoreModel {
	
	public static final String NEGATIVE = "Negative";
	public static final String NEUTRAL = "Neutral";
	public static final String POSITIVE = "Positive";

	public static enum DocumentGrouping {
		orientation,
		aspect
	}
	
	public static class GroupedDocuments {
		public Object key;
		public Either<List<GroupedDocuments>, List<AspectOpinionMinedDocumentModel>> value;
		
		public long size() {
			long size = 0;
			if (this.value == null) {
				size = 0;
			} else if (this.value.isRight()) {
				size = this.value.right().get().size();
			} else {
				for (GroupedDocuments gd : this.value.left().get()) {
					size += gd.size();
				}
			}
			
			return size;
		}
		
		public Map<Object, Long> getSummary() {
			Map<Object, Long> summary = Maps.newLinkedHashMap();
			if (this.value != null && this.value.isLeft()) {
				for (GroupedDocuments gd : this.value.left().get()) {
					summary.put(gd.key, gd.size());
				}
			}
			return summary;
		}
	}
	
	public DocumentCorpusModel corpus;
	public AspectLexiconModel lexicon;
	public String engineCode;
	public GroupedDocuments documents;
	
	public AspectOpinionMinedCorpusModel(AspectOpinionMinedCorpus minedCorpus) {
		super(minedCorpus);
		
		if (minedCorpus != null) {
			if (minedCorpus.getCorpus() != null) {
				this.corpus = (DocumentCorpusModel)createViewModel(minedCorpus.getCorpus());
				this.id = this.corpus.id;
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
			
			if (corpus != null) {
				this.corpus = new DocumentCorpusModel(corpus);
				this.corpus.populateSize(em, corpus);
			}
		}
		
		return this.size;
	}

	private Map<Object, Collection<AspectOpinionMinedDocumentModel>> groupDocuments(Iterable<AspectOpinionMinedDocumentModel> documents, DocumentGrouping grouping) {
		Multimap<Object, AspectOpinionMinedDocumentModel> indexedDocuments = null;
		
		switch(grouping) {
		case orientation:
			indexedDocuments = Multimaps.index(documents, new Function<AspectOpinionMinedDocumentModel, Object>() {
				@Override
				@Nullable
				public Object apply(@Nullable AspectOpinionMinedDocumentModel document) {
					double polarity = ObjectUtils.defaultIfNull(document.polarity, 0.0);
					if (polarity < 0) {
						return NEGATIVE;
					} else if (polarity == 0) {
						return NEUTRAL;
					}
					return POSITIVE;
				}
			});
			
			break;
		case aspect:
			indexedDocuments = ArrayListMultimap.create();
			for (AspectOpinionMinedDocumentModel document : documents) {
				if (document.aspectPolarities == null || document.aspectPolarities.size() == 0) {
					indexedDocuments.put(null, document);
				} else {
					for (AspectLexiconModel aspect : document.aspectPolarities.keySet()) {
						indexedDocuments.put(aspect, document);
					}
				}
			}
			
			break;
		}
		
		return indexedDocuments.asMap();
	}
	
	private GroupedDocuments groupDocuments(Iterable<AspectOpinionMinedDocumentModel> documents, List<DocumentGrouping> grouping) {
		GroupedDocuments groupedDocuments = new GroupedDocuments();
		if (grouping == null || grouping.size() == 0) {
			groupedDocuments.value = new Right<List<GroupedDocuments>, List<AspectOpinionMinedDocumentModel>>((List<AspectOpinionMinedDocumentModel>)Lists.newArrayList(documents));
		} else {
			DocumentGrouping topGrouping = grouping.get(0);
			List<DocumentGrouping> remainingGrouping = Lists.newArrayList(grouping);
			remainingGrouping.remove(0);
			Map<Object, Collection<AspectOpinionMinedDocumentModel>> subGroupsMap = groupDocuments(documents, topGrouping);
			List<GroupedDocuments> subGroups = Lists.newArrayList();
			for (Entry<Object, Collection<AspectOpinionMinedDocumentModel>> groupEntry : subGroupsMap.entrySet()) {
				GroupedDocuments subGroup = groupDocuments(groupEntry.getValue(), remainingGrouping);
				subGroup.key = ObjectUtils.defaultIfNull(groupEntry.getKey(), "None");
				subGroups.add(subGroup);
			}
			groupedDocuments.value = new Left<List<GroupedDocuments>, List<AspectOpinionMinedDocumentModel>>(subGroups);
		}
		
		return groupedDocuments;
	}
	
	public AspectOpinionMinedCorpusModel populateDocuments(AspectOpinionMinedCorpus corpus, List<DocumentGrouping> grouping) {
		Validate.isTrue(hasEntityManager());
		this.populateSize(em(), corpus);
		
		if (corpus != null) {
			Iterable<AspectOpinionMinedDocumentModel> models = Iterables.transform(
				corpus.getDocuments(AspectOpinionMinedDocument.class),
				new Function<AspectOpinionMinedDocument, AspectOpinionMinedDocumentModel>() {
					@Override
					@Nullable
					public AspectOpinionMinedDocumentModel apply(@Nullable AspectOpinionMinedDocument document) {
						return new AspectOpinionMinedDocumentModel(document);
					}
				}
			);
			
			this.documents = groupDocuments(models, grouping);
			this.documents.key = corpus.getTitle();
		}
		return this;
	}
	
	public AspectOpinionMinedCorpusModel populateDocuments(AspectOpinionMinedCorpus corpus) {
		return populateDocuments(corpus, Lists.newArrayList(DocumentGrouping.orientation, DocumentGrouping.aspect));
	}
}