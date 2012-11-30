package edu.sabanciuniv.sentilab.sare.models.document;

import javax.persistence.*;

import edu.sabanciuniv.sentilab.sare.models.document.base.*;

@Entity
@DiscriminatorValue("opinion-document")
public class OpinionDocument
	extends TextDocument<OpinionDocument> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2242899151250566895L;

	@Column
	private double polarity;
	
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