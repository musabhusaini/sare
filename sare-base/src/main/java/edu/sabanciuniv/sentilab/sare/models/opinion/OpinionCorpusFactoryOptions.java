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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sabanciuniv.sentilab.sare.models.opinion;

import java.io.*;

import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStoreFactoryOptions;

/**
 * The default set of options that can be used to construct an {@link OpinionCorpus} object.
 * The most specific combination of properties will be used.
 * @author Mus'ab Husaini
 */
public class OpinionCorpusFactoryOptions extends
	PersistentDocumentStoreFactoryOptions<OpinionCorpus> {

	private String title;
	private String description;
	private String language;
	private File file;
	private String format;
	private byte[] bytes;
	private InputStream inputStream;
	private String content;
	private String textDelimiter;
	
	/**
	 * Gets the title of the corpus to be created.
	 * @return the title of the corpus.
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Sets the title of the corpus to create.
	 * @param title the title of the corpus to set.
	 * @return the {@code this} object.
	 */
	public OpinionCorpusFactoryOptions setTitle(String title) {
		this.title = title;
		return this;
	}
	
	/**
	 * Gets the description of the corpus to be created.
	 * @return the description of the corpus.
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Sets the description of the corpus to create.
	 * @param description the description of the corpus to be set.
	 * @return the {@code this} object.
	 */
	public OpinionCorpusFactoryOptions setDescription(String description) {
		this.description = description;
		return this;
	}
	
	/**
	 * Gets the language of the corpus to be created.
	 * @return the language of the corpus.
	 */
	public String getLanguage() {
		return this.language;
	}
	
	/**
	 * Sets the language of the corpus to create.
	 * @param language the language of the corpus to be set.
	 * @return the {@code this} object.
	 */
	public OpinionCorpusFactoryOptions setLanguage(String language) {
		this.language = language;
		return this;
	}
	
	/**
	 * Gets the file to read the corpus contents from.
	 * @return the {@link File} object identifying the location where the corpus will be created from.
	 */
	public File getFile() {
		return this.file;
	}
	
	/**
	 * Sets the file to read the corpus contents from.
	 * @param file the {@link File} object identifying the location to read the corpus from.
	 * @return the {@code this} object.
	 */
	public OpinionCorpusFactoryOptions setFile(File file) {
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
	public OpinionCorpusFactoryOptions setFormat(String format) {
		this.format = format;
		return this;
	}
	
	/**
	 * Gets the byte array containing the input to create the corpus from.
	 * @return the {@link Byte} array containing the input.
	 */
	public byte[] getBytes() {
		return this.bytes;
	}
	
	/**
	 * Sets the byte array containing the input to create the corpus from.
	 * @param bytes the {@link Byte} array containing the input.
	 * @return the {@code this} object.
	 */
	public OpinionCorpusFactoryOptions setBytes(byte[] bytes) {
		this.bytes = bytes;
		return this;
	}
	
	/**
	 * Gets the input stream where the corpus to be created can be read from.
	 * @return the {@link InputStream} where the corpus can be created from.
	 */
	public InputStream getInputStream() {
		return this.inputStream;
	}
	
	/**
	 * Sets the input stream where the corpus to be created can be read from.
	 * @param inputStream the {@link InputStream} where the corpus can be created from.
	 * @return the {@code this} object.
	 */
	public OpinionCorpusFactoryOptions setInputStream(InputStream inputStream) {
		this.inputStream = inputStream;
		return this;
	}

	/**
	 * Gets the content where the corpus can be created from.
	 * @return the content of the corpus.
	 */
	public String getContent() {
		return this.content;
	}
	
	/*
	 * Sets the content of the corpus to be created.
	 * @param content of the corpus to set.
	 * @return the {@code this} object.
	 */
	public OpinionCorpusFactoryOptions setContent(String content) {
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
	public OpinionCorpusFactoryOptions setTextDelimiter(String textDelimiter) {
		this.textDelimiter = textDelimiter;
		return this;
	}
}