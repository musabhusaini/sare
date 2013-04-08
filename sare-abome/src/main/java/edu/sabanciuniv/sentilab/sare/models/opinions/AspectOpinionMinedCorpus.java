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

package edu.sabanciuniv.sentilab.sare.models.opinions;

import javax.persistence.*;

import com.google.common.base.Function;

import edu.sabanciuniv.sentilab.core.models.*;
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;
import edu.sabanciuniv.sentilab.sare.models.opinion.IOpinionMinedCorpus;

/**
 * A {@link DocumentCorpus} that has been mined for aspect ratings.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("alex-opinion-mined-corpus")
public class AspectOpinionMinedCorpus
		extends CorpusLexiconHybridStore<AspectLexicon>
		implements UserInaccessibleModel, IOpinionMinedCorpus {

	private static final long serialVersionUID = -7170505034416346487L;
	
	/**
	 * Creates an instance of {@link AspectOpinionMinedCorpus}.
	 * @param corpus the {@link DocumentCorpus} to be mined.
	 * @param lexicon the {@link AspectLexicon} to use for mining.
	 */
	public AspectOpinionMinedCorpus(DocumentCorpus corpus, AspectLexicon lexicon) {
		super(corpus, lexicon, new Function<PersistentDocument, AspectOpinionMinedDocument>() {
			public AspectOpinionMinedDocument apply(PersistentDocument input) {
				if (input instanceof FullTextDocument) {
					return new AspectOpinionMinedDocument((FullTextDocument)input);
				}
				return null;
			}
		});
	}
	
	/**
	 * Creates an instance of {@link AspectOpinionMinedCorpus}.
	 */
	public AspectOpinionMinedCorpus() {
		this(null, null);
	}
	
	/**
	 * Gets information about the opinion mining engine used.
	 * @return information about the opinion mining engine.
	 */
	@Override
	public String getEngineCode() {
		return this.getProperty("engine", String.class);
	}
	
	/**
	 * Sets information about the opinion mining engine used.
	 * @param engine information about the opinion mining engine.
	 * @return the {@code this} object.
	 */
	public AspectOpinionMinedCorpus setEngine(String engine) {
		this.setProperty("engine", engine);
		return this;
	}
	
	@Override
	public DocumentCorpus getAccessible() {
		return this.getCorpus();
	}
}