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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package global;

import java.util.concurrent.TimeUnit;

import actors.*;
import akka.actor.*;
import play.*;
import play.libs.*;
import scala.concurrent.duration.Duration;

public class Global extends GlobalSettings {

	@Override
	public void onStart(Application app) {
		super.onStart(app);
		
		// run session cleaner.
		ActorRef sessionCleaner = Akka.system().actorOf(new Props(SessionCleaner.class));
		Akka.system().scheduler().schedule(
			Duration.create(0, TimeUnit.MILLISECONDS),
			Duration.create(10, TimeUnit.MINUTES),
			sessionCleaner,
			"", Akka.system().dispatcher());
	}
}
