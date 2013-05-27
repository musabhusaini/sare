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

import models.grabbers.GrabbersModel;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionCorpusFactory;

public class OpinionCorpusFactoryModel
		extends PersistentDocumentStoreFactoryModel {

	public String content;
	public String format;
	public String delimiter;
	public GrabbersModel grabbers;
	
	public OpinionCorpusFactoryModel(OpinionCorpusFactory factory) {
		super(factory);
		
		if (factory != null) {
			this.content = factory.getContent();
			this.format = factory.getFormat();
			this.delimiter = factory.getTextDelimiter();
		}
	}
	
	public OpinionCorpusFactoryModel() {
		this(null);
	}
	
	public OpinionCorpusFactory toFactory() {
		OpinionCorpusFactory factory = new OpinionCorpusFactory();
		factory.setContent(this.content);
		factory.setFormat(this.format);
		factory.setTextDelimiter(this.delimiter);
		factory.setTitle(this.title);
		factory.setDescription(this.description);
		factory.setLanguage(this.language);
		return factory;
	}
}