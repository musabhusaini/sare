package controllers.tests;

import org.codehaus.jackson.JsonNode;
import org.junit.*;

import base.TestBase;

import com.google.common.collect.Iterators;

import play.mvc.*;
import play.libs.Json;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

public class ModuleControllerTest extends TestBase {

	@Test
	public void testModuleControllerWithEmpty() {
		Result result = callAction(controllers.routes.ref.ModuleController.options("{}"));

		assertThat(result).isNotNull();
		assertThat(status(result)).isEqualTo(OK);
		assertThat(contentType(result)).isEqualTo("application/json");
		
		JsonNode response = Json.parse(contentAsString(result));
		assertThat(response).isNotNull();
		assertThat(response.isArray()).isTrue();
		assertThat(Iterators.size(response.getElements())).isGreaterThan(0);
	}
}