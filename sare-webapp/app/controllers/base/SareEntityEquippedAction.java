package controllers.base;

import java.security.AccessControlException;

import javax.persistence.EntityManager;

import edu.sabanciuniv.sentilab.sare.entitymanager.SareEntityManagerFactory;

import play.*;
import play.mvc.*;
import play.mvc.Http.Context;

public class SareEntityEquippedAction extends Action.Simple {

	@Override
	public Result call(Context ctx) throws Throwable {
		Result result = null;
		EntityManager em = null;
		
		try {
			Logger.info(LoggedAction.getLogEntry(ctx, "creating entity manager"));
			
			// create entity manager, add it to args, and begin transaction before the call.
			em = SareEntityManagerFactory.createEntityManager();
			ctx.args.put("em", em);
			em.getTransaction().begin();

			try {
				// call the actual action.
				result = delegate.call(ctx);
				
				// commit active transaction after the call.
				if (em.isOpen() && em.getTransaction().isActive()) {
					em.getTransaction().commit();
				}
			} catch (AccessControlException e) {
				// if this has bubbled up, we'll just throw a json forbidden message.
				result = Application.forbiddenEntity(e.getMessage(), e);
			}
		} catch (Throwable e) {
			Logger.info(LoggedAction.getLogEntry(ctx, "rolling back transaction"));
			
			// rollback on error.
			if (em.isOpen() && em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			
			// rethrow.
			throw e;
		} finally {
			// close em.
			if (em != null && em.isOpen()) {
				em.close();
			}
		}
		
		return result;
	}	
}