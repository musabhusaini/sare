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

import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.ObjectUtils;

import models.document.TokenizingOptionsModel;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.DocumentSetCoverController;
import edu.sabanciuniv.sentilab.sare.controllers.setcover.SetCoverFactory;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;
import edu.sabanciuniv.sentilab.sare.models.setcover.*;

public class DocumentSetCoverModel
		extends DocumentCorpusModel {

	public DocumentCorpusModel baseCorpus;
	public TokenizingOptionsModel tokenizingOptions;
	public Double weightCoverage;
	public Double totalCoveredWeight;
	public Map<Integer, Double> coverageMatrix;
	
	public DocumentSetCoverModel(DocumentSetCover setCover) {
		super(setCover);

		this.tokenizingOptions = new TokenizingOptionsModel();
		
		if (setCover != null) {
			if (setCover.getBaseCorpus() != null) {
				this.baseCorpus = (DocumentCorpusModel)createViewModel(setCover.getBaseCorpus());
			}
			this.tokenizingOptions = new TokenizingOptionsModel(setCover.getTokenizingOptions());
			this.weightCoverage = Math.round(
				ObjectUtils.defaultIfNull(setCover.getWeightCoverage(), SetCoverFactory.DEFAULT_WEIGHT_COVERAGE) * 100) / 100.0;
			this.totalCoveredWeight = setCover.getTotalCoveredWeight();
		}
	}
	
	public DocumentSetCoverModel() {
		this(null);
	}
	
	@Override
	public long populateSize(EntityManager em, PersistentDocumentStore store) {
		if (store instanceof DocumentSetCover) {
			if (this.baseCorpus != null && ((DocumentSetCover)store).getBaseCorpus() != null) {
				this.baseCorpus.populateSize(em, ((DocumentSetCover)store).getBaseCorpus());
			}
			return this.size = new DocumentSetCoverController().getCoverSize(em, (DocumentSetCover)store);
		}
		
		return super.populateSize(em, store);
	}
	
	public SetCoverFactory toFactory() {
		SetCoverFactory factory = new SetCoverFactory();
		factory.setWeightCoverage(ObjectUtils.defaultIfNull(this.weightCoverage, 1.0));
		factory.setTitle(this.title);
		factory.setDescription(this.description);
		factory.setExistingId(this.id);
		
		if (this.tokenizingOptions != null) {
			factory.setTokenizingOptions(this.tokenizingOptions.toTokenizingOptions());
		}
		
		return factory;
	}
}