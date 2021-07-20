package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.*;
import io.vlingo.xoom.schemata.model.Id.ContextId;
import io.vlingo.xoom.schemata.model.Id.SchemaId;
import io.vlingo.xoom.schemata.query.view.SchemasView;
import org.junit.Test;

import java.util.Collections;

import static io.vlingo.xoom.schemata.test.Assertions.assertCompletes;
import static io.vlingo.xoom.schemata.test.Assertions.assertOnNotNull;
import static org.junit.Assert.assertEquals;

public class SchemasProjectionTest extends ProjectionTest {

  @Test
  public void itCreatesSchemaViewOnContextDefinedEvent() {
    Completes<ContextState> contextState = givenAnyContext();
    Completes<SchemasView> view = contextState.andThenTo(state -> schemasView(state.contextId));
    assertCompletes(view, (schemasView) -> assertEquals(Collections.emptyList(), schemasView.all()));
  }

  @Test
  public void itAddsSchemaToSchemasViewOnSchemaDefinedEvent() {
    final Completes<SchemaState> schemaState = givenAnySchema();
    final Completes<SchemasView> view = schemaState.andThenTo(state -> schemasView(state.schemaId));
    final Completes<SchemaId> id = schemaState.andThen(state -> state.schemaId);

    assertCompletes(view, id, (schemasView, schemaId) ->
            assertOnNotNull(schemasView.get(schemaId.value), (schemaItem) -> {
              assertEquals(Fixtures.SchemaCategory.name(), schemaItem.category);
              assertEquals(Fixtures.SchemaScope.name(), schemaItem.scope);
              assertEquals(Fixtures.SchemaName, schemaItem.name);
              assertEquals(Fixtures.SchemaDescription, schemaItem.description);
            })
    );
  }

  @Test
  public void itUpdatesSchemasViewOnSchemaRedefinedEvent() {
    final Completes<SchemaState> schemaState = onceProjected(SchemasView.class, () ->
            givenAnySchema().andThenTo(state -> schema(state.schemaId)
                    .redefineWith(Category.Command, Scope.Private, "New name", "New description")
            )
    );
    final Completes<SchemasView> view = schemaState.andThenTo(state -> schemasView(state.schemaId));
    final Completes<SchemaId> id = schemaState.andThen(state -> state.schemaId);

    assertCompletes(view, id, (schemasView, schemaId) ->
            assertOnNotNull(schemasView.get(schemaId.value), (schemaItem) -> {
              assertEquals(Category.Command.name(), schemaItem.category);
              assertEquals(Scope.Private.name(), schemaItem.scope);
              assertEquals("New name", schemaItem.name);
              assertEquals("New description", schemaItem.description);
            })
    );
  }

  @Test
  public void itUpdatesSchemasViewOnSchemaRenamedEvent() {
    final Completes<SchemaState> schemaState = onceProjected(SchemasView.class, () ->
            givenAnySchema().andThenTo(state -> schema(state.schemaId)
                    .renameTo("New XOOM Schema")
            )
    );
    final Completes<SchemasView> view = schemaState.andThenTo(state -> schemasView(state.schemaId));
    final Completes<SchemaId> id = schemaState.andThen(state -> state.schemaId);

    assertCompletes(view, id, (schemasView, schemaId) ->
            assertOnNotNull(schemasView.get(schemaId.value), (schemaItem) ->
                    assertEquals("New XOOM Schema", schemaItem.name)
            )
    );
  }

  public Completes<SchemasView> schemasView(final SchemaId schemaId) {
    return schemasView(schemaId.contextId);
  }

  private Completes<SchemasView> schemasView(final ContextId contextId) {
    return StorageProvider.instance().schemaQueries.schemas(
            contextId.organizationId().value,
            contextId.unitId.value,
            contextId.value
    );
  }

  public Schema schema(final SchemaId schemaId) {
    return stage.actorFor(Schema.class, SchemaEntity.class, schemaId);
  }
}
