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

package edu.sabanciuniv.sentilab.sare.controllers.setcover;

import java.util.*;

import org.apache.commons.lang3.*;
import org.apache.commons.lang3.tuple.*;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.core.controllers.ProgressObservable;
import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.base.documentStore.*;
import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;
import edu.sabanciuniv.sentilab.sare.models.setcover.*;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * A class that can work on {@link SetCoverDocument} objects.
 * @author Mus'ab Husaini
 */
public class SetCoverController
	extends PersistentDocumentStoreFactory<DocumentSetCover, SetCoverFactoryOptions>
	implements IDocumentStoreController, ProgressObservable {

	private double progress;
	
	private DocumentSetCover create(PersistentDocumentStore store, TokenizingOptions tokenizingOptions, double weightCoverage) {
		Validate.notNull(store, CannedMessages.NULL_ARGUMENT, "store");
		
		if (tokenizingOptions == null) {
			tokenizingOptions = new TokenizingOptions();
		}
		
		// create a set cover based on this store.
		DocumentSetCover setCover = new DocumentSetCover(store);
		
		// create a dummy set cover to keep the refuse in.
		DocumentSetCover dummySetCover = new DocumentSetCover(store);
		
		// for each store document.
		for (PersistentDocument document : Iterables.filter(store.getDocuments(), PersistentDocument.class)) {
			// create a copy of the current document as a set cover document.
			SetCoverDocument workingDocument = (SetCoverDocument)new SetCoverDocument(document)
				.setTokenizingOptions(tokenizingOptions)
				.setStore(dummySetCover);
			
			// loop through all set cover documents.
			Iterable<SetCoverDocument> setCoverDocuments = setCover.wrapGeneric(SetCoverDocument.class).getDocuments();
			for (int scIndex=0; scIndex<Iterables.size(setCoverDocuments); scIndex++) {
				SetCoverDocument setCoverDocument = Iterables.get(setCoverDocuments, scIndex);
				
				// create a working reference to the set cover document.
				SetCoverDocument workingSCDocument = setCoverDocument;
				
				// get merge weights on both directions.
				double forwardMerge = workingSCDocument.getMergedWeight(workingDocument);
				double backwardMerge = workingDocument.getMergedWeight(workingSCDocument);
				
				// if we get more weight on the backward merge, swap the documents.
				if (forwardMerge < backwardMerge) {
					setCover.replaceDocuments(workingSCDocument, workingDocument);
					
					SetCoverDocument tmpSCDocument = workingDocument;
					workingDocument = workingSCDocument;
					workingSCDocument = tmpSCDocument;
				}
				
				// perform the merge.
				workingSCDocument.merge(workingDocument);
				
				// if the entire document has been consumed, then we are done with it.
				if (workingDocument.getTotalTokenWeight() == 0) {
					break;
				}
			}
			
			// if the document was not completely consumed, we create another entry for it.
			if (workingDocument.getTotalTokenWeight() > 0) {
				workingDocument.setStore(setCover);
			} else {
				workingDocument.setStore(null);
			}
		}
		
		// get rid of the dummy.
		dummySetCover.setBaseStore(null);
		
		return this.reduceCoverage(setCover, weightCoverage);
	}
	
	private Pair<List<SetCoverDocument>, List<SetCoverDocument>> splitByCoverage(DocumentSetCover setCover, double weightCoverage) {
		List<SetCoverDocument> covered = Lists.newArrayList();
		List<SetCoverDocument> discarded = Lists.newArrayList();
		
		double totalWeight=setCover.totalWeight();
		double accumulatedWeight=0;
		
		// sort set cover.
		List<SetCoverDocument> setCoverDocuments = Lists.newArrayList(setCover.wrapGeneric(SetCoverDocument.class).getDocuments());
		Collections.sort(setCoverDocuments, new Comparator<SetCoverDocument>() {
			@Override
			public int compare(SetCoverDocument o1, SetCoverDocument o2) {
				return (int)(o2.getWeight() - o1.getWeight()) * 100;
			}
		});
		
		// get all the useful ones.
		Iterator<SetCoverDocument> iterator = setCoverDocuments.iterator();
		while (iterator.hasNext()) {
			if (accumulatedWeight >= weightCoverage * totalWeight) {
				break;
			}
			
			SetCoverDocument document = iterator.next();
			accumulatedWeight += document.getTotalTokenWeight();
			covered.add(document);
		}
		
		while (iterator.hasNext()) {
			discarded.add(iterator.next());
		}
		
		return new ImmutablePair<List<SetCoverDocument>, List<SetCoverDocument>>(covered, discarded);
	}
	
	@Override
	protected DocumentSetCover createPrivate(SetCoverFactoryOptions options)
		throws IllegalFactoryOptionsException {
		
		try {
			Validate.notNull(options, CannedMessages.NULL_ARGUMENT, "options");
			Validate.notNull(options.getStore(), CannedMessages.NULL_ARGUMENT, "options.store");
		} catch (NullPointerException e) {
			throw new IllegalFactoryOptionsException(e);
		}
		
		this.progress = 0.0;
		DocumentSetCover setCover = this.create(options.getStore(), options.getTokenizingOptions(), options.getWeightCoverage());
		
		if (StringUtils.isNotEmpty(options.getTitle())) {
			setCover.setTitle(options.getTitle());
		}
		
		if (StringUtils.isNotEmpty(options.getDescription())) {
			setCover.setDescription(options.getDescription());
		}
		
		if (StringUtils.isNotEmpty(options.getLanguage())) {
			setCover.setLanguage(options.getLanguage());
		}
		
		this.progress = 1.0;
		return setCover;
	}

	/**
	 * Reduces the coverage of a set cover.
	 * @param setCover the {@link DocumentSetCover} to reduce.
	 * @param weightCoverage the desired minimum weight coverage.
	 * @return the reduced {@link DocumentSetCover} object.
	 */
	public DocumentSetCover reduceCoverage(DocumentSetCover setCover, double weightCoverage) {
		Validate.notNull(setCover, CannedMessages.NULL_ARGUMENT, "setCover");
		
		// a quick way of checking if the weight coverage is valid or not. the setter will throw an exception if not.
		new SetCoverFactoryOptions()
			.setWeightCoverage(weightCoverage);
		
		// apply the weight ratio, if any.
		if (weightCoverage < 1.0) {
			List<SetCoverDocument> discarded = this.splitByCoverage(setCover, weightCoverage).getRight();
			
			// eliminate the extras.
			for (SetCoverDocument document : discarded) {
				setCover.removeDocument(document);
			}
		}
		
		return setCover;
	}
	
	/**
	 * Calculates the coverage matrix for a given set cover.
	 * @param setCover the {@link DocumentSetCover} to find the matrix for.
	 * @param coverageGranularity the coverage granularity, which is the interval between two coverage points in the matrix.
	 * @return the coverage matrix as a {@link Map} of percentage coverage as keys and store size reduction ratio as values.
	 */
	public Map<Integer, Double> calculateCoverageMatrix(DocumentSetCover setCover, int coverageGranularity) {
		Validate.notNull(setCover, CannedMessages.NULL_ARGUMENT, "setCover");
		Validate.isTrue(coverageGranularity > 0 && coverageGranularity <= 100, "parameter 'coverageGranularity' must fall within (0, 100]");
		
		double totalSize = Iterables.size(setCover.getBaseStore() != null ?
			setCover.getBaseStore().getDocuments() : setCover.getDocuments());
		Map<Integer, Double> matrix = Maps.newHashMap();
		for (int coverage=100; coverage>=0; coverage-=coverageGranularity) {
			List<SetCoverDocument> covered = this.splitByCoverage(setCover, coverage/100.0).getLeft();
			matrix.put(coverage, covered.size()/totalSize);
		}
		
		return matrix;
	}
	
	/**
	 * Calculates the coverage matrix for a given set cover.
	 * @param setCover the {@link DocumentSetCover} to find the matrix for.
	 * @return the coverage matrix as a {@link Map} of percentage coverage as keys and store size reduction ratio as values.
	 */
	public Map<Integer, Double> calculateCoverageMatrix(DocumentSetCover setCover) {
		return this.calculateCoverageMatrix(setCover, 5);
	}
	
	@Override
	public double getProgress() {
		return this.progress;
	}
}