/*
 * Sentilab SARE: a Sentiment Analysis Research Environment
 * Copyright (C) 2013 Sabanci University Sentilab
 * http://sentilab.sabanciuniv.edu
 * 
 * This file is part of SARE.
 * 
 * SARE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * SARE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package actors;

import java.util.*;

import javax.persistence.*;

import org.apache.commons.lang3.time.DateUtils;

import play.*;

import models.web.ProgressObserverToken;
import models.web.WebSession;

import com.avaje.ebean.*;

import controllers.base.SessionedAction;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.*;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

import akka.actor.*;

public class SessionCleaner
	extends UntypedActor {
	
	public static boolean clean(WebSession session) {
		if (session == null) {
			return false;
		}
		
		Logger.info("deleting session " + UuidUtils.normalize(session.id));
		List<ProgressObserverToken> poTokens = ProgressObserverToken.find
			.where()
				.eq("session", session)
				.findList();
		for (ProgressObserverToken poToken : poTokens) {
			poToken.delete();
		}
		
		session.delete();
		
		// if the owner id and session id are the same, it's a standalone session, so delete all stores owned.
		if (!SessionedAction.isAuthenticated(session)) {
			EntityManager em = SareEntityManagerFactory
				.createEntityManager(Play.application().getWrappedApplication().mode().toString());
			em.getTransaction().begin();
			
			// delete all owned stores.
			List<String> uuids = new PersistentDocumentStoreController().getAllUuids(em, session.ownerId);
			for (String uuid : uuids) {
				Logger.info("deleting store " + uuid + " owned by " + session.ownerId);
				PersistentDocumentStore store = em.find(PersistentDocumentStore.class, UuidUtils.toBytes(uuid));
				if (store != null) {
					em.remove(store);
				}
			}
			
			em.getTransaction().commit();
			em.close();
		} else {
			// TODO: add logic to handle user sign out.
		}
		
		return true;
	}
	
	@Override
	public void onReceive(Object message) throws Exception {
		Ebean.execute(new TxRunnable() {
			@Override
			public void run() {
				Logger.info("cleaning sessions");
				
				Integer timeout = Play.application().configuration().getInt("application.session.timeout");
				if (timeout == null) {
					timeout = 60;
				}
				
				if (timeout != 0) {
					// get all old sessions.
					List<WebSession> oldSessions =
						WebSession.find.where()
							.lt("updated", new Date(DateUtils.addMinutes(new Date(), -timeout).getTime()))
							.findList();
					
					// delete each.
					for (WebSession session : oldSessions) {
						clean(session);
					}
				}
			}
		});
	}
}