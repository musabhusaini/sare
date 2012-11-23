package edu.sabanciuniv.sentilab.utils.text.nlp.factory;

import java.lang.reflect.Modifier;
import java.util.Set;

import org.apache.commons.lang3.Validate;
import org.reflections.Reflections;

import edu.sabanciuniv.sentilab.core.controllers.factory.IFactory;
import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.utils.CannedMessages;
import edu.sabanciuniv.sentilab.utils.text.nlp.annotations.LinguisticProcessorInfo;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.ILinguisticProcessor;

/**
 * The factory class for creating {@link ILinguisticProcessor} objects.
 * @author Mus'ab Husaini
 */
public class LinguisticProcessorFactory
	implements IFactory<ILinguisticProcessor, LinguisticProcessorFactoryOptions> {
	
	@Override
	public ILinguisticProcessor create(LinguisticProcessorFactoryOptions options)
		throws IllegalFactoryOptionsException {
		
		try {
			Validate.notNull(options, CannedMessages.NULL_ARGUMENT, "options");
		} catch (NullPointerException e) {
			throw new IllegalFactoryOptionsException(e);
		}
		
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
			
			if (options.getName() == null || (options.isIgnoreNameCase() ?
					info.name().equalsIgnoreCase(options.getName()) : info.name().equals(options.getName()))) {
				processorClass = c;
			}
			
			if (processorClass != null &&
				options.getLanguage() != null && !info.language().equalsIgnoreCase(options.getLanguage())) {
				processorClass = null;
			}
			
			if (processorClass != null && options.isMustTag() && !info.canTag()) {
				processorClass = null;
			}
			
			if (processorClass != null && options.isMustParse() && !info.canParse()) {
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