package io.vlingo.schemata.infra.http;

import io.vlingo.common.Completes;
import io.vlingo.http.Response;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;
import io.vlingo.http.resource.serialization.JsonSerialization;
import io.vlingo.schemata.infra.http.model.Context;
import io.vlingo.schemata.infra.http.model.Organization;
import io.vlingo.schemata.infra.http.model.Unit;

import java.util.Arrays;

import static io.vlingo.http.Response.Status.Ok;
import static io.vlingo.http.resource.ResourceBuilder.get;
import static io.vlingo.http.resource.ResourceBuilder.resource;

/**
 * Serves the files making up the UI from the classpath.
 * Assumes the generated UI resources to be present in
 * {@code static/} with in the jar or {@code resources/}.
 *
 * <em>WARNING:</em> Note that the current implementation is only able to handle resource
 * directory trees up to three levels deep.
 * <p>
 * FIXME: This leaves the server wide open for read access. We should constrain access to the resources we actually provide.
 */
public class MockApiResource extends ResourceHandler {
  public static Resource asResource() {
    MockApiResource impl = new MockApiResource();

    return resource("mock-api", 10,
      get("/api/organizations")
        .handle(impl::organizations)
    );
  }

  private Completes<Response> organizations() {

    return Completes.withSuccess(Response.of(Ok,
      JsonSerialization.serialized(
        Arrays.asList(
          Organization.from("o1", "Orga 1", "Orga 1 Description",
            Arrays.asList(
              Unit.from("o1-u1", "Orga 1 Unit 1",
                Arrays.asList(
                  Context.from("o1-u1-c1", "Orga 1 Unit 1 Context 1"),
                  Context.from("o1-u1-c2", "Orga 1 Unit 1 Context 2"),
                  Context.from("o1-u1-c3", "Orga 1 Unit 1 Context 3")
                )),
              Unit.from("o1-u2", "Orga 1 Unit 2",
                Arrays.asList(
                  Context.from("o1-u1-c1", "Orga 1 Unit 1 Context 1")
                )),
              Unit.from("o1-u3", "Orga 1 Unit 3",
                Arrays.asList(
                  Context.from("o1-u1-c1", "Orga 1 Unit 3 Context 1"),
                  Context.from("o1-u1-c2", "Orga 1 Unit 3 Context 2")
                ))
            )
          ),
          Organization.from("o2", "Orga 2", "Orga 2 Description",
            Arrays.asList(
              Unit.from("o2-u1", "Orga 2 Unit 1",
                Arrays.asList()),
              Unit.from("o2-u2", "Orga 2 Unit 2",
                Arrays.asList(
                  Context.from("o2-u2-c1", "Orga 2 Unit 2 Context 1"),
                  Context.from("o2-u2-c2", "Orga 2 Unit 2 Context 2"),
                  Context.from("o2-u2-c3", "Orga 2 Unit 2 Context 3")
                )),
              Unit.from("o2-u3", "Orga 2 Unit 3",
                Arrays.asList(
                  Context.from("o2-u3-c1", "Orga 2 Unit 3 Context 1")
                ))
            ))
        )
      )));
  }


}
