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
import java.util.Map.Entry;

public class Aspects {
	private static Map<String, List<String>> aspectList = null;

	public static void fillAspectList(File file) {
		try {
			aspectList = new TreeMap<String, List<String>>();
			Scanner reader = new Scanner(file);
			while (reader.hasNextLine()) {
				String[] lineArray = reader.nextLine().split("\t");
				lineArray[0] = lineArray[0].substring(1,
						lineArray[0].length() - 1);
				for (int i = 1; i < lineArray.length; i++)
					addKeyword(lineArray[0], lineArray[i]);
				lineArray = null;
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void fillAspectList() {
		try {
			aspectList = new TreeMap<String, List<String>>();
			Scanner reader = new Scanner(new File("FeatureWords.txt"));
			while (reader.hasNextLine()) {
				String[] lineArray = reader.nextLine().split("\t");
				lineArray[0] = lineArray[0].substring(1,
						lineArray[0].length() - 1);
				for (int i = 1; i < lineArray.length; i++)
					addKeyword(lineArray[0], lineArray[i]);
				lineArray = null;
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void addKeyword(String aspectName, String keyword) {
		aspectName = aspectName.toLowerCase().trim();
		keyword = keyword.toLowerCase().trim();
		if (!aspectList.containsKey(aspectName))
			aspectList.put(aspectName, new ArrayList<String>());
		aspectList.get(aspectName).add(keyword);
	}

	public static Long getAspectOfKeyword(String keyword) {
		if (aspectList == null)
			fillAspectList();
		keyword = keyword.toLowerCase().trim();
		Long counter = new Long(0);
		for (Entry<String, List<String>> e : aspectList.entrySet()) {
			if (e.getValue().contains(keyword))
				return counter;
			counter++;
		}
		return new Long(-1);
	}

	public static String getAspectName(Long aspectId) {
		if (aspectList == null)
			fillAspectList();
		Long counter = new Long(0);
		for (Entry<String, List<String>> e : aspectList.entrySet()) {
			if (counter.compareTo(aspectId) != 0)
				counter++;
			else
				return e.getKey();
		}
		return null;
	}
}