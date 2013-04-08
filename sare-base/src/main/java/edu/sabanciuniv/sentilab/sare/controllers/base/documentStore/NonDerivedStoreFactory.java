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

package edu.sabanciuniv.sentilab.sare.controllers.base.documentStore;

import java.io.*;
import java.util.zip.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathException;

import org.apache.commons.io.*;
import org.apache.commons.lang3.*;
import org.xml.sax.SAXException;

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * The base class for factories that create non-derived stores (meaning they can be created from files).
 * @author Mus'ab Husaini
 */
public abstract class NonDerivedStoreFactory<T extends PersistentDocumentStore>
		extends PersistentDocumentStoreFactory<T> {

	protected File file;
	protected String format;
	protected byte[] bytes;
	protected InputStream inputStream;
	protected String content;
	protected String textDelimiter;

	protected abstract NonDerivedStoreFactory<T> addXmlPacket(T store, InputStream input)
		throws ParserConfigurationException, SAXException, IOException, XPathException;
	
	protected abstract NonDerivedStoreFactory<T> addTextPacket(T store, InputStream input, String delimiter)
		throws IOException;
			
	protected NonDerivedStoreFactory<T> addZipPacket(T store, InputStream input)
		throws IOException {
		
		Validate.notNull(store, CannedMessages.NULL_ARGUMENT, "store");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		ZipInputStream zipStream = new ZipInputStream(input);
		ZipEntry zipEntry;
		while ((zipEntry = zipStream.getNextEntry()) != null) {
			if (!zipEntry.isDirectory()) {
				// we create a byte stream so that the input stream is not closed by the underlying methods.
				this.createSpecific(store,
					new ByteArrayInputStream(IOUtils.toByteArray(zipStream)), FilenameUtils.getExtension(zipEntry.getName()));
			}
		}
		
		return this;
	}
		
	protected T createSpecific(T store, InputStream input, String format)
		throws IOException {
		
		Validate.notNull(store, CannedMessages.NULL_ARGUMENT, "store");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		switch(format) {
		case "text/xml":
		case "xml":
			try {
				this.addXmlPacket(store, input);
			} catch (ParserConfigurationException | SAXException | XPathException e) {
				throw new IOException("error reading input", e);
			}
			break;
		case "application/zip":
		case "zip":
			this.addZipPacket(store, input);
			break;
		case "text/plain":
		case "text":
		case "txt":
		case "text/csv":
		case "csv":
			this.addTextPacket(store, input, this.getTextDelimiter());
			break;
		default:
			throw new IllegalFactoryOptionsException("unsupported format " + format);
		}
		
		return store;
	}
	
	protected T createSpecific(T store, byte[] input, String format)
		throws IOException {
		
		Validate.notNull(store, CannedMessages.NULL_ARGUMENT, "store");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		try {
			return this.createSpecific(store, new ByteArrayInputStream(input), format);
		} catch (IOException e) {
			// unlikely that this will ever happen since we're using a byte stream.
			throw new IOException("error reading input", e);
		}
	}
	
	protected T createSpecific(T store, File input, String format)
		throws IOException {
		
		Validate.notNull(store, CannedMessages.NULL_ARGUMENT, "store");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		InputStream stream = FileUtils.openInputStream(input);
		this.createSpecific(store, stream, format);
		stream.close();
		return store;
	}

	protected abstract T createNew();
	
	@Override
	protected T createPrivate(T store)
		throws IllegalFactoryOptionsException {
		
		if (store == null) {
			store = this.createNew();
		}
		
		String format = StringUtils.isNotEmpty(this.getFormat()) ? this.getFormat() :
			(this.getFile() != null ? FilenameUtils.getExtension(this.getFile().getPath()) : null);
		
		try {
			if (this.getContent() != null) {
				Validate.notNull(format, CannedMessages.EMPTY_ARGUMENT, "options.format");
				
				this.createSpecific(store, this.getContent().getBytes(CharEncoding.UTF_8), this.getFormat());
			} else if (this.getBytes() != null) {
				Validate.notNull(format, CannedMessages.EMPTY_ARGUMENT, "options.format");
				
				this.createSpecific(store, this.getBytes(), format);
			} else if (this.getInputStream() != null) {
				Validate.notNull(format, CannedMessages.EMPTY_ARGUMENT, "options.format");
				
				this.createSpecific(store, this.getInputStream(), format);
			} else if (this.getFile() != null) {
				this.createSpecific(store, this.getFile(), format);
			}
		} catch (IOException e) {
			throw new IllegalFactoryOptionsException("there was an error reading the input", e);
		}
		
		return store;
	}

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
	public NonDerivedStoreFactory<T> setFile(File file) {
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
	public NonDerivedStoreFactory<T> setFormat(String format) {
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
	public NonDerivedStoreFactory<T> setBytes(byte[] bytes) {
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
	public NonDerivedStoreFactory<T> setInputStream(InputStream inputStream) {
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
	public NonDerivedStoreFactory<T> setContent(String content) {
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
	public NonDerivedStoreFactory<T> setTextDelimiter(String textDelimiter) {
		this.textDelimiter = textDelimiter;
		return this;
	}
}