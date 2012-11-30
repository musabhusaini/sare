package edu.sabanciuniv.sentilab.sare.models.base.document;

import javax.persistence.*;

/**
 * A class that has its own textual content.
 * @author Mus'ab Husaini
 * 
 * @param <T> a circular reference to this type of document; must derive from {@link TextDocument}.
 */
@MappedSuperclass
public abstract class TextDocument<T extends TextDocument<T>>
	extends GenericDocument<T> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5392373511223975393L;

	@Column(columnDefinition = "MEDIUMTEXT")
	protected String content;
	
	@Override
	public String getContent() {
		return this.content;
	}
	
	/**
	 * Sets the content of this document.
	 * @param content the content to set.
	 * @return the {@code this} object.
	 */
	public TextDocument<T> setContent(String content) {
		this.content = content;
		return this;
	}
}