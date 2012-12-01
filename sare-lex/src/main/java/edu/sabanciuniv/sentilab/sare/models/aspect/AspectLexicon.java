package edu.sabanciuniv.sentilab.sare.models.aspect;

import javax.persistence.*;

import edu.sabanciuniv.sentilab.sare.models.base.documentStore.*;

/**
 * A class that represents an aspect lexicon.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("aspect-lexicon")
public class AspectLexicon
	extends GenericDocumentStore<AspectExpression> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1318074240345771899L;

	/**
	 * Creates an instance of the {@link AspectLexicon} class.
	 */
	public AspectLexicon() {
		//
	}
	
	/**
	 * Creates an instance of the {@link AspectLexicon} class based on the provided store.
	 * @param baseStore the {@link DocumentStoreBase} object to use as the base store for this instance.
	 */
	public AspectLexicon(PersistentDocumentStore baseStore) {
		this();
		this.setBaseStore(baseStore);
	}
	
	@Override
	public String getTitle() {
		return super.getTitle() == null && this.getBaseStore() != null ? this.getBaseStore().getTitle() : super.getTitle();
	}
	
	@Override
	public String getLanguage() {
		return super.getLanguage() == null && this.getBaseStore() != null ? this.getBaseStore().getLanguage() : super.getLanguage();
	}
	
	@Override
	public String getDescription() {
		return super.getDescription() == null && this.getBaseStore() != null ? this.getBaseStore().getDescription() : super.getDescription();
	}
}