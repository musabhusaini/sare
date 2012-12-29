package controllers.modules;

import play.mvc.Result;
import views.html.corpusSelection;
import models.base.ViewModel;
import controllers.modules.base.Module;

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
		return ok(corpusSelection.render());
	}
}
