package edu.sabanciuniv.sentilab.core.controllers;

/**
 * A class that implements this interface allows for observing the progress of the last invoked action.
 * @author Mus'ab Husaini
 */
public interface ProgressObservable {
	
	/**
	 * Gets the current progress of the last invoked action.
	 * @return a value between {@code 0.0} to {@code 1.0} indicating the ratio of progress with {@code 0.0} indicating no progress
	 * and {@code 1.0} indicating completion.
	 */
	public double getProgress();
}
