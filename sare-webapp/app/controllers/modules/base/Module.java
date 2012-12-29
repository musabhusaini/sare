package controllers.modules.base;

import java.lang.annotation.*;

import controllers.base.Application;

import models.base.*;

public abstract class Module extends Application {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target({ElementType.TYPE})
	public static @interface Requires {
		public Class<? extends ViewModel>[] types() default {};
	}
	
	public abstract String getDisplayName();
	
	public abstract String getRoute(Iterable<ViewModel> viewModels);
}