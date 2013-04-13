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

package eu.ubipol.opinionmining.web_package;

import java.util.*;

import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import eu.ubipol.opinionmining.database_engine.DatabaseAdapter;
import eu.ubipol.opinionmining.nlp_engine.*;

public class CommentResult {
	private Paragraph paragraph;

	public CommentResult(String comment, AspectLexicon lexicon)
			throws Exception {
		this.paragraph = new Paragraph(comment, new DatabaseAdapter(lexicon));
	}

	public Map<AspectLexicon, Double> getScoreMap() {
		return this.paragraph.getScoreMap();
	}

	public List<ModifierItem> getModifierList() {
		return this.paragraph.getModifierList();
	}

	public List<SentenceObject> getSentences() {
		return this.paragraph.getSentenceMap();
	}
}