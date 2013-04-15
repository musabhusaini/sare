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

import java.lang.reflect.Method;

class Among {
	public Among(String s, int substring_i, int result, String methodname,
			SnowballProgram methodobject) {
		this.s_size = s.length();
		this.s = s.toCharArray();
		this.substring_i = substring_i;
		this.result = result;
		this.methodobject = methodobject;
		if (methodname.length() == 0) {
			this.method = null;
		} else {
			try {
				this.method = methodobject.getClass().getDeclaredMethod(
						methodname, new Class[0]);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public final int s_size; /* search string */
	public final char[] s; /* search string */
	public final int substring_i; /* index to longest matching substring */
	public final int result; /* result of the lookup */
	public final Method method; /* method to use if substring matches */
	public final SnowballProgram methodobject; /* object to invoke method on */
};