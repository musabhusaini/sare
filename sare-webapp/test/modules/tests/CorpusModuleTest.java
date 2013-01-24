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

package modules.tests;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.junit.*;

import base.TestBase;

import play.mvc.*;
import play.libs.Json;

import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class CorpusModuleTest extends TestBase {

	@Test
	public void testSupportedLanguages() {
		Result result = callAction(controllers.routes.ref.CollectionsController.supportedLanguages());
		
		assertThat(result).isNotNull();
		assertThat(status(result)).isEqualTo(OK);
		assertThat(contentType(result)).isEqualTo("application/json");
		
		JsonNode response = Json.parse(contentAsString(result));
		assertThat(response).isNotNull();
		assertThat(response.isArray()).isTrue();
		
		ArrayNode responseArray = (ArrayNode)response;
		assertThat(responseArray.size()).isGreaterThan(0);
	}
}