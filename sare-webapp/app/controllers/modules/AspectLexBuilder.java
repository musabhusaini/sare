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

package controllers.modules;

import static controllers.base.SareTransactionalAction.*;
import static controllers.base.SessionedAction.*;
import static models.base.ViewModel.*;

import java.util.*;

import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import com.google.common.base.*;
import com.google.common.collect.*;

import play.api.templates.Html;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import views.html.tags.*;
import models.base.ViewModel;
import models.document.LexiconBuilderDocumentTokenModel;
import models.document.PersistentDocumentModel;
import models.documentStore.*;
import controllers.CollectionsController;
import controllers.base.*;
import controllers.modules.base.Module;
import edu.sabanciuniv.sentilab.sare.controllers.aspect.AspectLexiconController;
import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.*;
import edu.sabanciuniv.sentilab.sare.models.aspect.*;
import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;
import edu.sabanciuniv.sentilab.utils.UuidUtils;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticToken;

@With(SareTransactionalAction.class)
@Module.Requireses({
	@Module.Requires,
	@Module.Requires(DocumentCorpusModel.class),
	@Module.Requires({DocumentCorpusModel.class, AspectLexiconModel.class}),
	@Module.Requires(AspectLexiconModel.class)
})
public class AspectLexBuilder extends Module {

	public static List<PersistentDocumentStoreModel> getLexica(DocumentCorpusModel corpus) {
		LexiconController lexiconController = new LexiconController();
		List<PersistentDocumentStoreModel> lexica = Lists.newArrayList();
		for (String lexiconId : lexiconController.getAllLexica(em(), getUsername(), AspectLexicon.class)) {
			AspectLexicon lexicon = fetchResource(lexiconId, AspectLexicon.class);
			if (corpus == null || (lexicon.getBaseStore() != null
				&& UuidUtils.normalize(corpus.id).equals(UuidUtils.normalize(lexicon.getBaseCorpus().getId())))) {
				
				PersistentDocumentStoreModel lexiconVM = (PersistentDocumentStoreModel)createViewModel(lexicon);
				lexiconVM.populateSize(em(), lexicon);
				lexica.add(lexiconVM);
			}
		}
		return lexica;
	}
	
	public static List<PersistentDocumentStoreModel> getLexica() {
		return getLexica(null);
	}
	
	@Override
	public UUID getId() {
		return UuidUtils.create("454cbf44cf154b0c8792fedc717da027");
	}
	
	@Override
	public String getDisplayName() {
		return "Build aspect lexicon";
	}

	@Override
	public String getRoute() {
		DocumentCorpusModel corpus = (DocumentCorpusModel)Iterables.find(this.viewModels, Predicates.instanceOf(DocumentCorpusModel.class), null);
		AspectLexiconModel lexicon = (AspectLexiconModel)Iterables.find(this.viewModels, Predicates.instanceOf(AspectLexiconModel.class), null);
		return controllers.modules.routes.AspectLexBuilder.modulePage(
			corpus != null ? corpus.id : null,
			lexicon != null ? lexicon.id : null,
			false).url();
	}
	
	public static Result modulePage(String corpus, String lexicon, boolean partial) {
		DocumentCorpus corpusObj = StringUtils.isNotEmpty(corpus) ? fetchResource(corpus, DocumentCorpus.class) : null;
		AspectLexicon lexiconObj = StringUtils.isNotEmpty(lexicon) ? fetchResource(lexicon, AspectLexicon.class) : null;
		if (lexiconObj == null
			&& new LexiconController().getAllLexica(em(), SessionedAction.getUsername(), AspectLexicon.class).size() == 0) {
			create(corpus);
		}
		
		if (lexiconObj != null && corpusObj == null) {
			corpusObj = lexiconObj.getBaseCorpus();
		}
		
		DocumentCorpusModel corpusVM = null;
		if (corpusObj != null) {
			corpusVM = (DocumentCorpusModel)createViewModel(corpusObj);
			corpusVM.populateSize(em(), corpusObj);
		}
		AspectLexiconModel lexiconVM = null;
		if (lexiconObj != null) {
			lexiconVM = (AspectLexiconModel)createViewModel(lexiconObj);
			lexiconVM.populateSize(em(), lexiconObj);
		}
		
		return moduleRender(new AspectLexBuilder().setViewModels(Lists.<ViewModel>newArrayList(corpusVM, lexiconVM)),
			aspectLexBuilder.render(corpusVM, lexiconVM, true), partial);
	}
	
