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

package edu.sabanciuniv.sentilab.sare.controllers.setcover;

import java.util.*;

import org.apache.commons.lang3.*;
import org.apache.commons.lang3.tuple.*;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.core.controllers.*;
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
	
	private Set<ProgressObserver> progressObservers;
	
	public SetCoverController() {
		this.progressObservers = Sets.newHashSet();
	}
	
	private DocumentSetCover createSpecific(DocumentSetCover setcover, PersistentDocumentStore store, TokenizingOptions tokenizingOptions, double weightCoverage) {
		Validate.notNull(store, CannedMessages.NULL_ARGUMENT, "store");
		
		if (tokenizingOptions == null) {
			tokenizingOptions = new TokenizingOptions();
		}
		
		double progress = 0.0;
		int storeSize = Iterables.size(store.getDocuments());
		
		// create a dummy set cover to keep the extra stuff in.
		DocumentSetCover dummySetCover = new DocumentSetCover(store);
		
		List<PersistentDocument> corpusDocuments = Lists.newArrayList(store.getDocuments());
		List<SetCoverDocument> setCoverDocuments = Lists.newArrayList(setcover.getAllDocuments());
		
		// for each store document.
		for (PersistentDocument document : corpusDocuments) {
			// create a copy of the current document as a set cover document.
			SetCoverDocument workingDocument = (SetCoverDocument)new SetCoverDocument(document)
				.setTokenizingOptions(tokenizingOptions)
				.setStore(dummySetCover);
			
			// loop through all set cover documents.
			for (int index=0; index<setCoverDocuments.size(); index++) {
				SetCoverDocument setCoverDocument = setCoverDocuments.get(index);
				
				// create a working reference to the set cover document.
				SetCoverDocument workingSCDocument = setCoverDocument;
				
				// get merge weights on both directions.
				double forwardMerge = workingSCDocument.getMergedWeight(workingDocument);
				double backwardMerge = workingDocument.getMergedWeight(workingSCDocument);
				
				// if we get more weight on the backward merge, swap the documents.
				if (forwardMerge < backwardMerge) {
					setCoverDocuments.add(setCoverDocuments.indexOf(workingSCDocument), workingDocument);
					setCoverDocuments.remove(workingSCDocument);
					
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
			
			setCoverDocuments.add(workingDocument);
			
			// if the document was completely consumed, then we mark it as uncovered.
			if (workingDocument.getTotalTokenWeight() == 0) {
				workingDocument.setCovered(false);
			}
		
			progress += 1.0 / storeSize;
			for (ProgressObserver progressObserver : this.progressObservers) {
				progressObserver.observe(progress, "create");
			}
		}
		
		setcover.setDocuments(setCoverDocuments);
		
		// get rid of the dummy.
		dummySetCover.setBaseStore(null);
		
		return this.adjustCoverage(setcover, weightCoverage)
			.setTokenizingOptions(tokenizingOptions);
	}
	
	private Pair<List<SetCoverDocument>, List<SetCoverDocument>> splitByCoverage(DocumentSetCover setcover, double weightCoverage) {
		List<SetCoverDocument> covered = Lists.newArrayList();
		List<SetCoverDocument> uncovered = Lists.newArrayList();
		List<SetCoverDocument> setCoverDocuments = Collections.synchronizedList(Lists.newArrayList(setcover.getAllDocuments()));
		
		double totalWeight=setcover.getTotalWeight();
		double accumulatedWeight=0;
		
		// sort set cover.
		Iterator<SetCoverDocument> iterator = Ordering.from(new Comparator<SetCoverDocument>() {
			@Override
			public int compare(SetCoverDocument o1, SetCoverDocument o2) {
				return (int)((o2.getWeight() - o1.getWeight()) * 100);
			}
		}).immutableSortedCopy(setCoverDocuments).iterator();
		
		// get all the useful ones.
		while (iterator.hasNext()) {
			if (accumulatedWeight >= weightCoverage * totalWeight) {
				break;
			}
			
			SetCoverDocument document = iterator.next();
			accumulatedWeight += document.getWeight();
			covered.add(document);
		}
		
		while (iterator.hasNext()) {
			uncovered.add(iterator.next());
		}
		
		return new ImmutablePair<List<SetCoverDocument>, List<SetCoverDocument>>(covered, uncovered);
	}
	
	@Override
	protected DocumentSetCover createPrivate(SetCoverFactoryOptions options, DocumentSetCover setcover)
		throws IllegalFactoryOptionsException {
		
		try {
			Validate.notNull(options.getStore(), CannedMessages.NULL_ARGUMENT, "options.store");
		} catch (NullPointerException e) {
			throw new IllegalFactoryOptionsException(e);
		}
		
		if (setcover == null) {
			// create a set cover based on this store.
			setcover = new DocumentSetCover(options.getStore());
		}
		
		if (ObjectUtils.equals(options.getTokenizingOptions(), setcover.getTokenizingOptions())) {
			if (ObjectUtils.equals(options.getWeightCoverage(), ObjectUtils.defaultIfNull(setcover.getWeightCoverage(), SetCoverFactoryOptions.DEFAULT_WEIGHT_COVERAGE))) {
				// in this case, nothing to do here.
				return setcover;
			} else {
				// in this case, we just need to adjust coverage.
				return this.adjustCoverage(setcover, options.getWeightCoverage());
			}
		}
		
		// clear set cover if it already exists, we'll start afresh.
		this.clear(setcover);
		this.createSpecific(setcover, options.getStore(), options.getTokenizingOptions(), options.getWeightCoverage());
		
		return setcover;
	}

	/**
	 * Adjusts the coverage of a set cover.
	 * @param setcover the {@link DocumentSetCover} to adjust.
	 * @param weightCoverage the desired minimum weight coverage.
	 * @return the reduced {@link DocumentSetCover} object.
	 */
	public DocumentSetCover adjustCoverage(DocumentSetCover setcover, double weightCoverage) {
		Validate.notNull(setcover, CannedMessages.NULL_ARGUMENT, "setcover");
		
		// a quick way of checking if the weight coverage is valid or not. the setter will throw an exception if not.
		new SetCoverFactoryOptions()
			.setWeightCoverage(weightCoverage);
		
		// apply the weight ratio.
		Pair<List<SetCoverDocument>, List<SetCoverDocument>> coverageSplit = this.splitByCoverage(setcover, weightCoverage);
		
		// mark covered and uncovered.
		for (SetCoverDocument document : coverageSplit.getLeft()) {
			document.setCovered(true);
		}
		
		for (SetCoverDocument document : coverageSplit.getRight()) {
			document.setCovered(false);
		}
		
		return setcover.setWeightCoverage(weightCoverage);
	}
	
	/**
	 * Calculates the coverage matrix for a given set cover.
	 * @param setcover the {@link DocumentSetCover} to find the matrix for.
	 * @param coverageGranularity the coverage granularity, which is the interval between two coverage points in the matrix.
	 * @return the coverage matrix as a {@link Map} of percentage coverage as keys and store size reduction ratio as values.
	 */
	public Map<Integer, Double> calculateCoverageMatrix(DocumentSetCover setcover, int coverageGranularity) {
		Validate.notNull(setcover, CannedMessages.NULL_ARGUMENT, "setcover");
		Validate.isTrue(coverageGranularity > 0 && coverageGranularity <= 100, "parameter 'coverageGranularity' must fall within (0, 100]");
		
		double totalSize = Iterables.size(setcover.getBaseStore() != null ?
			setcover.getBaseStore().getDocuments() : setcover.getAllDocuments());
		Map<Integer, Double> matrix = Maps.newHashMap();
		for (int coverage=100; coverage>=0; coverage-=coverageGranularity) {
			List<SetCoverDocument> covered = this.splitByCoverage(setcover, coverage/100.0).getLeft();
			matrix.put(coverage, covered.size()/totalSize);
		}
		
		return matrix;
	}
	
	/**
	 * Calculates the coverage matrix for a given set cover.
	 * @param setcover the {@link DocumentSetCover} to find the matrix for.
	 * @return the coverage matrix as a {@link Map} of percentage coverage as keys and store size reduction ratio as values.
	 */
	public Map<Integer, Double> calculateCoverageMatrix(DocumentSetCover setcover) {
		return this.calculateCoverageMatrix(setcover, 5);
	}
	
	/**
	 * Clears the provided set cover and brings it back to empty. Connection with the base store is not severed.
	 * @param setcover the {@link DocumentSetCover} object representing the set cover to clear.
	 * @return the {@code this} object.
	 */
	public SetCoverController clear(DocumentSetCover setcover) {
		Validate.notNull(setcover, CannedMessages.NULL_ARGUMENT, "setcover");
		
		setcover.setWeightCoverage(null)
			.setTokenizingOptions(null)
			.setDocuments(null);
		return this;
	}

	@Override
	public ProgressObservable addProgessObserver(ProgressObserver observer) {
		this.progressObservers.add(observer);
		return this;
	}

	@Override
	public boolean removeProgressObserver(ProgressObserver observer) {
		return this.progressObservers.remove(observer);
	}
}