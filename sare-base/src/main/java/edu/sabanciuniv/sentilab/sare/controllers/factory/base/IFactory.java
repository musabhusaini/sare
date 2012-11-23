package edu.sabanciuniv.sentilab.sare.controllers.factory.base;

import edu.sabanciuniv.sentilab.sare.controllers.base.IController;
import edu.sabanciuniv.sentilab.sare.models.factory.base.IFactoryOptions;
import edu.sabanciuniv.sentilab.sare.models.factory.base.IllegalFactoryOptionsException;

/**
 * A class that extends this interface provides a method to create objects of a certain type.
 * @author Mus'ab Husaini
 *
 * @param <T> the type of objects created by this implementation.
 * @param <O> the type of options accepted by this factory; must implement {@link IFactoryOptions}.
 */
public interface IFactory<T, O extends IFactoryOptions<T>>
	extends IController {

	/**
	 * Creates an object of type {@code T}.
	 * @param options the options to use for creating the desired object.
	 * @return the created object.
	 * @throws IllegalFactoryOptionsException when the options are not sufficient to create the object.
	 */
	public T create(O options) throws IllegalFactoryOptionsException;
}