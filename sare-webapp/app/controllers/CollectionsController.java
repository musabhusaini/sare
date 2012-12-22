package controllers;

import static controllers.base.SareEntityEquippedAction.*;
import static controllers.base.SessionedAction.*;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.codehaus.jackson.JsonNode;

import com.google.common.collect.Iterables;

import models.documentStore.*;
import play.mvc.*;
import play.mvc.BodyParser.Json;
import play.mvc.Http.MultipartFormData;
import play.mvc.Http.MultipartFormData.FilePart;

import controllers.base.*;
import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.PersistenceDocumentStoreController;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionCorpusFactory;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

@With(SareEntityEquippedAction.class)
public class CollectionsController extends Application {

	public static Result list() {
		return ok(play.libs.Json.toJson(new PersistenceDocumentStoreController().getAllUuids(em(), getUsername())));
	}
	
	public static Result create() {
		OpinionCorpusFactoryOptions options = null;
		
		MultipartFormData formData = request().body().asMultipartFormData();
		if (formData != null) {
			// if we have a multi-part form with a file.
			if (formData.getFiles() != null) {
				// get either the file named "corpus" or the first one.
				FilePart filePart = ObjectUtils.defaultIfNull(formData.getFile("corpus"), Iterables.getFirst(formData.getFiles(), null));
				if (filePart != null) {
					options = new OpinionCorpusFactoryOptions()
						.setFile(filePart.getFile())
						.setFormat(FilenameUtils.getExtension(filePart.getFilename()));
				}
			}
		} else {
			// otherwise try as a json body.
			JsonNode json = request().body().asJson();
			if (json != null) {
				OpinionCorpusFactoryOptionsView viewModel = play.libs.Json.fromJson(json,
					OpinionCorpusFactoryOptionsView.class);
				if (viewModel != null) {
					options = viewModel.toFactoryOptions();
				} else {
					throw new IllegalArgumentException();
				}
			} else {
				// if not json, then treat the whole thing as a file.
				options = new OpinionCorpusFactoryOptions()
					.setFile(request().body().asRaw().asFile())
					.setFormat(request().getHeader(CONTENT_TYPE));
			}
		}
		
		if (options == null) {
			throw new IllegalArgumentException();
		}
		
		OpinionCorpusFactory corpusFactory = new OpinionCorpusFactory();
		options.setOwnerId(SessionedAction.getUsername(ctx()));
		OpinionCorpus corpus = corpusFactory.create(options);
		em().persist(corpus);
		
		return created(createViewModel(corpus).asJson());
	}
	
	public static Result get(String collection) {
		PersistentDocumentStore store = fetchResource(collection, PersistentDocumentStore.class);
		return ok(createViewModel(store).asJson());
	}
	
	@BodyParser.Of(Json.class)
	public static Result update(String collection) {
		PersistentDocumentStore collectionObj = fetchResource(collection, PersistentDocumentStore.class);
		PersistentDocumentStoreView collectionView = play.libs.Json.fromJson(request().body().asJson(), PersistentDocumentStoreView.class);
		
		if (collectionView.title != null) {
			collectionObj.setTitle(collectionView.title);
		}
		
		if (collectionView.description != null) {
			collectionObj.setDescription(collectionView.description);
		}
		
		if (collectionView.language != null) {
			collectionObj.setLanguage(collectionView.language);
		}
		
		em().merge(collectionObj);
		
		return ok(createViewModelQuietly(collectionObj).asJson());
	}
	
	public static Result delete(String collection) {
		PersistentDocumentStore collectionObj = fetchResource(collection, PersistentDocumentStore.class);
		em().remove(collectionObj);
		return ok(createViewModelQuietly(collectionObj).asJson());
	}
}