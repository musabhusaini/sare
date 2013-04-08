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

package eu.ubipol.opinionmining.nlp_engine;

import java.util.Properties;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

public class Utils {
	private static StanfordCoreNLP pipeline;

	public static void loadNLPEngine() {
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos, parse");
		pipeline = new StanfordCoreNLP(props);
	}

	protected static StanfordCoreNLP getNLPEngine() {
		if (pipeline == null) {
			loadNLPEngine();
		}
		return pipeline;
	}

	protected enum WordType {
		ADJECTIVE, ADVERB, NOUN, VERB, OTHER
	}
}