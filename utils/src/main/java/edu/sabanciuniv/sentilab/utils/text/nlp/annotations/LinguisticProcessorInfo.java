package edu.sabanciuniv.sentilab.utils.text.nlp.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface LinguisticProcessorInfo {
	public String language() default "";
	public String name() default "";
	public boolean canTag() default true;
	public boolean canParse() default false;
}