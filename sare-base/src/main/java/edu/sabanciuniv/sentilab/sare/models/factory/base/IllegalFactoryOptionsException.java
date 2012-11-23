package edu.sabanciuniv.sentilab.sare.models.factory.base;

/**
 * An exception that is thrown when the {@link IFactoryOptions} object provided to a {@code create} method are not correct.
 * @author Mus'ab Husaini
 *
 */
public class IllegalFactoryOptionsException
	extends IllegalArgumentException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8224860508230579554L;

	/**
	 * Creates an instance of the {@link IllegalFactoryOptionsException} class.
	 */
	public IllegalFactoryOptionsException() {
		super();
	}
	
	/**
	 * Creates an instance of the {@link IllegalFactoryOptionsException} class with the specified message.
	 * @param s the detailed message.
	 */
	public IllegalFactoryOptionsException(String s) {
		super(s);
	}
	
	/**
	 * Creates an instance of the {@link IllegalFactoryOptionsException} class with the specified cause.
	 * @param cause the {@link Throwable} indicating the cause of this exception.
	 */
	public IllegalFactoryOptionsException(Throwable cause) {
		super(cause);
	}

	/**
	 * Creates an instance of the {@link IllegalFactoryOptionsException} class with the specified cause and message.
	 * @param s the detailed message.
	 * @param cause the {@link Throwable} indicating the cause of this exception.
	 */
	public IllegalFactoryOptionsException(String s, Throwable cause) {
		super(s, cause);
	}
}