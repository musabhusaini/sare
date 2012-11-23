package edu.sabanciuniv.sentilab.sare.models.factory.base;

import edu.sabanciuniv.sentilab.sare.controllers.factory.base.IFactory;
import edu.sabanciuniv.sentilab.sare.models.base.IModel;

/**
 * A class that extends this interface provides options for an implementation of the {@link IFactory} interface.
 * @author Mus'ab Husaini
 *
 * @param <T> the type of objects that can be created using these options.
 */
public interface IFactoryOptions<T>
	extends IModel {
}
