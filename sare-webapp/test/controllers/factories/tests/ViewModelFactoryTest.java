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

package controllers.factories.tests;

import models.base.*;
import models.document.*;

import org.apache.commons.lang3.ClassUtils;
import org.codehaus.jackson.JsonNode;
import org.junit.*;

import base.TestBase;

import controllers.factories.ViewModelFactory;
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectExpression;
import edu.sabanciuniv.sentilab.sare.models.base.PersistentObject;
import edu.sabanciuniv.sentilab.sare.models.opinion.OpinionDocument;

import play.libs.Json;

import static org.fest.assertions.Assertions.*;

public class ViewModelFactoryTest extends TestBase {

	private class PersistentTestObject extends PersistentObject {
		/**
		 * 
		 */
		private static final long serialVersionUID = 3615399775176377742L;

		@Override
		public String getOwnerId() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	@Test
	public void testCreateWithModelWorksWhenOwnViewExists() {
		OpinionDocument model = (OpinionDocument)new OpinionDocument()
			.setPolarity(-0.9)
			.setContent("some content");
		ViewModelFactory factory = new ViewModelFactory();
		ViewModelFactoryOptions options = new ViewModelFactoryOptions()
			.setModel(model);
		ViewModel viewModel = factory.create(options);
		
		assertThat(viewModel).isNotNull();
		assertThat(viewModel).isInstanceOf(OpinionDocumentView.class);
		
		OpinionDocumentView typedViewModel = (OpinionDocumentView)viewModel;
		assertThat(typedViewModel.type).isEqualTo(ClassUtils.getShortClassName(model.getClass()));
		assertThat(typedViewModel.content).isEqualTo(model.getContent());
		assertThat(typedViewModel.polarity).isEqualTo(model.getPolarity());
	}
	
	@Test
	public void testCreateWithModelWorksWhenOnlySuperclassViewExists() {
		AspectExpression model = (AspectExpression)new AspectExpression()
			.setContent("some content");
		ViewModelFactory factory = new ViewModelFactory();
		ViewModelFactoryOptions options = new ViewModelFactoryOptions()
			.setModel(model);
		ViewModel viewModel = factory.create(options);
		
		assertThat(viewModel).isNotNull();
		assertThat(viewModel).isInstanceOf(PersistentDocumentView.class);
		
		PersistentDocumentView typedViewModel = (PersistentDocumentView)viewModel;
		assertThat(typedViewModel.type).isEqualTo(ClassUtils.getShortClassName(model.getClass()));
		assertThat(typedViewModel.content).isEqualTo(model.getContent());
	}
	
	@Test
	public void testCreateWithModelFailsWhenNoViewExists() {
		PersistentTestObject model = new PersistentTestObject();
		ViewModelFactory factory = new ViewModelFactory();
		ViewModelFactoryOptions options = new ViewModelFactoryOptions()
			.setModel(model);
		ViewModel viewModel = factory.create(options);
		
		assertThat(viewModel).isNull();
	}
	
	@Test
	public void testCreateWithJsonWorksWhenOwnViewExists() {
		JsonNode json = Json.parse("{" +
				"\"type\": \"OpinionDocument\"," +
				"\"content\": \"some content\"" +
			"}");
		ViewModelFactory factory = new ViewModelFactory();
		ViewModelFactoryOptions options = new ViewModelFactoryOptions()
			.setJson(json);
		ViewModel viewModel = factory.create(options);
		
		assertThat(viewModel).isNotNull();
		assertThat(viewModel).isInstanceOf(OpinionDocumentView.class);
		
		PersistentDocumentView typedViewModel = (PersistentDocumentView)viewModel;
		assertThat(typedViewModel.type).isEqualTo(ClassUtils.getShortClassName(OpinionDocument.class));
		assertThat(typedViewModel.content).isEqualTo(json.get("content").asText());
	}
	
	@Test
	public void testCreateWithJsonFailsWhenSuperclassViewExists() {
		JsonNode json = Json.parse("{" +
				"\"type\": \"AspectExpression\"," +
				"\"content\": \"some content\"" +
			"}");
		ViewModelFactory factory = new ViewModelFactory();
		ViewModelFactoryOptions options = new ViewModelFactoryOptions()
			.setJson(json);
		ViewModel viewModel = factory.create(options);
		
		assertThat(viewModel).isNull();
	}
	
	@Test
	public void testCreateWithJsonFailsWhenNoViewExists() {
		JsonNode json = Json.parse("{" +
				"\"type\": \"Dummy\"," +
				"\"content\": \"some content\"" +
			"}");
		ViewModelFactory factory = new ViewModelFactory();
		ViewModelFactoryOptions options = new ViewModelFactoryOptions()
			.setJson(json);
		ViewModel viewModel = factory.create(options);
		
		assertThat(viewModel).isNull();
	}
}