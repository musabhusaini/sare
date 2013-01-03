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

package models;

import controllers.modules.base.Module;
import models.base.ViewModel;

public class ModuleModel extends ViewModel {

	public String name;
	public String route;
	public double relevancyScore;
	
	public ModuleModel(Module module) {
		super(module);
		
		if (module != null) {
			this.name = module.getDisplayName();
		}
	}
	
	public ModuleModel() {
		this(null);
	}
	
	public String getName() {
		return name;
	}

	public ModuleModel setName(String name) {
		this.name = name;
		return this;
	}

	public String getRoute() {
		return route;
	}

	public ModuleModel setRoute(String route) {
		this.route = route;
		return this;
	}

	public double getRelevancyScore() {
		return relevancyScore;
	}

	public ModuleModel setRelevancyScore(double relevancyScore) {
		this.relevancyScore = relevancyScore;
		return this;
	}
}