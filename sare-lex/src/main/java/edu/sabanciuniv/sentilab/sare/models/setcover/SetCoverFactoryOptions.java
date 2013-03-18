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

package edu.sabanciuniv.sentilab.sare.models.setcover;

import org.apache.commons.lang3.Validate;

import edu.sabanciuniv.sentilab.sare.models.base.document.TokenizingOptions;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;

/**
 * The default set of options that can be used to construct an {@link DocumentSetCover} object.
 * The most specific combination of properties will be used.
 * @author Mus'ab Husaini
 *
 */
public class SetCoverFactoryOptions
	extends PersistentDocumentStoreFactoryOptions<DocumentSetCover> {

	private PersistentDocumentStore store;
	private TokenizingOptions tokenizingOptions;
	private double weightCoverage;
	
	public SetCoverFactoryOptions() {
		this.weightCoverage = 1.0;
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
	public SetCoverFactoryOptions setStore(PersistentDocumentStore store) {
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
	public SetCoverFactoryOptions setTokenizingOptions(TokenizingOptions tokenizingOptions) {
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
	public SetCoverFactoryOptions setWeightCoverage(double weightCoverage) {
		Validate.isTrue(weightCoverage >= 0.0 && weightCoverage <= 1.0, "weight coverage must be in the interval [0.0, 1.0].");
		
		this.weightCoverage = weightCoverage;
		return this;
	}
}