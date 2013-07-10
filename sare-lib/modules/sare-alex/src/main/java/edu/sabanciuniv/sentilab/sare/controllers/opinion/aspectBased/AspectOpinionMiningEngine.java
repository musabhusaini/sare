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

import java.util.List;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.collect.Lists;

import edu.sabanciuniv.sentilab.core.controllers.*;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionMiningEngine;
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

/**
 * The base class for all engines that can mine aspect-based opinion polarities.
 * @author Mus'ab Husaini
 */
public abstract class AspectOpinionMiningEngine
		extends OpinionMiningEngine implements ProgressObservablePrimitive {

	protected AspectOpinionMinedCorpus targetMinedCorpus;
	protected AspectLexicon aspectLexicon;
	protected List<ProgressObserver> progressObservers;
	
	protected AspectOpinionMiningEngine() {
		this.progressObservers = Lists.newArrayList();
	}
		
	/**
	 * Gets the aspect lexicon used for opinion mining.
	 * @return the {@link AspectLexicon} used.
	 */
	public AspectLexicon getAspectLexicon() {
		return aspectLexicon;
	}

	/**
	 * Sets the aspect lexicon to be used for opinion mining.
	 * @param aspectLexicon the {@link AspectLexicon} to be used.
	 * @return the {@code this} object.
	 */
	public AspectOpinionMiningEngine setAspectLexicon(AspectLexicon aspectLexicon) {
		this.aspectLexicon = aspectLexicon;
		return this;
	}
	
	@Override
	public AspectOpinionMinedCorpus getTargetMinedCorpus() {
		if (this.getTestCorpus() == null) {
			this.targetMinedCorpus = null;
		} else if (this.targetMinedCorpus == null
				|| !ObjectUtils.equals(this.getTestCorpus(), this.targetMinedCorpus.getCorpus())) {
			this.targetMinedCorpus = new AspectOpinionMinedCorpus(this.getTestCorpus(), this.getAspectLexicon())
				.setEngineCode(this.getCode());
		}
		
		return this.targetMinedCorpus;
	}
	
	@Override
	public abstract AspectOpinionMinedCorpus mine();
	
	@Override
	public void notifyProgress(double progress, String message) {
		for (ProgressObserver observer : this.progressObservers) {
			observer.observe(progress, message);
		}
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
}