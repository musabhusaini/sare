package edu.sabanciuniv.sentilab.sare.models.document;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import edu.sabanciuniv.sentilab.sare.models.document.base.MergableDocument;
import edu.sabanciuniv.sentilab.sare.models.document.base.TokenizedDocument;

@Entity
@DiscriminatorValue("SetCover")
public class SetCoverDocument
	extends MergableDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5638920050574370647L;
	
	@Column
	private double weight;

	@Override
	public String getContent() {
		return this.baseDocument != null ? this.baseDocument.getContent() : null;
	}

	/**
	 * Gets the weight of this document.
	 * @return the weight of this document.
	 */
	public double getWeight() {
		return this.weight;
	}

	/**
	 * Sets the weight of this document.
	 * @param weight the weight to set.
	 * @return the {@code this} object.
	 */
	public SetCoverDocument setWeight(double weight) {
		this.weight = weight;
		return this;
	}
	
	@Override
	public SetCoverDocument merge(TokenizedDocument other) {
		super.merge(other);
		this.setWeight(this.getTotalTokenWeight());
		
		return this;
	}
}