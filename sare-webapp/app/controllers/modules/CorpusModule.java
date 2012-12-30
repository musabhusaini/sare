package controllers.modules;

import static controllers.base.SessionedAction.*;
import static controllers.base.SareTransactionalAction.*;

import java.util.*;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.*;

import play.mvc.*;
import views.html.corpusSelection;
import models.base.*;
import models.documentStore.PersistentDocumentStoreView;
import controllers.base.SareTransactionalAction;
import controllers.modules.base.Module;
import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.PersistentDocumentStoreController;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;

@With(SareTransactionalAction.class)
@Module.Requires
public class CorpusModule extends Module {

	@Override
	public String getDisplayName() {
		return "Corpus selection";
	}

	@Override
	public String getRoute(Iterable<ViewModel> viewModels) {
		return controllers.modules.routes.CorpusModule.selection().url();
	}
	
	public static Result selection() {
		PersistentDocumentStoreController docStoreController = new PersistentDocumentStoreController();
		List<String> uuids = docStoreController.getAllUuids(em(), getUsername());
		List<PersistentDocumentStoreView> stores = Lists.transform(uuids,
			new Function<String, PersistentDocumentStoreView>() {
				@Override
				@Nullable
				public PersistentDocumentStoreView apply(@Nullable String input) {
					return new PersistentDocumentStoreView(fetchResource(input, PersistentDocumentStore.class));
				}
			});
		
		return ok(corpusSelection.render(stores));
	}
}