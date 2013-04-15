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

package models.document;

import static controllers.base.SareTransactionalAction.*;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.*;

import com.google.common.collect.Maps;

import models.documentStore.AspectLexiconModel;

import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.sabanciuniv.sentilab.sare.models.opinion.AspectOpinionMinedDocument;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

public class AspectOpinionMinedDocumentModel
		extends FullTextDocumentModel {
	
	public Double polarity;
	public Map<AspectLexiconModel, Double> aspectPolarities;
	
	public AspectOpinionMinedDocumentModel(AspectOpinionMinedDocument document) {
		super(document);
		
		if (document != null) {
			if (document.getFullTextDocument() != null) {
				this.id = UuidUtils.normalize(document.getFullTextDocument().getId());
			}
			this.polarity = document.getPolarity();
			this.aspectPolarities = Maps.newHashMap();
			if (document.getAspectPolarities() != null) {
				for (Entry<AspectLexicon, Double> aspectEntry : document.getAspectPolarities().entrySet()) {
					if (aspectEntry.getKey() != null) {
						this.aspectPolarities.put(new AspectLexiconModel(aspectEntry.getKey(), false), aspectEntry.getValue());
					} else {
						this.polarity = aspectEntry.getValue();
					}
				}
			}
		}
	}
	
	public AspectOpinionMinedDocumentModel() {
		this(null);
	}
	
	public AspectOpinionMinedDocumentModel refreshAspects() {
		Validate.isTrue(hasEntityManager());
		
		Map<AspectLexiconModel, Double> tmpAspectPolarities = Maps.newHashMap();
		for (Entry<AspectLexiconModel, Double> aspectEntry : this.aspectPolarities.entrySet()) {
			tmpAspectPolarities.put(new AspectLexiconModel(fetchResource(UuidUtils.create(aspectEntry.getKey().id), AspectLexicon.class), false),
					aspectEntry.getValue());
		}
		
		this.aspectPolarities = tmpAspectPolarities;
		return this;
	}
}