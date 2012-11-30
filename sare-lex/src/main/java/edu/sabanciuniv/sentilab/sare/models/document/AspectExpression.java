package edu.sabanciuniv.sentilab.sare.models.document;

import javax.persistence.*;

import edu.sabanciuniv.sentilab.sare.models.document.base.*;

/**
 * A class that represents an aspect expression.
 * @author Mus'ab Husaini
 */
@Entity
@DiscriminatorValue("aspect-expression")
public class AspectExpression
	extends TextDocument<AspectExpression> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8749649591681294449L;
	
}