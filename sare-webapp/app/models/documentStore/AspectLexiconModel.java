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

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;

public class AspectLexiconModel
	extends PersistentDocumentStoreModel {
	
	public DocumentCorpusModel baseCorpus;
	public AspectLexiconModel parent;
	public List<AspectLexiconModel> children;
	
	public AspectLexiconModel(AspectLexicon lexicon, boolean expandChildren) {
		super(lexicon);
		
		if (lexicon != null) {
			if (lexicon.getBaseCorpus() != null) {
				this.baseCorpus = (DocumentCorpusModel)createViewModel(lexicon.getBaseCorpus());
			}
			
			if (Iterables.size(lexicon.getDerivedStores()) == 0) {
				this.children = null;
			} else {
				this.children = expandChildren ? Lists.newArrayList(Iterables.transform(Iterables.filter(lexicon.getDerivedStores(), AspectLexicon.class),
					new Function<AspectLexicon, AspectLexiconModel>() {
						@Override
						@Nullable
						public AspectLexiconModel apply(@Nullable AspectLexicon input) {
							return new AspectLexiconModel(input, false);
						}
					})) : Lists.<AspectLexiconModel>newArrayList();
			}
			
			if (lexicon.getBaseStore() instanceof AspectLexicon) {
				this.parent = new AspectLexiconModel((AspectLexicon)lexicon.getBaseStore(), false);
			}
		}
	}
	
	public AspectLexiconModel(AspectLexicon lexicon) {
		this(lexicon, true);
	}
	
	public AspectLexiconModel() {
		this(null);
	}
}