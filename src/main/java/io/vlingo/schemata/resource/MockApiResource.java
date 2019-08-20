// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import static io.vlingo.http.Response.Status.Ok;
import static io.vlingo.http.resource.ResourceBuilder.get;
import static io.vlingo.http.resource.ResourceBuilder.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.vlingo.common.Completes;
import io.vlingo.http.Response;
import io.vlingo.http.resource.Resource;
import io.vlingo.http.resource.ResourceHandler;
import io.vlingo.http.resource.serialization.JsonSerialization;
import io.vlingo.schemata.model.Category;
import io.vlingo.schemata.model.SchemaVersion.Status;
import io.vlingo.schemata.resource.data.ContextData;
import io.vlingo.schemata.resource.data.OrganizationData;
import io.vlingo.schemata.resource.data.SchemaData;
import io.vlingo.schemata.resource.data.SchemaMetaData;
import io.vlingo.schemata.resource.data.SchemaVersion;
import io.vlingo.schemata.resource.data.UnitData;

/**
 * Serves randomized mock API model formatted data
 * for consumption in the UI.
 */
public class MockApiResource extends ResourceHandler {
  public static Resource<?> asResource() {
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
          OrganizationData.from("o1", "Orga 1", "Orga 1 Description",
            Arrays.asList(
              UnitData.from("o1", "u1", "Orga 1 Unit 1", "Description1",
                Arrays.asList(
                  ContextData.from("o1-u1-c1", "Orga 1 Unit 1 Context 1"),
                  ContextData.from("o1-u1-c2", "Orga 1 Unit 1 Context 2"),
                  ContextData.from("o1-u1-c3", "Orga 1 Unit 1 Context 3")
                )),
              UnitData.from("o1", "u2", "Orga 1 Unit 2", "Description2",
                Arrays.asList(
                  ContextData.from("o1-u2-c1", "Orga 1 Unit 2 Context 1")
                )),
              UnitData.from("o1", "u3", "Orga 1 Unit 3", "Description3",
                Arrays.asList(
                  ContextData.from("o1-u3-c1", "Orga 1 Unit 3 Context 1"),
                  ContextData.from("o1-u3-c2", "Orga 1 Unit 3 Context 2")
                )))
            ),
          OrganizationData.from("o2", "Orga 2", "Orga 2 Description",
            Arrays.asList(
              UnitData.from("o2", "u1", "Orga 2 Unit 1", "Description2",
                Arrays.asList()),
              UnitData.from("o2", "u2", "Orga 2 Unit 2", "Description2",
                Arrays.asList(
                  ContextData.from("o2-u2-c1", "Orga 2 Unit 2 Context 1"),
                  ContextData.from("o2-u2-c2", "Orga 2 Unit 2 Context 2"),
                  ContextData.from("o2-u2-c3", "Orga 2 Unit 2 Context 3")
                )),
              UnitData.from("o2", "u3", "Orga 2 Unit 3", "Description3",
                Arrays.asList(
                  ContextData.from("o2-u3-c1", "Orga 2 Unit 3 Context 1")
                )))
            )
        )
      )));
  }

  private Completes<Response> schemata(final String organizationId, final String unitId, final String contextId) {
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

  private Completes<Response> schema(final String organizationId, final String unitId, final String contextId, final String schema, final String version) {

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

  private <T> T randomElement(final T[] elements) {
    Random random = new Random();
    return elements[random.nextInt(elements.length)];
  }

  private List<SchemaData> randomVersions() {
    List<SchemaData> versions = new ArrayList<>();
    Random random = new Random();

    int noOfVersions = random.nextInt(10);
    while (noOfVersions > 0) {
      versions.add(SchemaData.from(
        String.format("%s.%s.%s",
          random.nextInt(10),
          random.nextInt(10),
          random.nextInt(42)
        ),
        randomElement(Category.values()).name(),
        randomElement(new String[] {"DocumentAdded", "DocumentEdited", "DocumentRemoved", "DocumentRestored"}),
        randomElement(new String[] {"Description1", "Description2", "Description3", "Description4"})
        ));
      noOfVersions--;
    }

    versions.sort(Comparator.comparing(v -> v.id));
    return versions;
  }
}