	public static Result create(String corpus) {
		return update(corpus, null);
	}
	
	public static Result update(String corpus, String lexicon) {
		DocumentCorpus corpusObj = null;
		if (corpus != null) {
			corpusObj = fetchResource(corpus, DocumentCorpus.class);
		}
		AspectLexicon lexiconObj = null;
		if (lexicon != null) {
			lexiconObj = fetchResource(lexicon, AspectLexicon.class);
		}
		AspectLexiconFactoryOptions options = null;
		
		MultipartFormData formData = request().body().asMultipartFormData();
		if (formData != null) {
			// if we have a multi-part form with a file.
			if (formData.getFiles() != null) {
				// get either the file named "file" or the first one.
				FilePart filePart = ObjectUtils.defaultIfNull(formData.getFile("file"),
					Iterables.getFirst(formData.getFiles(), null));
				if (filePart != null) {
					options = (AspectLexiconFactoryOptions)new AspectLexiconFactoryOptions()
						.setFile(filePart.getFile())
						.setFormat(FilenameUtils.getExtension(filePart.getFilename()));
				}
			}
		} else {
			JsonNode json = request().body().asJson();
			if (json != null) {
				AspectLexiconFactoryOptionsModel viewModel = Json.fromJson(json, AspectLexiconFactoryOptionsModel.class);
				if (viewModel != null) {
					options = viewModel.toFactoryOptions();
					
					if (lexiconObj != null) {
						if (corpusObj != null && (lexiconObj.getBaseCorpus() == null
							|| !ObjectUtils.equals(lexiconObj.getBaseCorpus(), corpusObj))) {
							throw new IllegalArgumentException();
						}
					}
				} else {
					throw new IllegalArgumentException();
				}
			} else {
				// if not json, then just create empty options.
				options = new AspectLexiconFactoryOptions();
			}
		}
		
		if (options == null) {
			throw new IllegalArgumentException();
		}
		
		if (lexicon == null && StringUtils.isEmpty(options.getTitle())) {
			options.setTitle("Untitled aspect lexicon");
		}
		
		options
			.setBaseStore(corpusObj)
			.setOwnerId(SessionedAction.getUsername(ctx()))
			.setExistingId(lexicon)
			.setEm(em());
		
		AspectLexiconModel lexiconVM = null;
		AspectLexiconController factory = new AspectLexiconController();
		lexiconObj = factory.create(options);
		if (!em().contains(lexiconObj)) {
			em().persist(lexiconObj);
			
			lexiconVM = (AspectLexiconModel)createViewModel(lexiconObj);
			lexiconVM.populateSize(em(), lexiconObj);
			return created(lexiconVM.asJson());
		}
		
		em().merge(lexiconObj);
		
		lexiconVM = (AspectLexiconModel)createViewModel(lexiconObj);
		lexiconVM.populateSize(em(), lexiconObj);
		return ok(lexiconVM.asJson());
	}
	
	public static Html renderDocumentsView(String corpus, String lexicon) {
		LexiconBuilderDocumentStore builder = fetchBuilder(corpus, lexicon);
		DocumentCorpus corpusObj = fetchResource(corpus, DocumentCorpus.class);
		AspectLexicon lexiconObj = fetchResource(lexicon, AspectLexicon.class);
		
		DocumentCorpusModel corpusVM = (DocumentCorpusModel)createViewModel(corpusObj);
		corpusVM.populateSize(em(), corpusObj);
		
		AspectLexiconModel lexiconVM = (AspectLexiconModel)createViewModel(lexiconObj);
		lexiconVM.populateSize(em(), lexiconObj);
		
		return documentSlider
			.render(corpusVM, lexiconVM,
				corpusObj.getLinguisticProcessor().getBasicPosTags(),
				Lists.newArrayList(Splitter.on("|")
					.split(StringUtils.defaultString(builder.getProperty("emphasizedTags", String.class)))));
	}
	
	public static Result documentsView(String corpus, String lexicon) {
		return ok(renderDocumentsView(corpus, lexicon));
	}
	
