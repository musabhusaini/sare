/*
 * Sentilab SARE: a Sentiment Analysis Research Environment
 * Copyright (C) 2013 Sabanci University Sentilab
 * http://sentilab.sabanciuniv.edu
 * 
 * This file is part of SARE.
 * 
 * SARE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * SARE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sabanciuniv.sentilab.sare.controllers.base;

import java.util.Map.Entry;

import org.apache.commons.lang3.Validate;

import com.google.common.reflect.TypeToken;
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
	
	protected abstract T createPrivate(O options, T existing) throws IllegalFactoryOptionsException;

	@SuppressWarnings({ "unchecked", "serial" })
	@Override
	public T create(O options)
		throws IllegalFactoryOptionsException {
		
		try {
			Validate.notNull(options, CannedMessages.NULL_ARGUMENT, "options");
			
			T obj = null;
			if (options.getExistingId() != null && options.getEm() != null) {
				obj = (T)options.getEm().find(new TypeToken<T>(this.getClass()){}.getRawType(), options.getExistingId());
			}

			obj = this.createPrivate(options, obj);
			
			// merge the other data.
			if (obj != null && options.getOtherData() != null) {
				JsonObject otherData = new JsonParser().parse(options.getOtherData()).getAsJsonObject();
				for (Entry<String, JsonElement> dataEntry : otherData.entrySet()) {
					if (!obj.hasProperty(dataEntry.getKey())) {
						obj.setProperty(dataEntry.getKey(), dataEntry.getValue());
					}
				}
			}
			
			return obj;
		} catch (NullPointerException e) {
			throw new IllegalFactoryOptionsException(e);
		}
	}
}