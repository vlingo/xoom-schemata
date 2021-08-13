// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.schemata.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import io.vlingo.xoom.schemata.codegen.TypeDependenciesRetriever.TypeDependencies;
import io.vlingo.xoom.schemata.model.Path;
import io.vlingo.xoom.schemata.resource.data.ContextData;
import io.vlingo.xoom.schemata.resource.data.OrganizationData;
import io.vlingo.xoom.schemata.resource.data.SchemaData;
import io.vlingo.xoom.schemata.resource.data.SchemaVersionData;
import io.vlingo.xoom.schemata.resource.data.UnitData;

public class SchemaDependenciesResourceTest extends AbstractRestTest {

  private static final String OrgName = "Vlingo";
  private static final String UnitName = "store";
  private static final String Context = "io.vlingo.store.logistics";
  private static final String PriceSpecification = "data Price { double value }";
  private static final String CourierNameSpecification = "data CourierName { string name }";
  private static final String CourierSpecification = "data Courier { data.CourierName:1.0.0 name }";

  @Test
  @SuppressWarnings({ "unchecked", "unlikely-arg-type" })
  public void testThatSchemaDependenciesAreRetrieved() {
    loadSchemas();

    final String reference =
            Path.with(OrgName, UnitName, Context, "ProductDelivered", "1.0.0").toReference();

    final Set<TypeDependencies> dependencies =
        given().when().get(String.format("/api/schemas/%s/dependencies", reference))
                .then().statusCode(200).extract().body().as(Set.class);

    assertEquals(2, dependencies.size());
    assertTrue(dependencies.contains("Vlingo:store:io.vlingo.store.logistics:Courier:1.0.0"));
    assertTrue(dependencies.contains("Vlingo:store:io.vlingo.store.logistics:Price:1.0.0"));
  }

  private void loadSchemas() {
    final String organizationId =
            given().when().body(OrganizationData.just(OrgName, OrgName))
                    .post("/api/organizations").then().statusCode(201)
                    .extract().body().as(OrganizationData.class).organizationId;

    final String unitUri = String.format("/api/organizations/%s/units", organizationId);

    final String unitId =
            given().when().body(UnitData.just(UnitName, UnitName))
                    .post(unitUri).then().statusCode(201).extract().body().as(UnitData.class).unitId;

    final String contextUri = String.format("%s/%s/contexts", unitUri, unitId);

    final String contextId =
            given().when().body(ContextData.just(Context, Context))
                    .post(contextUri).then().statusCode(201).extract()
                    .body().as(ContextData.class).contextId;

    final String schemasUri = String.format("%s/%s/schemas", contextUri, contextId);

    final String priceSchemaId =
            given().when().body(SchemaData.just("Data", "Public", "Price", "Price"))
                    .post(schemasUri)
                    .then().statusCode(201).extract().body().as(SchemaData.class).schemaId;

    final String courierNameSchemaId =
            given().when().body(SchemaData.just("Data", "Public", "CourierName", "CourierName"))
                    .post(schemasUri)
                    .then().statusCode(201).extract().body().as(SchemaData.class).schemaId;

    final String courierSchemaId =
            given().when().body(SchemaData.just("Data", "Public", "Courier", "Courier"))
                    .post(schemasUri)
                    .then().statusCode(201).extract().body().as(SchemaData.class).schemaId;

    final String productDeliveredSchemaId =
            given().when().body(SchemaData.just("Event", "Public", "ProductDelivered", "ProductDelivered"))
                    .post(schemasUri)
                    .then().statusCode(201).extract().body().as(SchemaData.class).schemaId;

    given().when().body(SchemaVersionData.just(PriceSpecification, "Price", "", "0.0.1", "1.0.0"))
            .post(String.format("%s/%s/versions", schemasUri, priceSchemaId)).then().statusCode(201);

    given().when().body(SchemaVersionData.just(CourierNameSpecification, "Courier Name", "", "0.0.1", "1.0.0"))
            .post(String.format("%s/%s/versions", schemasUri, courierNameSchemaId)).then().statusCode(201);

    given().when().body(SchemaVersionData.just(CourierSpecification, "Courier", "", "0.0.1", "1.0.0"))
            .post(String.format("%s/%s/versions", schemasUri, courierSchemaId)).then().statusCode(201);

    given().when().body(SchemaVersionData.just(readSpecification("productDelivered"), "Product Delivered", "", "0.0.1", "1.0.0"))
            .post(String.format("%s/%s/versions", schemasUri, productDeliveredSchemaId)).then().statusCode(201);
  }

  private String readSpecification(final String name) {
    try {
      final InputStream stream = SchemaDependenciesResourceTest.class.getResourceAsStream("/io/vlingo/xoom/schemata/codegen/vss/" + name + ".vss");
      return IOUtils.toString(stream, StandardCharsets.UTF_8.name());
    } catch (final Exception exception) {
      throw new RuntimeException(String.format("Failed to load specification `%s`.", name), exception);
    }
  }
}
