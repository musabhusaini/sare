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

package edu.sabanciuniv.sentilab.sare.controllers.opinion.aspectBased;

import java.util.*;
import java.util.Map.Entry;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionMiningEngine;
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.utils.*;
import eu.ubipol.opinionmining.web_package.CommentResult;

/**
 * A wrapper for the UbiPOL aspect-based opinion mining engine.
 * @author Mus'ab Husaini
 */
@OpinionMiningEngine.Of(name = "UbiPOL", code = UbiPolAspectBasedEngine.CODE)
public class UbiPolAspectBasedEngine
		extends AspectOpinionMiningEngine {
	
	public static final String CODE = "ubipol";

	@Override
	public AspectOpinionMinedCorpus mine() {
		Validate.notNull(this.getTestCorpus(), CannedMessages.NULL_ARGUMENT, "this.testCorpus");
		Validate.notNull(this.getAspectLexicon(), CannedMessages.NULL_ARGUMENT, "this.aspectLexicon");
		
		AspectOpinionMinedCorpus minedCorpus = this.getTargetMinedCorpus();
		
		double progress = 0.0;
		this.notifyProgress(progress, "mine");
		
		List<AspectOpinionMinedDocument> documents = Lists.newArrayList(minedCorpus.getDocuments(AspectOpinionMinedDocument.class));
		for (AspectOpinionMinedDocument document : documents) {
			try {
				CommentResult commentResult = new CommentResult(document.getContent(), this.getAspectLexicon());
				Map<AspectLexicon, Float> scoreMap = commentResult.getScoreMap();
				Map<String, Double> aspectPolarities = Maps.newHashMap();
				Double overallPolarity = null;
				for (Entry<AspectLexicon, Float> scoreEntry : scoreMap.entrySet()) {
					if (scoreEntry.getKey() == null) {
						overallPolarity = scoreEntry.getValue().doubleValue();
					} else {
						aspectPolarities.put(
							UuidUtils.normalize(scoreEntry.getKey().getIdentifier()), scoreEntry.getValue().doubleValue()
						);
					}
				}
				
				document
					.setAspectPolarities(aspectPolarities)
					.setPolarity(overallPolarity);

				progress += 1.0 / documents.size();
				this.notifyProgress(progress, "mine");
			} catch (Exception e) {
				progress = 1.0;
				throw new RuntimeException(e);
			} finally {
				this.notifyProgress(progress, "mine");
			}
		}
		
		this.notifyProgress(1.0, "mine");
		return minedCorpus;
	}
}