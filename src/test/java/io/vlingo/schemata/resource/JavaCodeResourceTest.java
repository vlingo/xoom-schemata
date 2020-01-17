// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource;

import static io.vlingo.http.Response.Status.Ok;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import io.vlingo.actors.CompletesEventually;
import io.vlingo.http.Context;
import io.vlingo.http.Method;
import io.vlingo.http.Request;
import io.vlingo.http.RequestHeader;
import io.vlingo.http.Response;
import io.vlingo.schemata.Schemata;
import io.vlingo.schemata.model.Category;
import io.vlingo.schemata.model.Scope;
import io.vlingo.schemata.resource.data.AuthorizationData;
import io.vlingo.schemata.resource.data.ContextData;
import io.vlingo.schemata.resource.data.OrganizationData;
import io.vlingo.schemata.resource.data.SchemaData;
import io.vlingo.schemata.resource.data.SchemaVersionData;
import io.vlingo.schemata.resource.data.UnitData;

public class JavaCodeResourceTest extends ResourceTest {
  @Test
  public void testThatJavaCodeIsReferenced() {
    final CodeResource resource = new CodeResource(world);
    resource.__internal__test_set_up(context(), stage);
    final Response response = resource.queryCodeForLanguage(reference(), "java").await();
    assertEquals(Ok, response.status);
    assertTrue(response.entity.content().contains("SchemaDefined"));
    assertEquals("package io.vlingo.schemata.event;\n" +
            "\n" +
            "import io.vlingo.lattice.model.DomainEvent;\n" +
            "\n" +
            "public final class SchemaDefined extends DomainEvent {\n" +
            "  public SchemaDefined() {\n" +
            "  }\n" +
            "}\n",
            response.entity.content());
  }

  @Before
  @Override
  public void setUp() throws Exception {
    super.setUp();

    createFixtures();
  }

  private Context context() {
    final String authValue =
            AuthorizationData.AuthorizationType +
            " source = " + organizationId +
            "    dependent = " + referenceFrom(organizationId, unitId, contextId);

    final Request request =
            Request
              .has(Method.GET)
              .and(RequestHeader.of(RequestHeader.Authorization, authValue));

    return new Context(request, (CompletesEventually) null);
  }

  private static final String OrgName = "VLINGO, LLC";
  private static final String OrgDescription = "We are the vlingo/PLATFORM company.";
  private String organizationId;

  private static final String UnitName = "Development Team";
  private static final String UnitDescription = "We are vlingo/PLATFORM development.";
  private String unitId;


  private static final String ContextNamespace = "io.vlingo.schemata";
  private static final String ContextDescription = "We are the vlingo/schemata team.";
  private String contextId;

  private static final String SchemaName = "SchemaDefined";
  private static final String SchemaDescription = "Captures that a schema's definition occurred.";
  private static final String SchemaCategory = Category.Event.name();
  private static final String SchemaScope = Scope.Public.name();
  private String schemaId;

  private static final String SchemaVersionDescription = "Test context.";
  private static final String SchemaVersionSpecification = "event SchemaDefined {}";
  private static final String SchemaVersionStatus = "Draft";
  private static final String SchemaVersionVersion000 = "0.0.0";
  private static final String SchemaVersionVersion100 = "1.0.0";
  private String schemaVersionId;

  private void createFixtures() {
    final OrganizationResource organizationResource = new OrganizationResource(world);
    final Response organizationResponse = organizationResource.defineWith(OrganizationData.just(OrgName, OrgDescription)).await();
    organizationId = extractResourceIdFrom(organizationResponse);

    final UnitResource unitResource = new UnitResource(world);
    final Response unitResponse = unitResource.defineWith(organizationId, UnitData.just(UnitName, UnitDescription)).await();
    unitId = extractResourceIdFrom(unitResponse);

    final ContextResource contextResource = new ContextResource(world);
    final Response contextResponse = contextResource.defineWith(organizationId, unitId, ContextData.just(ContextNamespace, ContextDescription)).await();
    contextId = extractResourceIdFrom(contextResponse);

    final SchemaResource schemaResource = new SchemaResource(world);
    final Response schemaResponse = schemaResource.defineWith(organizationId, unitId, contextId, SchemaData.just(SchemaCategory, SchemaScope, SchemaName, SchemaDescription)).await();
    schemaId = extractResourceIdFrom(schemaResponse);

    final SchemaVersionResource schemaVersionResource = new SchemaVersionResource(world);
    final SchemaVersionData defineData = SchemaVersionData.just(SchemaVersionSpecification, SchemaVersionDescription, SchemaVersionStatus, SchemaVersionVersion000, SchemaVersionVersion100);
    final Response schemaVersionResponse = schemaVersionResource.defineWith(organizationId, unitId, contextId, schemaId, defineData).await();
    schemaVersionId = extractResourceIdFrom(schemaVersionResponse);

    System.out.println(
            new StringBuilder()
              .append("RESOURCES FOR TEST:").append("\n")
              .append(" Organization: ").append(organizationId).append("\n")
              .append("         Unit: ").append(unitId).append("\n")
              .append("      Context: ").append(contextId).append("\n")
              .append("       Schema: ").append(schemaId).append("\n")
              .append("SchemaVersion: ").append(schemaVersionId).append("\n")
              .toString());
  }

  private String reference() {
    return referenceFrom(OrgName, UnitName, ContextNamespace, SchemaName, SchemaVersionVersion100);
  }

  private String referenceFrom(final String...values) {
    final StringBuilder builder = new StringBuilder();
    String separator = "";

    for (final String value : values) {
      builder.append(separator).append(value);
      separator = Schemata.ReferenceSeparator;
    }

    return builder.toString();
  }
}
