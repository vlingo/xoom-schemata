// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.resource;

import static io.vlingo.xoom.http.Response.Status.Ok;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

// import io.vlingo.xoom.actors.CompletesEventually;
import io.vlingo.xoom.http.Response;
import io.vlingo.xoom.schemata.Schemata;
import io.vlingo.xoom.schemata.model.Category;
import io.vlingo.xoom.schemata.model.Scope;
import io.vlingo.xoom.schemata.resource.data.ContextData;
import io.vlingo.xoom.schemata.resource.data.OrganizationData;
import io.vlingo.xoom.schemata.resource.data.SchemaData;
import io.vlingo.xoom.schemata.resource.data.SchemaVersionData;
import io.vlingo.xoom.schemata.resource.data.UnitData;

public class JavaCodeResourceTest extends ResourceTest {
  @Test
  @Ignore //FIXME: still hanging (at/after CodeQueriesActor.codeFor(->queryStateFor)) + this now needs to inject the context or test different
  public void testThatJavaCodeIsReferenced() {
    final CodeResource resource = new CodeResource(stage);
    final Response response = resource.queryCodeForLanguage(reference(), "java").await();
    assertEquals(Ok, response.status);
    assertTrue(response.entity.content().contains("SchemaDefined"));
    assertEquals("package io.vlingo.xoom.schemata.event;\n" +
            "\n" +
            "import io.vlingo.xoom.lattice.model.DomainEvent;\n" +
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

  // private Context context() {
  //   final String authValue =
  //           AuthorizationData.AuthorizationType +
  //           " source = " + organizationId +
  //           "    dependent = " + referenceFrom(organizationId, unitId, contextId);

  //   final Request request =
  //           Request
  //             .has(Method.GET)
  //             .and(RequestHeader.of(RequestHeader.Authorization, authValue));

  //   return new Context(request, (CompletesEventually) null);
  // }

  private static final String OrgName = "VLINGO, LLC";
  private static final String OrgDescription = "We are the VLINGO XOOM Platform company.";
  private String organizationId;

  private static final String UnitName = "Development Team";
  private static final String UnitDescription = "We are VLINGO XOOM Platform development.";
  private String unitId;


  private static final String ContextNamespace = "io.vlingo.xoom.schemata";
  private static final String ContextDescription = "We are the VLINGO XOOM Scehmata team.";
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
    final OrganizationResource organizationResource = new OrganizationResource(stage);
    final Response organizationResponse = organizationResource.defineWith(OrganizationData.just(OrgName, OrgDescription)).await();
    organizationId = extractResourceIdFrom(organizationResponse);

    final UnitResource unitResource = new UnitResource(stage);
    final Response unitResponse = unitResource.defineWith(organizationId, UnitData.just(UnitName, UnitDescription)).await();
    unitId = extractResourceIdFrom(unitResponse);

    final ContextResource contextResource = new ContextResource(stage);
    final Response contextResponse = contextResource.defineWith(organizationId, unitId, ContextData.just(ContextNamespace, ContextDescription)).await();
    contextId = extractResourceIdFrom(contextResponse);

    final SchemaResource schemaResource = new SchemaResource(stage);
    final Response schemaResponse = schemaResource.defineWith(organizationId, unitId, contextId, SchemaData.just(SchemaCategory, SchemaScope, SchemaName, SchemaDescription)).await();
    schemaId = extractResourceIdFrom(schemaResponse);

    final SchemaVersionResource schemaVersionResource = new SchemaVersionResource(stage);
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
    return String.join(Schemata.ReferenceSeparator, values);
  }
}
