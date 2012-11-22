package edu.sabanciuniv.sentilab.sare.models.document;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import edu.sabanciuniv.sentilab.sare.models.document.base.GenericDocument;

@Entity
@DiscriminatorValue("Opinion")
public class OpinionDocument
	extends GenericDocument<OpinionDocument> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2242899151250566895L;

	@Column(columnDefinition = "MEDIUMTEXT")
	private String content;
	
	@Column
	private double polarity;
	
	@Override
	public String getContent() {
		return this.content;
	}
	
	/**
	 * Sets the content of this document.
	 * @param content the content to set.
	 * @return the {@code this} object.
	 */
	public OpinionDocument setContent(String content) {
		this.content = content;
		return this;
	}
	
	/**
	 * Gets the opinion polarity of this document.
	 * @return the opinion polarity of this document.
	 */
	public double getPolarity() {
		return this.polarity;
	}

	/**
	 * Sets the opinion polarity of this document.
	 * @param polarity the opinion polarity to set.
	 * @return the {@code this} object.
	 */
	public OpinionDocument setPolarity(double polarity) {
		this.polarity = polarity;
		return this;
	}
	
	@Override
	public String toString() {
		return String.format("%s [polarity = %1.2f]", super.toString(), this.getPolarity());
	}
}