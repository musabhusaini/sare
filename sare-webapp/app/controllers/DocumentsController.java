package controllers;

import static edu.sabanciuniv.sentilab.sare.models.base.UniquelyIdentifiableObject.*;
import static controllers.base.SareTransactionalAction.*;
import models.document.OpinionDocumentView;

import play.mvc.*;
import play.mvc.BodyParser.Json;

import controllers.base.*;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.PersistentDocumentController;
import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionDocumentFactory;
import edu.sabanciuniv.sentilab.sare.models.base.document.PersistentDocument;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

@With(SareTransactionalAction.class)
public class DocumentsController extends Application {

	private static PersistentDocument fetchDocument(String collection, String document) {
		PersistentDocument documentObj = fetchResource(document, PersistentDocument.class);
		if (!normalizeUuidString(collection).equals(normalizeUuidString(documentObj.getStore().getIdentifier()))) {
			throw new IllegalArgumentException();
		}
		return documentObj;
	}
	
	public static Result list(String collection) {
		return ok(play.libs.Json.toJson(new PersistentDocumentController().getAllUuids(em(), collection)));
	}
	
	@BodyParser.Of(Json.class)
	public static Result add(String collection) {
		PersistentDocumentStore store = fetchResource(collection, PersistentDocumentStore.class);
		
		if (store instanceof OpinionCorpus) {
			OpinionDocumentView viewModel = play.libs.Json.fromJson(request().body().asJson(), OpinionDocumentView.class);
			OpinionDocumentFactoryOptions options = new OpinionDocumentFactoryOptions()
				.setContent(viewModel.content)
				.setPolarity(viewModel.polarity)
				.setCorpus((OpinionCorpus)store);
			
			OpinionDocument document = new OpinionDocumentFactory().create(options);
			em().persist(document);
			
			return created(createViewModel(document).asJson());
		}
		
		return badRequest();
	}
	
	public static Result get(String collection, String document) {
		return ok(createViewModel(fetchDocument(collection, document)).asJson());
	}
	
	public static Result delete(String collection, String document) {
		PersistentDocument documentObj = fetchDocument(collection, document);
		em().remove(documentObj);
		return ok(createViewModel(documentObj).asJson());
	}
	
	public static Result update(String collection, String document) {
		PersistentDocument documentObj = fetchDocument(collection, document);
		
		if (documentObj instanceof OpinionDocument) {
			OpinionDocument opinionDocument = (OpinionDocument)documentObj;
			OpinionDocumentView viewModel = play.libs.Json.fromJson(request().body().asJson(), OpinionDocumentView.class);
			if (viewModel.content != null) {
				opinionDocument.setContent(viewModel.content);
			}
			if (viewModel.polarity != null) {
				opinionDocument.setPolarity(viewModel.polarity);
			}
			em().merge(opinionDocument);
			
			return ok(createViewModel(opinionDocument).asJson());
		}
		
		return badRequest();
	}
}