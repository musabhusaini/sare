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

import javax.persistence.EntityManager;

import org.apache.commons.lang3.ObjectUtils;

import models.TokenizingOptionsModel;

import com.google.common.collect.Iterables;

import controllers.base.SareTransactionalAction;
import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.DocumentSetCoverController;
import edu.sabanciuniv.sentilab.sare.models.setcover.*;

public class DocumentSetCoverModel
	extends DocumentCorpusModel {

	public DocumentCorpusModel baseCorpus;
	public TokenizingOptionsModel tokenizingOptions;
	public Double weightCoverage;
	public Double totalCoveredWeight;
	
	public DocumentSetCoverModel(DocumentSetCover setCover) {
		super(setCover, true);
		
		if (setCover != null) {
			if (setCover.getBaseCorpus() != null) {
				this.baseCorpus = (DocumentCorpusModel)createViewModel(setCover.getBaseCorpus());
			}
			this.tokenizingOptions = new TokenizingOptionsModel(setCover.getTokenizingOptions());
			this.weightCoverage = setCover.getWeightCoverage();
			this.totalCoveredWeight = setCover.getTotalCoveredWeight();
			
			EntityManager em = SareTransactionalAction.em();
			if (em != null) {
				// TODO: perhaps there is a better way to do this than to put controller code in the view model.
				this.size = new DocumentSetCoverController().getCoverSize(em, setCover);
			} else {
				this.size = Iterables.size(setCover.getDocuments());
			}
		}
	}
	
	public DocumentSetCoverModel() {
		this(null);
	}
	
	public SetCoverFactoryOptions toFactoryOptions() {
		SetCoverFactoryOptions options = (SetCoverFactoryOptions)new SetCoverFactoryOptions()
			.setWeightCoverage(ObjectUtils.defaultIfNull(this.weightCoverage, 1.0))
			.setTitle(this.title)
			.setDescription(this.description)
			.setExistingId(this.id);
		
		if (this.tokenizingOptions != null) {
			options.setTokenizingOptions(this.tokenizingOptions.toTokenizingOptions());
		}
		
		return options;
	}
}