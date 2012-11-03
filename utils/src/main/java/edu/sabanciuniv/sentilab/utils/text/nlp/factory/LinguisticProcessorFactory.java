package edu.sabanciuniv.sentilab.utils.text.nlp.factory;

import java.lang.reflect.Modifier;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.reflections.Reflections;

import edu.sabanciuniv.sentilab.utils.CannedMessages;
import edu.sabanciuniv.sentilab.utils.text.nlp.annotations.LinguisticProcessorInfo;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.ILinguisticProcessor;

/**
 * The factory class for creating {@link ILinguisticProcessor} objects.
 * @author Mus'ab Husaini
 */
public class LinguisticProcessorFactory {
	
	private LinguisticProcessorFactoryOptions options;
	
	/**
	 * Creates an instance of the {@link LinguisticProcessorFactory} class.
	 * @param options the {@link LinguisticProcessorFactoryOptions} object indicating the properties of the desired linguistic processor.
	 */
	public LinguisticProcessorFactory(LinguisticProcessorFactoryOptions options) {
		this.options = Validate.notNull(options, CannedMessages.NULL_ARGUMENT, "options");
	}
	
	/**
	 * Creates the desired linguistic processor, if available.
	 * @return the {@link ILinguisticProcessor} object corresponding to the desired specifications if it exists. {@code null} otherwise.
	 */
	public ILinguisticProcessor create() {
		Class<? extends ILinguisticProcessor> processorClass = null;
		Reflections reflections = new Reflections("edu.sabanciuniv.sentilab");
		Set<Class<? extends ILinguisticProcessor>> subTypes = reflections.getSubTypesOf(ILinguisticProcessor.class);
		for (Class<? extends ILinguisticProcessor> c : subTypes) {
			LinguisticProcessorInfo info = c.getAnnotation(LinguisticProcessorInfo.class);
			if (info == null || Modifier.isAbstract(c.getModifiers())) {
				continue;
			}
			
			try {
				c.getConstructor();
			} catch (NoSuchMethodException | SecurityException e) {
				continue;
			}
			
			if (this.options.getName() == null || (this.options.isIgnoreNameCase() ?
					info.name().equalsIgnoreCase(this.options.getName()) : info.name().equals(this.options.getName()))) {
				processorClass = c;
			}
			
			if (processorClass != null &&
				this.options.getLanguage() != null && !info.language().equalsIgnoreCase(this.options.getLanguage())) {
				processorClass = null;
			}
			
			if (processorClass != null && this.options.isMustTag() && !info.canTag()) {
				processorClass = null;
			}
			
			if (processorClass != null && this.options.isMustParse() && !info.canParse()) {
				processorClass = null;
			}
			
			if (processorClass != null) {
				break;
			}
		}
		
		try {
			return processorClass.newInstance();
		} catch (Exception e) {
			// Fall back to the default behavior.
		}
		
		return null;
	}
}