package edu.sabanciuniv.sentilab.core.models.factory;

import edu.sabanciuniv.sentilab.core.controllers.factory.IFactory;
import edu.sabanciuniv.sentilab.core.models.IModel;

/**
 * A class that extends this interface provides options for an implementation of the {@link IFactory} interface.
 * @author Mus'ab Husaini
 *
 * @param <T> the type of objects that can be created using these options.
 */
public interface IFactoryOptions<T>
	extends IModel {
}