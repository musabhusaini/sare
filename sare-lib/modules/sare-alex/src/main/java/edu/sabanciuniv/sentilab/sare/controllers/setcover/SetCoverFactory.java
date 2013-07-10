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
public class SetCoverFactory
		extends PersistentDocumentStoreFactory<DocumentSetCover>
		implements DocumentStoreController, ProgressObservablePrimitive {

	public static final double DEFAULT_WEIGHT_COVERAGE = 1.0;

	private PersistentDocumentStore store;
	private TokenizingOptions tokenizingOptions;
	private double weightCoverage;
	
	private Set<ProgressObserver> progressObservers;
	
	/**
	 * Creates an instance of the {@link SetCoverFactory}.
	 */
	public SetCoverFactory() {
		this.weightCoverage = DEFAULT_WEIGHT_COVERAGE;
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
			this.notifyProgress(progress, "create");
		}
		
		setcover.setDocuments(setCoverDocuments);
		
		// get rid of the dummy.
		dummySetCover.setBaseStore(null);
		
		return setcover.adjustCoverage(weightCoverage)
			.setTokenizingOptions(tokenizingOptions);
	}
	
	@Override
	protected DocumentSetCover createPrivate(DocumentSetCover setcover)
		throws IllegalFactoryOptionsException {
		
		try {
			Validate.notNull(this.getStore(), CannedMessages.NULL_ARGUMENT, "this.store");
		} catch (NullPointerException e) {
			throw new IllegalFactoryOptionsException(e);
		}
		
		if (setcover == null) {
			// create a set cover based on this store.
			setcover = new DocumentSetCover(this.getStore());
		}
		
		if (ObjectUtils.equals(this.getTokenizingOptions(), setcover.getTokenizingOptions())) {
			if (ObjectUtils.equals(this.getWeightCoverage(), ObjectUtils.defaultIfNull(setcover.getWeightCoverage(), DEFAULT_WEIGHT_COVERAGE))) {
				// in this case, nothing to do here.
				return setcover;
			} else {
				// in this case, we just need to adjust coverage.
				return setcover.adjustCoverage(this.getWeightCoverage());
			}
		}
		
		// clear set cover if it already exists, we'll start afresh.
		setcover.clear();
		this.createSpecific(setcover, this.getStore(), this.getTokenizingOptions(), this.getWeightCoverage());
		
		return setcover;
	}

	/**
	 * Gets the store from which the set cover will be created.
	 * @return the {@link PersistentDocumentStore} object from which the set cover will be created.
	 */
	public PersistentDocumentStore getStore() {
		return this.store;
	}
	
	/**
	 * Sets the store from which the set cover is to be created.
	 * @param store the {@link PersistentDocumentStore} object from which the set cover is to be created.
	 * @return the {@code this} object.
	 */
	public SetCoverFactory setStore(PersistentDocumentStore store) {
		this.store = store;
		return this;
	}
	
	/**
	 * Gets the tokenizing options that will be used to tokenize the content of the documents.
	 * @return the {@link TokenizingOptions} object containing the tokenizing options.
	 */
	public TokenizingOptions getTokenizingOptions() {
		return this.tokenizingOptions;
	}
	
	/**
	 * Sets the tokenizing options to be used for tokenizing the content of the documents.
	 * @param tokenizingOptions the {@link TokenizingOptions} object containing the tokenizing options.
	 * @return the {@code this} object.
	 */
	public SetCoverFactory setTokenizingOptions(TokenizingOptions tokenizingOptions) {
		this.tokenizingOptions = tokenizingOptions;
		return this;
	}

	/**
	 * Gets the token weight coverage to be maintained in the final set cover.
	 * @return the token weight coverage; must be in [0.0, 1.0].
	 */
	public double getWeightCoverage() {
		return this.weightCoverage;
	}

	/**
	 * Sets the desired token weight coverage for the set cover.
	 * @param weightCoverage the desired token weight coverage; must be in [0.0, 1.0].
	 * @return the {@code this} object.
	 */
	public SetCoverFactory setWeightCoverage(double weightCoverage) {
		Validate.isTrue(weightCoverage >= 0.0 && weightCoverage <= 1.0, "weight coverage must be in the interval [0.0, 1.0].");

		this.weightCoverage = weightCoverage;
		return this;
	}
	
	@Override
	public ProgressObservablePrimitive addProgessObserver(ProgressObserver observer) {
		this.progressObservers.add(observer);
		return this;
	}

	@Override
	public boolean removeProgressObserver(ProgressObserver observer) {
		return this.progressObservers.remove(observer);
	}

	@Override
	public void notifyProgress(double progress, String message) {
		for (ProgressObserver progressObserver : this.progressObservers) {
			progressObserver.observe(progress, message);
		}
	}
}