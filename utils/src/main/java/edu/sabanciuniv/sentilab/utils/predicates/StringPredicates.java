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
