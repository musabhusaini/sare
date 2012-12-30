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

package edu.sabanciuniv.sentilab.utils.text.nlp.stanford.english;

import edu.sabanciuniv.sentilab.utils.text.nlp.annotations.LinguisticProcessorInfo;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.*;
import edu.sabanciuniv.sentilab.utils.text.nlp.stanford.StanfordText;

/**
 * The Stanford natural language processor for the English language.
 * @author Mus'ab Husaini
 */
@LinguisticProcessorInfo(
	name = "Stanford Core NLP",
	language = "en",
	canTag = true,
	canParse = true
)
public class StanfordEnglishProcessor
	extends LinguisticProcessor {

	@Override
	public LinguisticText decompose(String text) {
		return new StanfordText(this, StanfordCoreNLPWrapper.getBasic().annotate(text));
	}
	
	@Override
	public LinguisticText tag(String text) {
		return new StanfordText(this, StanfordCoreNLPWrapper.getTagger().annotate(text));
	}

	@Override
	public LinguisticText parse(String text) {
		return new StanfordText(this, StanfordCoreNLPWrapper.getParser().annotate(text));
	}
}