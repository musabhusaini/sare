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

package edu.sabanciuniv.sentilab.sare.models.base.documentStore;

import java.io.File;
import java.io.InputStream;

import edu.sabanciuniv.sentilab.core.models.factory.IFactoryOptions;

/**
 * Base class for all {@link IFactoryOptions} that can create non-derived {@link PersistentDocumentStore}.
 * @author Mus'ab Husaini
 * @param <T> the type of objects that will be created; must extend {@link PersistentDocumentStore}.
 */
public abstract class NonDerivedStoreFactoryOptions<T extends PersistentDocumentStore>
	extends PersistentDocumentStoreFactoryOptions<T> {

	private File file;
	private String format;
	private byte[] bytes;
	private InputStream inputStream;
	private String content;
	private String textDelimiter;

	/**
	 * Gets the file to read the store contents from.
	 * @return the {@link File} object identifying the location where the store will be created from.
	 */
	public File getFile() {
		return this.file;
	}
	
	/**
	 * Sets the file to read the store contents from.
	 * @param file the {@link File} object identifying the location to read the store from.
	 * @return the {@code this} object.
	 */
	public NonDerivedStoreFactoryOptions<T> setFile(File file) {
		this.file = file;
		return this;
	}
	
	/**
	 * Gets the format of the input.
	 * @return the format of the input (typically the extension of the file).
	 */
	public String getFormat() {
		return this.format;
	}
	
	/**
	 * Sets the format of the input.
	 * @param format the format of the input (typically the extension of the file).
	 * @return the {@code this} object.
	 */
	public NonDerivedStoreFactoryOptions<T> setFormat(String format) {
		this.format = format;
		return this;
	}
	
	/**
	 * Gets the byte array containing the input to create the store from.
	 * @return the {@link Byte} array containing the input.
	 */
	public byte[] getBytes() {
		return this.bytes;
	}
	
	/**
	 * Sets the byte array containing the input to create the store from.
	 * @param bytes the {@link Byte} array containing the input.
	 * @return the {@code this} object.
	 */
	public NonDerivedStoreFactoryOptions<T> setBytes(byte[] bytes) {
		this.bytes = bytes;
		return this;
	}
	
	/**
	 * Gets the input stream where the store to be created can be read from.
	 * @return the {@link InputStream} where the store can be created from.
	 */
	public InputStream getInputStream() {
		return this.inputStream;
	}
	
	/**
	 * Sets the input stream where the store to be created can be read from.
	 * @param inputStream the {@link InputStream} where the store can be created from.
	 * @return the {@code this} object.
	 */
	public NonDerivedStoreFactoryOptions<T> setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		return this;
	}

	/**
	 * Gets the content where the store can be created from.
	 * @return the content of the store.
	 */
	public String getContent() {
		return this.content;
	}
	
	/*
	 * Sets the content of the store to be created.
	 * @param content of the store to set.
	 * @return the {@code this} object.
	 */
	public NonDerivedStoreFactoryOptions<T> setContent(String content) {
		this.content = content;
		return this;
	}
	
	/**
	 * Gets the delimiter used when reading text files.
	 * @return the string representing the delimiter, if any.
	 */
	public String getTextDelimiter() {
		return this.textDelimiter;
	}

	/**
	 * Sets the delimiter to be used when reading text files.
	 * @param textDelimiter the delimiter to use.
	 * @return the {@code this} object.
	 */
	public NonDerivedStoreFactoryOptions<T> setTextDelimiter(String textDelimiter) {
		this.textDelimiter = textDelimiter;
		return this;
	}
}
