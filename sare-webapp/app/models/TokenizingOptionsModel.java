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

package models;

import java.util.List;

import com.google.common.collect.Lists;

import edu.sabanciuniv.sentilab.sare.models.base.document.TokenizingOptions;
import models.base.ViewModel;

public class TokenizingOptionsModel
	extends ViewModel {

	public List<String> tags;
	public boolean isLemmatized;
	
	public TokenizingOptionsModel(TokenizingOptions tokenizingOptions) {
		super(tokenizingOptions);
		
		this.tags = Lists.newArrayList();
		
		if (tokenizingOptions != null) {
			this.tags = tokenizingOptions.getTags();
			this.isLemmatized = tokenizingOptions.isLemmatized();
		}
	}
	
	public TokenizingOptionsModel() {
		this(null);
	}
	
	public TokenizingOptions toTokenizingOptions() {
		return new TokenizingOptions()
			.setLemmatized(this.isLemmatized)
			.setTags(this.tags);
	}
}