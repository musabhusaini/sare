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

package eu.ubipol.opinionmining.stem_engine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.hunspell.HunspellDictionary;
import org.apache.lucene.analysis.hunspell.HunspellStemmer;
import org.apache.lucene.analysis.hunspell.HunspellStemmer.Stem;
import org.apache.lucene.util.Version;

public class Stemmer {
	private static HunspellStemmer hunspellStem = null;
	private static PaiceStemmer paiceStem = null;
	private static EnglishStemmer snowballStem = null;

	public static List<String> GetStems(String word, String stemmerOptions) {
		if (stemmerOptions.contains("h") && hunspellStem == null) {
			try {
				InputStream aff = Stemmer.class
						.getResourceAsStream("resources/en_US.aff");
				InputStream dic = Stemmer.class
						.getResourceAsStream("resources/en_US.dic");
				HunspellDictionary dictionary = new HunspellDictionary(aff,
						dic, Version.LUCENE_36);
				hunspellStem = new HunspellStemmer(dictionary);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if (stemmerOptions.contains("p") && paiceStem == null)
			paiceStem = new PaiceStemmer(
					Stemmer.class
							.getResourceAsStream("resources/stemrules.txt"),
					"/p");
		if (stemmerOptions.contains("s") && snowballStem == null)
			snowballStem = new EnglishStemmer();

		List<String> result = new ArrayList<String>();
		String temp;
		if (stemmerOptions.contains("h"))
			for (Stem s : hunspellStem.stem(word))
				if (!result.contains(s))
					result.add(s.getStemString());
		if (stemmerOptions.contains("p")) {
			temp = paiceStem.stripAffixes(word);
			if (!result.contains(temp))
				result.add(temp);
		}
		if (stemmerOptions.contains("s")) {
			snowballStem.setCurrent(word);
			snowballStem.stem();
			temp = snowballStem.getCurrent();
			if (!result.contains(temp))
				result.add(temp);
		}
		temp = null;
		return result;
	}
}
