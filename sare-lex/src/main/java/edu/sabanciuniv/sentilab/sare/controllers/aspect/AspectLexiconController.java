package edu.sabanciuniv.sentilab.sare.controllers.aspect;

import edu.sabanciuniv.sentilab.core.controllers.factory.IFactory;
import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.base.documentStore.DocumentStoreController;
import edu.sabanciuniv.sentilab.sare.models.aspect.*;

public class AspectLexiconController extends DocumentStoreController
	implements IFactory<AspectLexicon, AspectLexiconFactoryOptions> {

	@Override
	public AspectLexicon create(AspectLexiconFactoryOptions options)
		throws IllegalFactoryOptionsException {
		
		// TODO Auto-generated method stub
		return null;
	}
}