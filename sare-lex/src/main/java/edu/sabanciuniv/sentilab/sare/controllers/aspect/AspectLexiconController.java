package edu.sabanciuniv.sentilab.sare.controllers.aspect;

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.base.documentStore.*;
import edu.sabanciuniv.sentilab.sare.models.aspect.*;

public class AspectLexiconController
	extends PersistentDocumentStoreFactory<AspectLexicon, AspectLexiconFactoryOptions>
	implements IDocumentStoreController {

	@Override
	protected AspectLexicon createPrivate(AspectLexiconFactoryOptions options)
		throws IllegalFactoryOptionsException {
		
		// TODO Auto-generated method stub
		return null;
	}
}