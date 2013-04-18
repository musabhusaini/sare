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

package controllers.modules.opinionMiners;

import java.util.UUID;

import models.web.*;

import controllers.modules.base.Module;
import controllers.modules.opinionMiners.base.*;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.aspectBased.UbiPolAspectBasedEngine;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

@OpinionMiner.Coded("ubipol")
@Module.Requires(UniqueModel.class)
public class UbiPolOpinionMiner
		extends AspectOpinionMiner {

	@Override
	public UUID getId() {
		return UuidUtils.create("d438a7c53b45432b9ef9ab5934fa7422");
	}

	@Override
	public String getDisplayName() {
		return "UbiPOL Engine";
	}
	
	@Override
	public String getCode() {
		return UbiPolAspectBasedEngine.CODE;
	}
}