package edu.sabanciuniv.sentilab.utils.text.nlp.base;

import java.lang.annotation.Annotation;

import edu.sabanciuniv.sentilab.utils.text.nlp.annotations.LinguisticProcessorInfo;

/**
 * The base implementation of {@link ILinguisticProcessor}.
 * @author Mus'ab Husaini
 */
public abstract class LinguisticProcessor
	implements ILinguisticProcessor {

	private LinguisticProcessorInfo infoAnnotation;
	
	public static final LinguisticProcessorInfo DEFAULT_INFO = new LinguisticProcessorInfo() {
		
		@Override
		public Class<? extends Annotation> annotationType() {
			return LinguisticProcessorInfo.class;
		}
		
		@Override
		public String name() {
			return "";
		}
		
		@Override
		public String language() {
			return "";
		}
		
		@Override
		public boolean canTag() {
			return true;
		}
		
		@Override
		public boolean canParse() {
			return false;
		}
	};
	
	/**
	 * Creates an empty instance of {@code LinguisticProcessor}.
	 */
	protected LinguisticProcessor() {
		this.infoAnnotation = this.getClass().getAnnotation(LinguisticProcessorInfo.class);
		
		if (this.infoAnnotation == null) {
			this.infoAnnotation = DEFAULT_INFO;
		}
	}
	
	/**
	 * Gets the name of this processor.
	 * @return the name.
	 */
	public String getName() {
		return this.infoAnnotation.name();
	}
	
	/**
	 * Gets the language that this processor can process.
	 * @return the language.
	 */
	public String getLanguage() {
		return this.infoAnnotation.language();
	}
	
	/**
	 * Gets a flag indicating whether this processor can provide POS tags or not.
	 * @return the flag.
	 */
	public boolean canTag() {
		return this.infoAnnotation.canTag();
	}
	
	/**
	 * Gets a flag indicating whether this processor can parse text for dependencies or not.
	 * @return the flag.
	 */
	public boolean canParse() {
		return this.infoAnnotation.canParse();
	}
}