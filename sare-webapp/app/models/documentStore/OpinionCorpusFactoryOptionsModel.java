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

package models.documentStore;

import org.apache.commons.lang3.*;

import edu.sabanciuniv.sentilab.sare.models.opinion.OpinionCorpusFactoryOptions;
import models.base.ViewModel;

public class OpinionCorpusFactoryOptionsModel extends ViewModel {

	public String content;
	public String format;
	public String delimiter;
	public PersistentDocumentStoreModel details;
	
	public OpinionCorpusFactoryOptionsModel(OpinionCorpusFactoryOptions options) {
		super(options);
		
		if (options != null) {
			this.content = options.getContent();
			this.format = options.getFormat();
			this.delimiter = options.getTextDelimiter();
			this.details = new PersistentDocumentStoreModel();
			this.details.title = options.getTitle();
			this.details.description = options.getDescription();
			this.details.language = options.getLanguage();
		}
	}
	
	public OpinionCorpusFactoryOptionsModel() {
		this(null);
	}
	
	public OpinionCorpusFactoryOptions toFactoryOptions() {
		PersistentDocumentStoreModel corpusView = ObjectUtils.defaultIfNull(details, new PersistentDocumentStoreModel());
		
		return new OpinionCorpusFactoryOptions()
			.setContent(content)
			.setFormat(format)
			.setTextDelimiter(delimiter)
			.setTitle(corpusView.title)
			.setDescription(corpusView.description)
			.setLanguage(corpusView.language);
	}
}