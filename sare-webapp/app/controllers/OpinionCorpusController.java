package controllers;

import play.mvc.*;

import controllers.base.*;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

@With(SareEntityEquippedAction.class)
public class OpinionCorpusController extends AuthenticatedController {

	public static Result list(String id) {
		OpinionCorpus corpus = fetchResource(id, OpinionCorpus.class);
		
		if (corpus == null) {
			notFoundEntity(id);
		}
		
		return TODO;
	}
	
	public static Result delete(String id) {
		return TODO;
	}
	
	public static Result add() {
		return TODO;
	}
}
