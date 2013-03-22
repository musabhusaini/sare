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

import org.apache.commons.lang3.StringUtils;

import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticToken;
import models.base.ViewModel;
import models.documentStore.AspectLexiconModel;

public class LexiconBuilderDocumentTokenModel
	extends ViewModel {
	
	public String text;
	public String lemma;
	public String tag;
	public String trailing;
	public boolean emphasized;
	public boolean seen;
	public AspectLexiconModel aspect;
	
	public LexiconBuilderDocumentTokenModel(LinguisticToken token) {
		if (token != null) {
			this.text = token.getText();
			this.lemma = token.getLemma();
			this.tag = token.getPosTag().getSimpleTag();
			this.trailing = " ".equals(token.getTrailingSeparator()) ? "&nbsp;" :
				(StringUtils.defaultString(token.getTrailingSeparator()).contains("\n") ? "<br/>": token.getTrailingSeparator());
		}
	}
	
	public LexiconBuilderDocumentTokenModel() {
		this(null);
	}
}