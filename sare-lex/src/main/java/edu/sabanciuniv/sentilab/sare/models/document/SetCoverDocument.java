package edu.sabanciuniv.sentilab.sare.models.document;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import edu.sabanciuniv.sentilab.sare.models.document.base.PersistentDocument;
import edu.sabanciuniv.sentilab.sare.models.document.base.MergableDocument;

/**
 * The class for set cover documents.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("SetCover")
public class SetCoverDocument
	extends MergableDocument<SetCoverDocument> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5638920050574370647L;
	
	@Column
	private Double weight;

	@PrePersist
	@PreUpdate
	private void updateWeight() {
		if (this.weight == null) {
			this.setWeight(this.getWeight());
		}
	}
	
	/**
	 * Sets the weight of this document.
	 * @param weight the weight to set.
	 * @return the {@code this} object.
	 */
	private SetCoverDocument setWeight(Double weight) {
		this.weight = weight;
		return this;
	}

	/**
	 * Creates a new instance of {@link SetCoverDocument}.
	 */
	public SetCoverDocument() {
		//
	}
	
	/**
	 * Creates a new instance of {@link SetCoverDocument}.
	 * @param baseDocument the {@code PersistentDocument} used as the base document for this instance.
	 */
	public SetCoverDocument(PersistentDocument baseDocument) {
		this();
		this.setBaseDocument(baseDocument);
	}
	
	@Override
	public String getContent() {
		return this.getBaseDocument() != null ? this.getBaseDocument().getContent() : null;
	}

	/**
	 * Gets the weight of this document.
	 * @return the weight of this document.
	 */
	public double getWeight() {
		return this.weight == null ? this.getTotalTokenWeight() : this.weight;
	}
	
	/**
	 * Resets the weight of this document to {@code null}.
	 * @return the {@code this} object.
	 */
	public SetCoverDocument resetWeight() {
		this.setWeight(null);
		return this;
	}

	@Override
	public String toString() {
		return this.getBaseDocument() != null ? this.getBaseDocument().toString() : super.toString(); 
	}
}