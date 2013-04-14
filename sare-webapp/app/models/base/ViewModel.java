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

package models.base;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.annotate.*;
import org.codehaus.jackson.node.ObjectNode;

import controllers.factories.ViewModelFactory;
import edu.sabanciuniv.sentilab.core.models.IModel;

import play.libs.Json;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ViewModel 
		implements IModel {

	public static <T> ViewModel createViewModel(T model) {
		ViewModel viewModel = new ViewModelFactory()
			.setModel(model)
			.create();
		if (viewModel == null) {
			throw new IllegalArgumentException();
		}
		
		return viewModel;
	}
	
	public static ViewModel createViewModel(JsonNode json) {
		ViewModel viewModel = new ViewModelFactory()
			.setJson(json)
			.create();
		if (viewModel == null) {
			throw new IllegalArgumentException();
		}
		
		return viewModel;
	}
	
	public static <T> ViewModel createViewModelQuietly(T model, ViewModel defaultViewModel) {
		try {
			return createViewModel(model);
		} catch (IllegalArgumentException e) {
			return defaultViewModel;
		}
	}
	
	public static <T> ViewModel createViewModelQuietly(T model) {
		return createViewModelQuietly(model, new ViewModel());
	}
	
	public static ViewModel createViewModelQuietly(JsonNode json, ViewModel defaultViewModel) {
		try {
			return createViewModel(json);
		} catch (IllegalArgumentException e) {
			return defaultViewModel;
		}
	}
	
	public static ViewModel createViewModelQuietly(JsonNode json) {
		return createViewModelQuietly(json, new ViewModel());
	}

	public String type;
	
	public ViewModel(Object obj) {
		if (obj != null) {
			this.type = obj.getClass().getSimpleName();
		}
	}
	
	public ViewModel() {
		this(null);
	}
	
	public JsonNode asJson(Iterable<String> excludedProperties) {
		JsonNode json = Json.toJson(this);
		if (excludedProperties != null && json.isObject()) {
			for (String property : excludedProperties) {
				((ObjectNode)json).remove(property);
			}
		}
		return json;
	}
	
	public JsonNode asJson() {
		return this.asJson(null);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ViewModel) {
			return StringUtils.equals(this.type, ((ViewModel)obj).type);
		}
		
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return StringUtils.defaultString(this.type).hashCode();
	}
}