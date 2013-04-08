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

import static models.base.ViewModel.*;
import static controllers.base.SessionedAction.*;
import static controllers.base.SareTransactionalAction.*;

import java.util.*;

import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.*;
import org.codehaus.jackson.JsonNode;

import com.google.common.base.Function;
import com.google.common.collect.*;

import play.*;
import play.libs.Json;
import play.mvc.*;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;
import twitter4j.*;
import twitter4j.auth.AccessToken;
import views.html.tags.*;
import models.document.OpinionDocumentModel;
import models.documentStore.*;
import controllers.DocumentsController;
import controllers.base.*;
import controllers.modules.base.Module;
import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.PersistentDocumentStoreController;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.*;
import edu.sabanciuniv.sentilab.sare.models.base.PersistentObject;
import edu.sabanciuniv.sentilab.sare.models.base.document.FullTextDocument;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

@With(SareTransactionalAction.class)
@Module.Requires
public class CorpusModule extends Module {

	@Override
	public UUID getId() {
		return UuidUtils.create("617bff47465a4a7d8e38cfcb841bccf0");
	}
	
	@Override
	public String getDisplayName() {
		return "Select a corpus";
	}

	@Override
	public String getRoute() {
		return controllers.modules.routes.CorpusModule.modulePage(false).url();
	}
	
	public static List<PersistentDocumentStoreModel> getCorpora() {
		PersistentDocumentStoreController docStoreController = new PersistentDocumentStoreController();
		return Lists.transform(docStoreController.getAllUuids(em(), getUsername(), DocumentCorpus.class),
			new Function<String, PersistentDocumentStoreModel>() {
				@Override
				@Nullable
				public PersistentDocumentStoreModel apply(@Nullable String input) {
					PersistentDocumentStore store = fetchResource(UuidUtils.create(input), PersistentDocumentStore.class);
					PersistentDocumentStoreModel storeVM = (PersistentDocumentStoreModel)createViewModel(store);
					storeVM.populateSize(em(), store);
					return storeVM;
				}
			});
	}
		
	public static Result modulePage(boolean partial) {
		return moduleRender(new CorpusModule(), storeList.render(getCorpora(), true, "Select a corpus"), partial);
	}
		
	public static Result create() {
		return update(null);
	}
	
