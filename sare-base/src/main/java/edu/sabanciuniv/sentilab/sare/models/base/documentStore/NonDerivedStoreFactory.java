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

import java.io.*;
import java.util.zip.*;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathException;

import org.apache.commons.io.*;
import org.apache.commons.lang3.*;
import org.xml.sax.SAXException;

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.base.documentStore.PersistentDocumentStoreFactory;
import edu.sabanciuniv.sentilab.utils.CannedMessages;

/**
 * The base class for factories that create non-derived stores (meaning they can be created from files).
 * @author Mus'ab Husaini
 */
public abstract class NonDerivedStoreFactory<T extends PersistentDocumentStore, O extends NonDerivedStoreFactoryOptions<T>>
	extends PersistentDocumentStoreFactory<T, O> {

	protected abstract NonDerivedStoreFactory<T, O> addXmlPacket(T store, InputStream input, O options)
		throws ParserConfigurationException, SAXException, IOException, XPathException;
	
	protected abstract NonDerivedStoreFactory<T, O> addTextPacket(T store, InputStream input, String delimiter, O options)
		throws IOException;
			
	protected NonDerivedStoreFactory<T, O> addZipPacket(T store, InputStream input, O options)
		throws IOException {
		
		Validate.notNull(store, CannedMessages.NULL_ARGUMENT, "store");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		ZipInputStream zipStream = new ZipInputStream(input);
		ZipEntry zipEntry;
		while ((zipEntry = zipStream.getNextEntry()) != null) {
			if (!zipEntry.isDirectory()) {
				// we create a byte stream so that the input stream is not closed by the underlying methods.
				this.createSpecific(store,
					new ByteArrayInputStream(IOUtils.toByteArray(zipStream)), FilenameUtils.getExtension(zipEntry.getName()), options);
			}
		}
		
		return this;
	}
		
	protected T createSpecific(T store, InputStream input, String format, O options)
		throws IOException {
		
		Validate.notNull(store, CannedMessages.NULL_ARGUMENT, "store");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		switch(format) {
		case "text/xml":
		case "xml":
			try {
				this.addXmlPacket(store, input, options);
			} catch (ParserConfigurationException | SAXException | XPathException e) {
				throw new IOException("error reading input", e);
			}
			break;
		case "application/zip":
		case "zip":
			this.addZipPacket(store, input, options);
			break;
		case "text/plain":
		case "text":
		case "txt":
		case "text/csv":
		case "csv":
			this.addTextPacket(store, input, options.getTextDelimiter(), options);
			break;
		default:
			throw new IllegalFactoryOptionsException("unsupported format " + format);
		}
		
		return store;
	}
	
	protected T createSpecific(T store, byte[] input, String format, O options)
		throws IOException {
		
		Validate.notNull(store, CannedMessages.NULL_ARGUMENT, "store");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		try {
			return this.createSpecific(store, new ByteArrayInputStream(input), format, options);
		} catch (IOException e) {
			// unlikely that this will ever happen since we're using a byte stream.
			throw new IOException("error reading input", e);
		}
	}
	
	protected T createSpecific(T store, File input, String format, O options)
		throws IOException {
		
		Validate.notNull(store, CannedMessages.NULL_ARGUMENT, "store");
		Validate.notNull(input, CannedMessages.NULL_ARGUMENT, "input");
		
		InputStream stream = FileUtils.openInputStream(input);
		this.createSpecific(store, stream, format, options);
		stream.close();
		return store;
	}

	protected abstract T createNew();
	
	@Override
	protected T createPrivate(O options, T store)
		throws IllegalFactoryOptionsException {
		
		if (store == null) {
			store = this.createNew();
		}
		
		String format = StringUtils.isNotEmpty(options.getFormat()) ? options.getFormat() :
			(options.getFile() != null ? FilenameUtils.getExtension(options.getFile().getPath()) : null);
		
		try {
			if (options.getContent() != null) {
				Validate.notNull(format, CannedMessages.EMPTY_ARGUMENT, "options.format");
				
				this.createSpecific(store, IOUtils.toInputStream(options.getContent()), options.getFormat(), options);
			} else if (options.getBytes() != null) {
				Validate.notNull(format, CannedMessages.EMPTY_ARGUMENT, "options.format");
				
				this.createSpecific(store, options.getBytes(), format, options);
			} else if (options.getInputStream() != null) {
				Validate.notNull(format, CannedMessages.EMPTY_ARGUMENT, "options.format");
				
				this.createSpecific(store, options.getInputStream(), format, options);
			} else if (options.getFile() != null) {
				this.createSpecific(store, options.getFile(), format, options);
			}
		} catch (IOException e) {
			throw new IllegalFactoryOptionsException("there was an error reading the input", e);
		}
		
		return store;
	}
}