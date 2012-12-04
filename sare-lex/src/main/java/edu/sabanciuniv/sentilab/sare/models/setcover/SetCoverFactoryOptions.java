package edu.sabanciuniv.sentilab.sare.models.setcover;

import edu.sabanciuniv.sentilab.core.models.factory.IFactoryOptions;
import edu.sabanciuniv.sentilab.sare.models.base.document.TokenizingOptions;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;

/**
 * The default set of options that can be used to construct an {@link DocumentSetCover} object.
 * The most specific combination of properties will be used.
 * @author Mus'ab Husaini
 *
 */
public class SetCoverFactoryOptions
	implements IFactoryOptions<DocumentSetCover> {

	private PersistentDocumentStore store;
	private TokenizingOptions tokenizingOptions;
	private double requiredWeightRatio;
	private String title;
	private String description;
	private String language;
	
	public SetCoverFactoryOptions() {
		this.requiredWeightRatio = 1.0;
	}
	
	/**
	 * Gets the store from which the set cover will be created.
	 * @return the {@link PersistentDocumentStore} object from which the set cover will be created.
	 */
	public PersistentDocumentStore getStore() {
		return this.store;
	}
	
	/**
	 * Sets the store from which the set cover is to be created.
	 * @param store the {@link PersistentDocumentStore} object from which the set cover is to be created.
	 * @return the {@code this} object.
	 */
	public SetCoverFactoryOptions setStore(PersistentDocumentStore store) {
		this.store = store;
		return this;
	}
	
	/**
	 * Gets the tokenizing options that will be used to tokenize the content of the documents.
	 * @return the {@link TokenizingOptions} object containing the tokenizing options.
	 */
	public TokenizingOptions getTokenizingOptions() {
		return this.tokenizingOptions;
	}
	
	/**
	 * Sets the tokenizing options to be used for tokenizing the content of the documents.
	 * @param tokenizingOptions the {@link TokenizingOptions} object containing the tokenizing options.
	 * @return the {@code this} object.
	 */
	public SetCoverFactoryOptions setTokenizingOptions(TokenizingOptions tokenizingOptions) {
		this.tokenizingOptions = tokenizingOptions;
		return this;
	}
	
	/**
	 * Gets the title of the set cover to be created.
	 * @return the title.
	 */
	public String getTitle() {
		return this.title;
	}
	
	/**
	 * Sets the title to be set for the set cover.
	 * @param title the title to be set.
	 * @return the {@code this} object.
	 */
	public SetCoverFactoryOptions setTitle(String title) {
		this.title = title;
		return this;
	}
	
	/**
	 * Gets the description of the set cover to be created.
	 * @return the description.
	 */
	public String getDescription() {
		return this.description;
	}
	
	/**
	 * Sets the description to be set for the set cover.
	 * @param description the description to be set.
	 * @return the {@code this} object.
	 */
	public SetCoverFactoryOptions setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * Gets the language of the set cover to be created.
	 * @return the language.
	 */
	public String getLanguage() {
		return this.language;
	}

	/**
	 * Sets the language to set for the set cover.
	 * @param language the language to be set.
	 * @return the {@code this} object.
	 */
	public SetCoverFactoryOptions setLanguage(String language) {
		this.language = language;
		return this;
	}

	/**
	 * Gets the minimum ratio of total token weight to be retained in the final set cover.
	 * @return the ratio of total token weight.
	 */
	public double getRequiredWeightRatio() {
		return this.requiredWeightRatio;
	}

	/**
	 * Sets the minimum ratio of total token weight required for the set cover.
	 * @param requiredWeightRatio the ratio of total token weight to set.
	 * @return the {@code this} object.
	 */
	public SetCoverFactoryOptions setRequiredWeightRatio(double requiredWeightRatio) {
		this.requiredWeightRatio = requiredWeightRatio;
		return this;
	}
}