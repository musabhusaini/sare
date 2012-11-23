package edu.sabanciuniv.sentilab.sare.models.documentStore;

import java.io.*;

import edu.sabanciuniv.sentilab.sare.models.factory.base.IFactoryOptions;

/**
 * The default set of options that can be used to construct an {@link OpinionCorpus} object.
 * The most specific combination of properties will be used.
 * @author Mus'ab Husaini
 */
public class OpinionCorpusFactoryOptions implements
		IFactoryOptions<OpinionCorpus> {

	private String title;
	private String description;
	private String language;
	private File file;
	private String format;
	private byte[] bytes;
	private InputStream inputStream;
	
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
}