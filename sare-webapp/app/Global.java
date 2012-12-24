import java.util.concurrent.TimeUnit;

import actors.*;
import akka.actor.*;
import akka.util.*;
import play.*;
import play.libs.*;

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
			null);
	}
}
