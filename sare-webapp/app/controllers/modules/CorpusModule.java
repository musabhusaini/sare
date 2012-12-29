package controllers.modules;

import models.base.ViewModel;
import controllers.modules.base.Module;

@Module.Requires()
public class CorpusModule extends Module {

	@Override
	public String getDisplayName() {
		return "Corpus selection";
	}

	@Override
	public String getRoute(Iterable<ViewModel> viewModels) {
		// TODO Auto-generated method stub
		return null;
	}
}