	public static Result lexiconView(String lexicon) {
		AspectLexicon lexiconObj = fetchResource(lexicon, AspectLexicon.class);
		AspectLexiconModel lexiconVM = (AspectLexiconModel)createViewModel(lexiconObj);
		lexiconVM.populateSize(em(), lexiconObj);
		return ok(aspectLexicon.render(lexiconVM, true));
	}
	
	private static LexiconBuilderDocumentStore fetchBuilder(String corpus, String lexicon) {
		DocumentCorpus corpusObj = fetchResource(corpus, DocumentCorpus.class);
		AspectLexicon lexiconObj = fetchResource(lexicon, AspectLexicon.class);
		LexiconBuilderController controller = new LexiconBuilderController();
		LexiconBuilderDocumentStore builder = controller.findBuilder(em(), corpusObj, lexiconObj);
		if (builder == null) {
			builder = (LexiconBuilderDocumentStore)new LexiconBuilderDocumentStore(corpusObj, lexiconObj)
				.setOwnerId(getUsername());
			em().persist(builder);
		} else {
			controller.refreshBuilder(em(), builder);
		}
		
		return builder;
	}
	
	private static LexiconBuilderDocument fetchDocument(LexiconBuilderDocumentStore builder, Long rank) {
		if (rank < 0) {
			rank = null;
		}
		
		LexiconBuilderDocument document = new LexiconBuilderController().getDocument(em(), builder, rank);
		if (document != null && document.getFullTextDocument() != null) {
			TokenizingOptions tokenizingOptions = document.getFullTextDocument().getTokenizingOptions();
			document.getFullTextDocument().setTokenizingOptions(tokenizingOptions.setLemmatized(true));
		}
		return document;
	}
		
	public static Result getDocument(String corpus, String lexicon, final String emphasis, Long rank) {
		final LexiconBuilderDocumentStore builder = fetchBuilder(corpus, lexicon);
		final AspectLexicon lexiconObj = (AspectLexicon)builder.getLexicon();
		final LexiconBuilderController controller = new LexiconBuilderController();
		
		LexiconBuilderDocument document = fetchDocument(builder, rank);
		if (document != null && document.getFullTextDocument() != null) {
			List<LexiconBuilderDocumentTokenModel> tokens = Lists.newArrayList(Iterables.transform(
				document.getFullTextDocument().getParsedContent().getTokens(),
					new Function<LinguisticToken, LexiconBuilderDocumentTokenModel>() {
						@Override
						@Nullable
						public LexiconBuilderDocumentTokenModel apply(@Nullable LinguisticToken input) {
							LexiconBuilderDocumentTokenModel model = new LexiconBuilderDocumentTokenModel(input);
							model.emphasized = input.getPosTag().is(emphasis);
							if (model.emphasized) {
								model.seen = controller.isSeenToken(em(), builder, input.toString());
								
								AspectExpression expression = lexiconObj.findExpression(input.getLemma(), true);
								if (expression == null) {
									expression = lexiconObj.findExpression(input.getText(), true);
								}
								if (expression != null && expression.getAspect() != null) {
									model.aspect = (AspectLexiconModel)createViewModel(expression.getAspect());
								}
							}
							return model;
						}
					}));
			ObjectNode json = (ObjectNode)createViewModel(document.getBaseDocument()).asJson();
			json.put("enhancedContent", enhancedDocument.render(tokens, emphasis).body());
			json.put("rank", document.getRank());
			return ok(json);
		}
		
		return notFoundEntity(ObjectUtils.toString(rank));
	}
	
	public static Result seeDocument(String corpus, String lexicon, String emphasis, Long rank) {
		LexiconBuilderDocumentStore builder = fetchBuilder(corpus, lexicon);
		LexiconBuilderDocument document = fetchDocument(builder, rank);
		
		if (document != null && document.getFullTextDocument() != null) {
			new LexiconBuilderController().setSeenDocument(em(), document, emphasis);
			
			builder.setProperty("emphasizedTags", emphasis);
			em().merge(builder);
			
			return ok(createViewModel(document.getFullTextDocument()).asJson());
		}
		
		return notFoundEntity(ObjectUtils.toString(rank));
	}
	
