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

package models.web;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.*;
import com.google.common.collect.*;

import controllers.modules.base.Module;
import edu.sabanciuniv.sentilab.utils.UuidUtils;
import models.base.ViewModel;

public class ModuleModel extends ViewModel {

	public String id;
	public String baseName;
	public String name;
	public String url;
	public boolean canPartiallyRender;
	public boolean allowSelfOutput;
	public double relevancyScore;
	public List<ModuleModel> subModules;
	
	public ModuleModel(Module module) {
		super(module);
		
		if (module != null) {
			this.id = UuidUtils.normalize(module.getId());
			this.baseName = module.getBaseDisplayName();
			this.name = module.getDisplayName();
			this.url = module.getRoute();
			this.canPartiallyRender = module.canPartiallyRender();
			this.allowSelfOutput = module.allowSelfOutput();
			this.relevancyScore = 1.0;
			
			if (module.getSubModules() != null) {
				this.subModules = Lists.transform(Lists.newArrayList(Iterables.filter(module.getSubModules(), Predicates.notNull())),
					new Function<Module, ModuleModel>() {
						@Override
						@Nullable
						public ModuleModel apply(@Nullable Module input) {
							return new ModuleModel(input);
						}
					}
				);
			}
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

	public String getUrl() {
		return url;
	}

	public ModuleModel setUrl(String url) {
		this.url = url;
		return this;
	}
	
	public boolean getCanPartiallyRender() {
		return this.canPartiallyRender;
	}
	
	public ModuleModel setCanPartiallyRender(boolean canPartiallyRender) {
		this.canPartiallyRender = canPartiallyRender;
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