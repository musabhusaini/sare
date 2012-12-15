package edu.sabanciuniv.sentilab.sare.models.opinion;

import javax.persistence.*;

import edu.sabanciuniv.sentilab.sare.models.base.document.*;

@Entity
@DiscriminatorValue("opinion-document")
public class OpinionDocument
	extends TextDocument<OpinionDocument> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2242899151250566895L;

	@Column
	private Double polarity;
	
	/**
	 * Gets the opinion polarity of this document.
	 * @return the opinion polarity of this document.
	 */
	public Double getPolarity() {
		return this.polarity;
	}

	/**
	 * Sets the opinion polarity of this document.
	 * @param polarity the opinion polarity to set.
	 * @return the {@code this} object.
	 */
	public OpinionDocument setPolarity(Double polarity) {
		this.polarity = polarity;
		return this;
	}
	
	@Override
	public String toString() {
		return String.format("%s [polarity = %1.2f]", super.toString(), this.getPolarity());
	}
}