	public static Result getAspects(String lexicon) {
		AspectLexicon lexiconObj = fetchResource(lexicon, AspectLexicon.class);
		List<AspectLexiconModel> aspects = Lists.newArrayList(Iterables.transform(lexiconObj.getAspects(),
			new Function<AspectLexicon, AspectLexiconModel>() {
				@Override
				@Nullable
				public AspectLexiconModel apply(@Nullable AspectLexicon input) {
					return (AspectLexiconModel)createViewModel(input);
				}
			}));
		
		return ok(Json.toJson(aspects));
	}

	public static Result getAspect(String lexicon, String aspect, boolean recursive) {
		AspectLexicon lexiconObj = null;
		AspectLexicon aspectObj = null;
		
		if (UuidUtils.isUuid(lexicon)) {
			lexiconObj = fetchResource(lexicon, AspectLexicon.class);
		}
		if (UuidUtils.isUuid(aspect)) {
			aspectObj = fetchResourceQuietly(aspect, AspectLexicon.class);
			if (aspectObj != null && lexiconObj != null && !ObjectUtils.equals(aspectObj.getParentAspect(), lexiconObj)) {
				throw new IllegalArgumentException();
			}
		}
		
		if (aspectObj == null && lexiconObj != null) {
			aspectObj = lexiconObj.findAspect(aspect, recursive);
		}
		
		if (aspectObj == null) {
			return notFoundEntity(aspect);
		}
		
		return ok(createViewModel(aspectObj).asJson());
	}
	
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	public static Result addAspect(String lexicon) {
		AspectLexicon lexiconObj = fetchResource(lexicon, AspectLexicon.class);
		JsonNode aspectJson = request().body().asJson();
		AspectLexiconModel aspect = aspectJson == null ?
			new AspectLexiconModel() : Json.fromJson(aspectJson, AspectLexiconModel.class);
		
		// if no title, generate an unused one.
		if (StringUtils.isEmpty(aspect.title)) {
			int count = 0;
			while (lexiconObj.hasAspect("Aspect " + ++count));
			aspect.title = "Aspect " + count;
		}
		
		AspectLexicon aspectObj = lexiconObj.addAspect(aspect.title);
		if (aspectObj == null) {
			throw new IllegalArgumentException();
		}
		
		em().persist(aspectObj);
		
		return created(createViewModel(aspectObj).asJson());
	}
	
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	public static Result updateExpression(String aspect, String expression) {
		AspectLexicon aspectObj = null;
		AspectExpression expressionObj = fetchResource(expression, AspectExpression.class);
		JsonNode updatedExpressionNode = request().body().asJson();
		
		if (StringUtils.isNotEmpty(aspect)) {
			aspectObj = fetchResource(aspect, AspectLexicon.class);
			if (!ObjectUtils.equals(expressionObj.getAspect(), aspectObj) && !aspectObj.migrateExpression(expressionObj)) {
				throw new IllegalArgumentException();
			}
		} else if (expressionObj.getAspect() != null) {
			aspectObj = expressionObj.getAspect();
		} else {
			throw new IllegalArgumentException();
		}
		
		if (updatedExpressionNode != null) {
			PersistentDocumentModel updatedExpression = Json.fromJson(updatedExpressionNode, PersistentDocumentModel.class);
			expressionObj = aspectObj.updateExpression(expressionObj.getContent(), updatedExpression.content);
			if (expressionObj == null) {
				throw new IllegalArgumentException();
			}
		}
		
		em().merge(expressionObj);
		return ok(createViewModel(expressionObj).asJson());
	}
	
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	public static Result updateAspect(String lexicon, String aspect) {
		AspectLexicon lexiconObj = null;
		AspectLexicon aspectObj = fetchResource(aspect, AspectLexicon.class);
		JsonNode updatedAspectNode = request().body().asJson();
		
		if (StringUtils.isNotEmpty(lexicon)) {
			lexiconObj = fetchResource(lexicon, AspectLexicon.class);
			
			if (!ObjectUtils.equals(aspectObj.getParentAspect(), lexiconObj) && !lexiconObj.migrateAspect(aspectObj)) {
				throw new IllegalArgumentException();
			}
		} else if (aspectObj.getParentAspect() != null) {
			lexiconObj = aspectObj.getParentAspect();
		} else {
			throw new IllegalArgumentException();
		}
		
		if (updatedAspectNode != null) {
			AspectLexiconModel updatedAspect = Json.fromJson(updatedAspectNode, AspectLexiconModel.class);
			aspectObj = lexiconObj.updateAspect(aspectObj.getTitle(), updatedAspect.title);
			if (aspectObj == null) {
				throw new IllegalArgumentException();
			}
		}
		
		em().merge(aspectObj);
		
		return ok(createViewModel(aspectObj).asJson());
	}
	
