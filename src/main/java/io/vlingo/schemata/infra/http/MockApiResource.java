package io.vlingo.schemata.infra.http;

import io.vlingo.common.Completes;
import io.vlingo.http.Response;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;
import io.vlingo.http.resource.serialization.JsonSerialization;
import io.vlingo.schemata.infra.http.model.*;
import io.vlingo.schemata.model.Category;
import io.vlingo.schemata.model.SchemaVersion.Status;

import java.util.*;

import static io.vlingo.http.Response.Status.Ok;
import static io.vlingo.http.resource.ResourceBuilder.get;
import static io.vlingo.http.resource.ResourceBuilder.resource;

/**
 * Serves randomized mock API model formatted data
 * for consumption in the UI.
 */
public class MockApiResource extends ResourceHandler {
  public static Resource asResource() {
    MockApiResource impl = new MockApiResource();

    return resource("mock-api", 10,
      get("/api/schemata/{organization}/{unit}/{context}/{schema}/{version}")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .handle(impl::schema),
      get("/api/schemata/{organization}/{unit}/{context}")
        .param(String.class)
        .param(String.class)
        .param(String.class)
        .handle(impl::schemata),
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
                  Context.from("o1-u2-c1", "Orga 1 Unit 2 Context 1")
                )),
              Unit.from("o1-u3", "Orga 1 Unit 3",
                Arrays.asList(
                  Context.from("o1-u3-c1", "Orga 1 Unit 3 Context 1"),
                  Context.from("o1-u3-c2", "Orga 1 Unit 3 Context 2")
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

  private Completes<Response> schemata(String organizationId, String unitId, String contextId) {
    Map<Category, List<SchemaMetaData>> schemataMap = new HashMap<>();
    String[] tlds = {"io", "com", "org", "net", "gov", "info", "audio"};
    String[] domains = {"vlingo", "kalele", "pluto", "jupiter", "uranus", "mars", "venus", "earth", "titan", "ganymede", "tyco"};
    String[] names = {"foo", "bar", "baz", "qux", "quux", "quuz", "corge", "grault", "garply"};

    Random random = new Random();
    int noOfSchemata = random.nextInt(10);
    while (noOfSchemata > 0) {
      Category category = randomElement(Category.values());
      String name = randomElement(names);

      schemataMap.computeIfAbsent(category, k -> new ArrayList<>());

      schemataMap.get(category).add(SchemaMetaData.from(
        String.format("%s.%s.%s.%s",
          randomElement(tlds),
          randomElement(domains),
          category.name().toLowerCase(),
          name
        ),
        String.format("%s%s", category.name().toLowerCase(), name.substring(0, 1).toUpperCase() + name.substring(1)),
        randomVersions()));
      noOfSchemata--;
    }

    return Completes.withSuccess(Response.of(Ok,
      JsonSerialization.serialized(
        schemataMap
      )));
  }

  private Completes<Response> schema(String organizationId, String unitId, String contextId, String schema, String version) {

    String specification = "event SalutationHappened {\n" +
      "    type eventType\n" +
      "    timestamp occurredOn\n" +
      "    version eventVersion\n" +
      "\n" +
      "    string toWhom\n" +
      "    string text\n" +
      "}";

    String description = ("# " + schema) + "\n" +
      "This schema describes blah blubb." + "\n" + "\n" +
      "It is used whenever something _significant_ happens or should be *done*." + "\n" + "\n" +
      "Oh, a list: " + "\n" +
      "* Item 1" + "\n" +
      "* Item 2" + "\n" +
      "* Item 3" + "\n" +
      "\n" +
      "---" +
      "\n" +
      "Organisation: " + organizationId + "\n" +
      "Unit: " + unitId + "\n" +
      "Context: " + contextId + "\n";

    SchemaVersion result = SchemaVersion.from(
      description,
      specification,
      randomElement(Status.values()).name(),
      version
    );

    return Completes.withSuccess(Response.of(Ok,
      JsonSerialization.serialized(
        result
      )));
  }

  private <T> T randomElement(T[] elements) {
    Random random = new Random();
    return elements[random.nextInt(elements.length)];
  }

  private List<Schema> randomVersions() {
    List<Schema> versions = new ArrayList<>();
    Random random = new Random();

    int noOfVersions = random.nextInt(10);
    while (noOfVersions > 0) {
      versions.add(Schema.from(
        String.format("%s.%s.%s",
          random.nextInt(10),
          random.nextInt(10),
          random.nextInt(42)
        ),
        randomElement(Status.values()).name()));
      noOfVersions--;
    }

    versions.sort(Comparator.comparing(v -> v.id));
    return versions;
  }


}
