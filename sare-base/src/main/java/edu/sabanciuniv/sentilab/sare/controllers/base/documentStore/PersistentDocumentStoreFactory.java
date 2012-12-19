package edu.sabanciuniv.sentilab.sare.controllers.base.documentStore;

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.base.PersistentObjectFactory;
import edu.sabanciuniv.sentilab.sare.controllers.base.document.IDocumentController;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;

/**
 * The base class for all factories that create {@link PersistentDocumentStore} instances.
 * @author Mus'ab Husaini
 * @param <T> the type of object that will be created; must derive from {@link PersistentDocumentStore}.
 * @param <O> the type of options that will be used to create the objects; must derive from {@link PersistentDocumentStoreFactoryOptions}.
 */
public abstract class PersistentDocumentStoreFactory<T extends PersistentDocumentStore, O extends PersistentDocumentStoreFactoryOptions<T>>
	extends PersistentObjectFactory<T, O> implements IDocumentController {
	
	@Override
	public T create(O options)
		throws IllegalFactoryOptionsException {
		
		T obj = super.create(options);
		obj.setOwnerId(options.getOwnerId());
		return obj;
	}
}