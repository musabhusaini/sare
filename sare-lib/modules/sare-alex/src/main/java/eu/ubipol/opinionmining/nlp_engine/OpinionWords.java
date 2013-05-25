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

import java.io.File;
import java.util.*;

import eu.ubipol.opinionmining.nlp_engine.Utils.WordType;


public class OpinionWords {
	private static Map<String, Double> wordScores;

	public static void fillScoreList(File file) {
		Scanner reader = null;
		try {
			wordScores = new HashMap<String, Double>();
			reader = new Scanner(file);
			while (reader.hasNextLine()) {
				String[] line = reader.nextLine().split("\t");
				wordScores.put(line[0], Double.valueOf(line[1]));
			}
		} catch (Exception e) {
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	private static void fillScoreList() {
		Scanner reader = null;
		try {
			wordScores = new HashMap<String, Double>();
			reader = new Scanner(
					OpinionWords.class
							.getResourceAsStream("/sentiwordnet_stemmed.txt"));
			while (reader.hasNextLine()) {
				String[] line = reader.nextLine().split("\t");
				wordScores.put(line[0], Double.valueOf(line[1]));
			}
		} catch (Exception e) {
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	private static Map<String, Double> getScoreList() {
		try {
			if (wordScores == null) {
				fillScoreList();
			}
			return wordScores;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	protected static Double getWordScore(String word, WordType wordType) {
		String searchString = word;
		if (wordType == WordType.ADJECTIVE)
			searchString += "_a";
		else if (wordType == WordType.ADVERB)
			searchString += "_r";
		else if (wordType == WordType.VERB)
			searchString += "_v";
		else if (wordType == WordType.NOUN)
			searchString += "_n";
		else
			return new Double(-2);
		if (getScoreList().containsKey(searchString))
			return getScoreList().get(searchString);
		else
			return new Double(-2);
	}
}