package controllers.base;

import models.base.*;
import controllers.factories.ViewModelFactory;
import play.*;
import play.mvc.*;

import views.html.*;

@With({ SessionedAction.class, ErrorHandledAction.class, LoggedAction.class })
public class Application extends Controller {
	
	protected static <T> ViewModel createViewModel(T model) {
		ViewModel viewModel = new ViewModelFactory().create(new ViewModelFactoryOptions().setModel(model));
		if (viewModel == null) {
			throw new IllegalArgumentException();
		}
		
		return viewModel;
	}
	
	protected static <T> ViewModel createViewModelQuietly(T model) {
		try {
			return createViewModel(model);
		} catch (IllegalArgumentException e) {
			return new ViewModel();
		}
	}
	
	public static Result index() {
		return ok(index.render("Your new application is ready."));
	}

	public static Result keepAlive() {
		Logger.info(LoggedAction.getLogEntry("keeping session alive"));
		return ok();
	}
	
	public static Result authenticate() {
		return TODO;
	}

	public static Result login() {
		return TODO;
	}

	public static Result logout() {
		return TODO;
	}
}