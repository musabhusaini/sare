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
