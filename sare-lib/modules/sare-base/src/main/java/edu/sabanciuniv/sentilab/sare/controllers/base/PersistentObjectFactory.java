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

import java.util.UUID;
import java.util.Map.Entry;

import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;

import edu.sabanciuniv.sentilab.core.controllers.factory.IFactory;
import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.models.base.*;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

/**
 * The base class for all factories that create {@link PersistentObject} instances.
 * @author Mus'ab Husaini
 * @param <T> the type of object that will be created; must derive from {@link PersistentObject}.
 */
public abstract class PersistentObjectFactory<T extends PersistentObject>
		extends ControllerBase implements IFactory<T> {
	
	protected byte[] existingId;
	protected String otherData;
	protected EntityManager em;

	/**
	 * Gets the ID of an existing object to be modified.
	 * @return the ID to fetch the object with.
	 */
	public byte[] getExistingId() {
		return this.existingId;
	}

	/**
	 * Sets the ID of an existing object to be modified. Must also provide a non-{@code null} value for {@code em}
	 * if this is {@code null}
	 * @param id the ID to fetch the object with.
	 * @return the {@code this} object.
	 */
	public PersistentObjectFactory<T> setExistingId(byte[] id) {
		this.existingId = id;
		return this;
	}

	/**
	 * Sets the ID of an existing object to be modified. Must also provide a non-{@code null} value for {@code em}
	 * if this is {@code null}
	 * @param id the ID to fetch the object with.
	 * @return the {@code this} object.
	 */
	public PersistentObjectFactory<T> setExistingId(String id) {
		if (StringUtils.isEmpty(id)) {
			return this;
		}

		return this.setExistingId(UuidUtils.toBytes(id));
	}

	/**
	 * Sets the ID of an existing object to be modified. Must also provide a non-{@code null} value for {@code em}
	 * if this is {@code null}
	 * @param id the ID to fetch the object with.
	 * @return the {@code this} object.
	 */
	public PersistentObjectFactory<T> setExistingId(UUID id) {
		if (id == null) {
			return this;
		}

		return this.setExistingId(UuidUtils.toBytes(id));
	}

	/**
	 * Gets any other data to be attached to the target object.
	 * @return the {@link String} representing other data.
	 */
	public String getOtherData() {
		return this.otherData;
	}

	/**
	 * Sets any other data to attach to the target object.
	 * @param otherData the {@link String} representing any other data (must be valid JSON).
	 * @return the {@code this} object.
	 * @throws IllegalArgumentException when the argument cannot be parsed as a JSON object.
	 */
	public PersistentObjectFactory<T> setOtherData(String otherData) {
		if (otherData != null) {
			try {
				if (!new JsonParser().parse(otherData).isJsonObject()) {
					throw new JsonSyntaxException("");
				}
			} catch (JsonSyntaxException e) {
				throw new IllegalArgumentException("argument 'otherData' must be valid JSON", e);
			}
		}

		this.otherData = otherData;
		return this;
	}

	/**
	 * Gets an entity manager that will be used in case an ID was provided.
	 * @return the {@link EntityManager} that will be used.
	 */
	public EntityManager getEm() {
		return this.em;
	}

	/**
	 * Sets the entity manager to use in case an ID was provided. Only needed if an existing ID was provided.
	 * @param em the {@link EntityManager} to be set.
	 * @return the {@code this} object.
	 */
	public PersistentObjectFactory<T> setEm(EntityManager em) {
		this.em = em;
		return this;
	}

	protected abstract T createPrivate(T existing) throws IllegalFactoryOptionsException;

	@SuppressWarnings({ "unchecked", "serial" })
	@Override
	public T create()
		throws IllegalFactoryOptionsException {
		
		try {
			T obj = null;
			if (this.getExistingId() != null && this.getEm() != null) {
				obj = (T)this.getEm().find(new TypeToken<T>(this.getClass()){}.getRawType(), this.getExistingId());
			}

			obj = this.createPrivate(obj);
			
			// merge the other data.
			if (obj != null && this.getOtherData() != null) {
				JsonObject otherData = new JsonParser().parse(this.getOtherData()).getAsJsonObject();
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