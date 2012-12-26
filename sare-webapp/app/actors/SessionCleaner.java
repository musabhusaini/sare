package actors;

import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang3.time.DateUtils;

import play.*;

import models.*;

import com.avaje.ebean.*;

import controllers.base.SessionedAction;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.*;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

import akka.actor.*;

public class SessionCleaner extends UntypedActor {
	
	public static boolean clean(WebSession session) {
		if (session == null) {
			return false;
		}
		
		Logger.info("deleting session " + UuidUtils.normalize(session.id));
		session.delete();
		
		// if the owner id and session id are the same, it's a standalone session, so delete all stores owned.
		if (!SessionedAction.isAuthenticated(session)) {
			EntityManager em = SareEntityManagerFactory.createEntityManager();
			em.getTransaction().begin();
			
			// delete all owned stores.
			List<String> uuids = new PersistentDocumentStoreController().getAllUuids(em, session.ownerId);
			for (String uuid : uuids) {
				Logger.info("deleting store " + uuid + " owned by " + session.ownerId);
				PersistentDocumentStore store = em.find(PersistentDocumentStore.class, UuidUtils.toBytes(uuid));
				em.remove(store);
			}
			
			em.getTransaction().commit();
			em.close();
		}
		
		return true;
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		Ebean.execute(new TxRunnable() {
			@Override
			public void run() {
				Logger.info("cleaning sessions");
				
				// get all old sessions.
				List<WebSession> oldSessions =
					WebSession.find.where()
						.lt("updated", new Date(DateUtils.addHours(new Date(), -1).getTime()))
						.findList();
				
				// delete each.
				for (WebSession session : oldSessions) {
					clean(session);
				}
			}
		});
	}
}