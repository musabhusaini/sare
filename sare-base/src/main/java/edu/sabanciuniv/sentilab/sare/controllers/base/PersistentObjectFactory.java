package edu.sabanciuniv.sentilab.sare.controllers.base;

import java.util.Map.Entry;

import org.apache.commons.lang3.Validate;

import com.google.gson.*;

import edu.sabanciuniv.sentilab.core.controllers.factory.IFactory;
import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.models.base.*;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * The base class for all factories that create {@link PersistentObject} instances.
 * @author Mus'ab Husaini
 * @param <T> the type of object that will be created; must derive from {@link PersistentObject}.
 * @param <O> the type of options that will be used to create the objects; must derive from {@link PersistentObjectFactoryOptions}.
 */
public abstract class PersistentObjectFactory<T extends PersistentObject, O extends PersistentObjectFactoryOptions<T>>
	extends ControllerBase
	implements IFactory<T, O> {

	protected abstract T createPrivate(O options) throws IllegalFactoryOptionsException;
	
	@Override
	public T create(O options)
		throws IllegalFactoryOptionsException {
		
		try {
			Validate.notNull(options, CannedMessages.NULL_ARGUMENT, "options");
			
			T obj = this.createPrivate(options);
			
			// merge the other data.
			if (obj != null && options.getOtherData() != null) {
				JsonObject otherData = new JsonParser().parse(options.getOtherData()).getAsJsonObject();
				for (Entry<String, JsonElement> dataEntry : otherData.entrySet()) {
					if (!obj.getOtherData().has(dataEntry.getKey())) {
						obj.getOtherData().add(dataEntry.getKey(), dataEntry.getValue());
					}
				}
			}
			
			return obj;
		} catch (NullPointerException e) {
			throw new IllegalFactoryOptionsException(e);
		}
	}
}