	public static Result update(UUID corpus) {
		OpinionCorpus corpusObj = null;
		if (corpus != null) {
			corpusObj = fetchResource(corpus, OpinionCorpus.class);
		}
		OpinionCorpusFactory corpusFactory = null;
		
		MultipartFormData formData = request().body().asMultipartFormData();
		if (formData != null) {
			// if we have a multi-part form with a file.
			if (formData.getFiles() != null) {
				// get either the file named "file" or the first one.
				FilePart filePart = ObjectUtils.defaultIfNull(formData.getFile("file"),
					Iterables.getFirst(formData.getFiles(), null));
				if (filePart != null) {
					corpusFactory = (OpinionCorpusFactory)new OpinionCorpusFactory()
						.setFile(filePart.getFile())
						.setFormat(FilenameUtils.getExtension(filePart.getFilename()));
				}
			}
		} else {
			// otherwise try as a json body.
			JsonNode json = request().body().asJson();
			if (json != null) {
				OpinionCorpusFactoryModel optionsVM = Json.fromJson(json, OpinionCorpusFactoryModel.class);
				if (optionsVM != null) {
					corpusFactory = optionsVM.toFactory();
				} else {
					throw new IllegalArgumentException();
				}
				
				if (optionsVM.grabbers != null) {
					if (optionsVM.grabbers.twitter != null) {
						if (StringUtils.isNotBlank(optionsVM.grabbers.twitter.query)) {
							TwitterFactory tFactory = new TwitterFactory();
							Twitter twitter = tFactory.getInstance();
							twitter.setOAuthConsumer(
								Play.application().configuration().getString("twitter4j.oauth.consumerKey"),
								Play.application().configuration().getString("twitter4j.oauth.consumerSecret"));
							twitter.setOAuthAccessToken(
								new AccessToken(Play.application().configuration().getString("twitter4j.oauth.accessToken"),
									Play.application().configuration().getString("twitter4j.oauth.accessTokenSecret")));
							
							Query query = new Query(optionsVM.grabbers.twitter.query);
							query.count(ObjectUtils.defaultIfNull(optionsVM.grabbers.twitter.limit, 10));
							query.resultType(Query.RECENT);
							if (StringUtils.isNotEmpty(corpusFactory.getLanguage())) {
								query.lang(corpusFactory.getLanguage());
							} else if (corpusObj != null) {
								query.lang(corpusObj.getLanguage());
							}
							
							QueryResult qr;
							try {
								qr = twitter.search(query);
							} catch (TwitterException e) {
								throw new IllegalArgumentException();
							}
							
							StringBuilder tweets = new StringBuilder();
							for (twitter4j.Status status : qr.getTweets()) {
								// quote for csv, normalize space, and remove higher unicode characters. 
								String text = StringEscapeUtils.escapeCsv(
									StringUtils.normalizeSpace(
										status.getText().replaceAll("[^\\u0000-\uFFFF]", "�")));
								tweets.append(text + System.lineSeparator());
							}
							
							corpusFactory.setContent(tweets.toString());
							corpusFactory.setFormat("txt");
						}
					}
				}
			} else {
				// if not json, then just create empty.
				corpusFactory = new OpinionCorpusFactory();
			}
		}
		
		if (corpusFactory == null) {
			throw new IllegalArgumentException();
		}
		
		if (corpus == null && StringUtils.isEmpty(corpusFactory.getTitle())) {
			corpusFactory.setTitle("Untitled corpus");
		}
		
		corpusFactory
			.setOwnerId(SessionedAction.getUsername(ctx()))
			.setExistingId(corpus)
			.setEm(em());
		
		DocumentCorpusModel corpusVM = null;
		corpusObj = corpusFactory.create();
		if (!em().contains(corpusObj)) {
			em().persist(corpusObj);
			
			corpusVM = (DocumentCorpusModel)createViewModel(corpusObj);
			corpusVM.populateSize(em(), corpusObj);
			return created(corpusVM.asJson());
		}
		
		for (PersistentObject obj : corpusObj.getDocuments()) {
			if (em().contains(obj)) {
				em().merge(obj);
			} else {
				em().persist(obj);
			}
		}
		em().merge(corpusObj);
		
		corpusVM = (DocumentCorpusModel)createViewModel(corpusObj);
		corpusVM.populateSize(em(), corpusObj);
		return ok(corpusVM.asJson());
	}
	
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	public static Result addDocument(UUID corpus) {
		return updateDocument(corpus, null);
	}
	
	@BodyParser.Of(play.mvc.BodyParser.Json.class)
	public static Result updateDocument(UUID corpus, UUID document) {
		DocumentCorpus store = fetchResource(corpus, DocumentCorpus.class);
		
		if (store instanceof OpinionCorpus) {
			OpinionDocumentModel viewModel = Json.fromJson(request().body().asJson(), OpinionDocumentModel.class);
			OpinionDocumentFactory documentFactory = (OpinionDocumentFactory)new OpinionDocumentFactory()
				.setContent(viewModel.content)
				.setPolarity(viewModel.polarity)
				.setCorpus((OpinionCorpus)store)
				.setEm(em())
				.setExistingId(document);
			
			OpinionDocument documentObj = documentFactory.create();
			if (em().contains(documentObj)) {
				em().merge(documentObj);
			} else {
				em().persist(documentObj);
			}
			
			return created(createViewModel(documentObj).asJson());
		}
		
		return badRequest();
	}
	
	public static Result deleteDocument(UUID corpus, UUID document) {
		FullTextDocument documentObj = DocumentsController.fetchDocument(corpus, document, FullTextDocument.class);
		em().remove(documentObj);
		return ok(createViewModel(documentObj).asJson());
	}
	
	public static Result twitterGrabberView(UUID corpus) {
		DocumentCorpus corpusObj = fetchResource(corpus, DocumentCorpus.class);
		DocumentCorpusModel corpusVM = (DocumentCorpusModel)createViewModel(corpusObj);
		corpusVM.populateSize(em(), corpusObj);
		return ok(twitterGrabber.render(corpusVM));
	}
}