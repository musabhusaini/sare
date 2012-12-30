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

package edu.sabanciuniv.sentilab.utils.predicates;

import java.util.regex.Pattern;

import com.google.common.base.Predicate;

public abstract class StringPredicates {
	
	private StringPredicates() {
		// prevent instantiation.
	}
	
	public static Predicate<String> startsWith(final String start, final boolean ignoreCase) {
		return new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return ignoreCase ? input.toLowerCase().startsWith(start.toLowerCase()) : input.startsWith(start);
			}
		};
	}
	
	public static Predicate<String> startsWith(final String start) {
		return startsWith(start, false);
	}
	
	public static Predicate<String> isStartOf(final String input, final boolean ignoreCase) {
		return new Predicate<String>() {
			@Override
			public boolean apply(String start) {
				return ignoreCase ? input.toLowerCase().startsWith(start.toLowerCase()) : input.startsWith(start);
			}
		};
	}
	
	public static Predicate<String> isStartOf(final String input) {
		return isStartOf(input, false);
	}
	
	public static Predicate<String> containsPattern(final Pattern pattern) {
		return new Predicate<String>() {
			@Override
			public boolean apply(String input) {
				return pattern.matcher(input).find();
			}
		};
	}
	
	public static Predicate<String> containsPattern(final String pattern) {
		return containsPattern(Pattern.compile(pattern));
	}
	
	public static Predicate<Pattern> patternContains(final String input) {
		return new Predicate<Pattern>() {
			@Override
			public boolean apply(Pattern pattern) {
				return pattern.matcher(input).find();
			}
		};
	}
}
