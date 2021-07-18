package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.*;
import io.vlingo.xoom.schemata.model.Id.SchemaId;
import io.vlingo.xoom.schemata.query.view.SchemaView;
import org.junit.Test;

import static io.vlingo.xoom.schemata.test.Assertions.assertCompletes;
import static org.junit.Assert.assertEquals;

public class SchemaProjectionTest extends ProjectionTest {

  @Test
  public void itCreatesSchemaViewOnSchemaDefinedEvent() {
    final Completes<SchemaState> schemaState = givenAnySchema();
    final Completes<SchemaView> view = schemaState.andThenTo(state -> schemaView(state.schemaId));

    assertCompletes(view, (schemaView) -> {
      assertEquals(Fixtures.SchemaCategory, schemaView.category());
      assertEquals(Fixtures.SchemaName, schemaView.name());
      assertEquals(Fixtures.SchemaDescription, schemaView.description());
      assertEquals(Fixtures.SchemaScope, schemaView.scope());
    });
  }

  @Test
  public void itUpdatesTheSchemaViewOnSchemaCategorisedEvent() {
    final Completes<SchemaState> schemaState = onceProjected(SchemaView.class, () ->
            givenAnySchema().andThenTo(state -> schema(state.schemaId).categorizeAs(Category.Command))
    );
    final Completes<SchemaView> view = schemaState.andThenTo(state -> schemaView(state.schemaId));

    assertCompletes(view, (schemaView) -> assertEquals(Category.Command, schemaView.category()));
  }

  @Test
  public void itUpdatesTheSchemaViewOnSchemaScopedEvent() {
    final Completes<SchemaState> schemaState = onceProjected(SchemaView.class, () ->
            givenAnySchema().andThenTo(state -> schema(state.schemaId).scopeAs(Scope.Private))
    );
    final Completes<SchemaView> view = schemaState.andThenTo(state -> schemaView(state.schemaId));

    assertCompletes(view, (schemaView) -> assertEquals(Scope.Private, schemaView.scope()));
  }

  @Test
  public void itUpdatesTheSchemaViewOnSchemaRenamedEvent() {
    final Completes<SchemaState> schemaState = onceProjected(SchemaView.class, () ->
            givenAnySchema().andThenTo(state -> schema(state.schemaId).renameTo("New schema"))
    );
    final Completes<SchemaView> view = schemaState.andThenTo(state -> schemaView(state.schemaId));

    assertCompletes(view, (schemaView) -> assertEquals("New schema", schemaView.name()));
  }

  @Test
  public void itUpdatesTheSchemaViewOnSchemaDescribedEvent() {
    final Completes<SchemaState> schemaState = onceProjected(SchemaView.class, () ->
            givenAnySchema().andThenTo(state -> schema(state.schemaId).describeAs("New description"))
    );
    final Completes<SchemaView> view = schemaState.andThenTo(state -> schemaView(state.schemaId));

    assertCompletes(view, (schemaView) -> assertEquals("New description", schemaView.description()));
  }

  @Test
  public void itUpdatesTheSchemaViewOnSchemaRedefinedEvent() {
    final Completes<SchemaState> schemaState = onceProjected(SchemaView.class, () ->
            givenAnySchema().andThenTo(state -> schema(state.schemaId).redefineWith(
                    Category.Command,
                    Scope.Private,
                    "New name",
                    "New description"
            ))
    );
    final Completes<SchemaView> view = schemaState.andThenTo(state -> schemaView(state.schemaId));

    assertCompletes(view, (schemaView) -> {
      assertEquals(Category.Command, schemaView.category());
      assertEquals(Scope.Private, schemaView.scope());
      assertEquals("New name", schemaView.name());
      assertEquals("New description", schemaView.description());
    });
  }

  public Completes<SchemaView> schemaView(final SchemaId schemaId) {
    return StorageProvider.instance().schemaQueries.schema(
            schemaId.organizationId().value,
            schemaId.unitId().value,
            schemaId.contextId.value,
            schemaId.value
    );
  }

  public Schema schema(final SchemaId schemaId) {
    return stage.actorFor(Schema.class, SchemaEntity.class, schemaId);
  }
}
