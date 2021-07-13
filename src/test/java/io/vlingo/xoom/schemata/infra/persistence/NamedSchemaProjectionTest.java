package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.SchemaState;
import io.vlingo.xoom.schemata.model.SchemaVersionState;
import io.vlingo.xoom.schemata.query.view.NamedSchemaView;
import org.junit.Test;

import java.util.Collections;

import static io.vlingo.xoom.schemata.test.Assertions.assertCompletes;
import static org.junit.Assert.assertEquals;

public class NamedSchemaProjectionTest extends ProjectionTest {

  @Test
  public void itCreatesNamedSchemaViewOnSchemaDefinedEvent() {
    Completes<SchemaState> schemaState = givenAnySchema();
    Completes<NamedSchemaView> schemaView = schemaState.andThenTo(state -> namedSchemaView(
            Fixtures.OrgName, Fixtures.UnitName, Fixtures.ContextNamespace, Fixtures.SchemaName
    ));

    assertCompletes(schemaView, (view) -> {
      assertEquals(Fixtures.SchemaName, view.name());
      assertEquals(Collections.emptyList(), view.schemaVersions());
    });
  }

  @Test
  public void itNamedSchemaViewWithSchemaVersionOnSchemaVersionDefinedEvent() {
    Completes<SchemaVersionState> schemaVersionState = givenAnySchemaVersion();
    Completes<NamedSchemaView> schemaView = schemaVersionState.andThenTo(state -> namedSchemaView(
            Fixtures.OrgName, Fixtures.UnitName, Fixtures.ContextNamespace, Fixtures.SchemaName
    ));

    assertCompletes(schemaView, (view) -> {
      assertEquals(1, view.schemaVersions().size());
      assertEquals(Fixtures.SchemaVersionSpecification.value, view.schemaVersions().get(0).specification());
    });
  }

  @SuppressWarnings("SameParameterValue")
  private Completes<NamedSchemaView> namedSchemaView(final String orgName, final String unitName, final String contextNamespace, final String schemaName) {
    return StorageProvider.instance().schemaQueries.schemaByNames(orgName, unitName, contextNamespace, schemaName);
  }
}