	public static Result deleteAspect(String lexicon, String aspect) {
		if (StringUtils.isNotEmpty(lexicon)) {
			AspectLexicon lexiconObj = fetchResource(lexicon, AspectLexicon.class);
			AspectLexicon aspectObj = fetchResource(aspect, AspectLexicon.class);
			if (aspectObj.getParentAspect() != null
				&& ObjectUtils.equals(aspectObj.getParentAspect(), lexiconObj)) {
				aspectObj = lexiconObj.removeAspect(aspectObj.getTitle());
				em().remove(aspectObj);
				
				return ok(createViewModel(aspectObj).asJson());
			} else {
				throw new IllegalArgumentException();
			}
		}
		
		return CollectionsController.delete(aspect);
	}
	
	public static Result getExpressions(String aspect) {
		AspectLexicon aspectObj = fetchResource(aspect, AspectLexicon.class);
		List<PersistentDocumentModel> expressions = Lists.newArrayList(Iterables.transform(aspectObj.getExpressions(),
			new Function<AspectExpression, PersistentDocumentModel>() {
				@Override
				@Nullable
				public PersistentDocumentModel apply(@Nullable AspectExpression input) {
					return (PersistentDocumentModel)createViewModel(input);
				}
			}));
		
		return ok(Json.toJson(expressions));
	}
	
	public static Result getExpression(String aspect, String expression, boolean recursive) {
		AspectLexicon aspectObj = null;
		AspectExpression expressionObj = null;
		
		if (UuidUtils.isUuid(aspect)) {
			aspectObj = fetchResource(aspect, AspectLexicon.class);
		}
		if (UuidUtils.isUuid(expression)) {
			expressionObj = fetchResourceQuietly(expression, AspectExpression.class);
			if (expressionObj != null && aspectObj != null && !ObjectUtils.equals(expressionObj.getAspect(), aspectObj)) {
				throw new IllegalArgumentException();
			}
		}
		
		if (expressionObj == null && aspectObj != null) {
			expressionObj = aspectObj.findExpression(expression, recursive);
		}
		
		if (expressionObj == null) {
			return notFoundEntity(expression);
		}
		return ok(createViewModel(expressionObj).asJson());
	}
	
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	public static Result addExpression(String aspect) {
		AspectLexicon aspectObj = fetchResource(aspect, AspectLexicon.class);
		JsonNode expressionJson = request().body().asJson();
		PersistentDocumentModel expression = expressionJson == null ?
			new PersistentDocumentModel() : Json.fromJson(expressionJson, PersistentDocumentModel.class);
		
		// if no content, generate an unused one.
		if (StringUtils.isEmpty(expression.content)) {
			int count = 0;
			while (aspectObj.hasExpression("Keyword " + ++count));
			expression.content = "Keyword " + count;
		}
		
		AspectExpression expressionObj = aspectObj.addExpression(expression.content);
		if (expressionObj == null) {
			throw new IllegalArgumentException();
		}
		
		em().persist(expressionObj);
		return created(createViewModel(expressionObj).asJson());
	}
	
	public static Result deleteExpression(String aspect, String expression) {
		AspectExpression expressionObj = fetchResource(expression, AspectExpression.class);
		if (StringUtils.isNotEmpty(aspect)) {
			AspectLexicon aspectObj = fetchResource(aspect, AspectLexicon.class);
			if (expressionObj.getAspect() != null
				&& ObjectUtils.equals(expressionObj.getAspect(), aspectObj)) {
				expressionObj = aspectObj.removeExpression(expressionObj.getContent());
			}
		}
		
		em().remove(expressionObj);
		return ok(createViewModel(expressionObj).asJson());
	}
}