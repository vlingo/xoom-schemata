package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.Id.SchemaId;
import io.vlingo.xoom.schemata.model.Id.SchemaVersionId;
import io.vlingo.xoom.schemata.model.SchemaVersion;
import io.vlingo.xoom.schemata.model.SchemaVersion.Specification;
import io.vlingo.xoom.schemata.model.SchemaVersionEntity;
import io.vlingo.xoom.schemata.model.SchemaVersionState;
import io.vlingo.xoom.schemata.query.view.SchemaVersionsView;
import org.junit.Test;

import java.util.Collections;

import static io.vlingo.xoom.schemata.test.Assertions.assertCompletes;
import static io.vlingo.xoom.schemata.test.Assertions.assertOnNotNull;
import static org.junit.Assert.assertEquals;

public class SchemaVersionsProjectionTest extends ProjectionTest {

  @Test
  public void itCreatesSchemaVersionsViewOnSchemaDefinedEvent() {
    final Completes<SchemaVersionsView> schemaVersionsView = givenAnySchema().andThenTo(state -> schemaVersionsView(state.schemaId));

    assertCompletes(schemaVersionsView, (view) -> assertEquals(Collections.emptyList(), view.all()));
  }

  @Test
  public void itUpdatesSchemaVersionsViewOnSchemaVersionDefinedEvent() {
    final Completes<SchemaVersionState> schemaVersionState = givenAnySchemaVersion();
    final Completes<SchemaVersionsView> schemaVersionsView = schemaVersionState.andThenTo(state -> schemaVersionsView(state.schemaVersionId));
    final Completes<SchemaVersionId> schemaVersionId = schemaVersionState.andThen(state -> state.schemaVersionId);

    assertCompletes(schemaVersionsView, schemaVersionId, (view, id) ->
            assertOnNotNull(view.get(id.value), (schemaVersionView) -> {
              assertEquals(Fixtures.SchemaVersionDescription, schemaVersionView.description());
              assertEquals(Fixtures.SchemaVersionSpecification.value, schemaVersionView.specification());
              assertEquals(Fixtures.SchemaVersionVersion000.value, schemaVersionView.previousVersion());
              assertEquals(Fixtures.SchemaVersionVersion100.value, schemaVersionView.currentVersion());
              assertEquals(SchemaVersion.Status.Draft.value, schemaVersionView.status());
            })
    );
  }

  @Test
  public void itUpdatesSchemaVersionsViewOnSchemaVersionDescribedEvent() {
    final Completes<SchemaVersionState> schemaVersionState = onceProjected(SchemaVersionsView.class, () -> givenAnySchemaVersion()
            .andThenTo(state -> schemaVersion(state.schemaVersionId).describeAs("New description")));
    final Completes<SchemaVersionsView> schemaVersionsView = schemaVersionState
            .andThenTo(state -> schemaVersionsView(state.schemaVersionId));
    final Completes<SchemaVersionId> schemaVersionId = schemaVersionState.andThen(state -> state.schemaVersionId);

    assertCompletes(schemaVersionsView, schemaVersionId, (view, id) ->
            assertOnNotNull(view.get(id.value), (schemaVersionView) ->
                    assertEquals("New description", schemaVersionView.description())
            )
    );
  }

  @Test
  public void itUpdatesSchemaVersionsViewOnSchemaVersionSpecifiedEvent() {
    final String newSpecification = "event SchemaDefined { type eventType }";
    final Completes<SchemaVersionState> schemaVersionState = onceProjected(SchemaVersionsView.class, () -> givenAnySchemaVersion()
            .andThenTo(state -> schemaVersion(state.schemaVersionId).specifyWith(Specification.of(newSpecification))));
    final Completes<SchemaVersionsView> schemaVersionsView = schemaVersionState
            .andThenTo(state -> schemaVersionsView(state.schemaVersionId));
    final Completes<SchemaVersionId> schemaVersionId = schemaVersionState.andThen(state -> state.schemaVersionId);

    assertCompletes(schemaVersionsView, schemaVersionId, (view, id) ->
            assertOnNotNull(view.get(id.value), (schemaVersionView) ->
                    assertEquals(newSpecification, schemaVersionView.specification())
            )
    );
  }

  @Test
  public void itUpdatesSchemaVersionsViewOnSchemaVersionPublishedEvent() {
    final Completes<SchemaVersionState> schemaVersionState = onceProjected(SchemaVersionsView.class, () -> givenAnySchemaVersion()
            .andThenTo(state -> schemaVersion(state.schemaVersionId).publish()));
    final Completes<SchemaVersionsView> schemaVersionsView = schemaVersionState
            .andThenTo(state -> schemaVersionsView(state.schemaVersionId));
    final Completes<SchemaVersionId> schemaVersionId = schemaVersionState.andThen(state -> state.schemaVersionId);

    assertCompletes(schemaVersionsView, schemaVersionId, (view, id) ->
            assertOnNotNull(view.get(id.value), (schemaVersionView) ->
                    assertEquals(SchemaVersion.Status.Published.value, schemaVersionView.status())
            )
    );
  }

  @Test
  public void itUpdatesSchemaVersionsViewOnSchemaVersionDeprecatedEvent() {
    final Completes<SchemaVersionState> schemaVersionState = givenAnySchemaVersion();
    onceProjected(SchemaVersionsView.class, () -> schemaVersionState.andThenTo(state -> schemaVersion(state.schemaVersionId).publish()));
    onceProjected(SchemaVersionsView.class, () -> schemaVersionState.andThenTo(state -> schemaVersion(state.schemaVersionId).deprecate()));
    final Completes<SchemaVersionsView> schemaVersionsView = schemaVersionState
            .andThenTo(state -> schemaVersionsView(state.schemaVersionId));
    final Completes<SchemaVersionId> schemaVersionId = schemaVersionState.andThen(state -> state.schemaVersionId);

    assertCompletes(schemaVersionsView, schemaVersionId, (view, id) ->
            assertOnNotNull(view.get(id.value), (schemaVersionView) ->
                    assertEquals(SchemaVersion.Status.Deprecated.value, schemaVersionView.status())
            )
    );
  }

  @Test
  public void itUpdatesSchemaVersionsViewOnSchemaVersionRemovedEvent() {
    final Completes<SchemaVersionState> schemaVersionState = givenAnySchemaVersion();
    onceProjected(SchemaVersionsView.class, () -> schemaVersionState.andThenTo(state -> schemaVersion(state.schemaVersionId).publish()));
    onceProjected(SchemaVersionsView.class, () -> schemaVersionState.andThenTo(state -> schemaVersion(state.schemaVersionId).deprecate()));
    onceProjected(SchemaVersionsView.class, () -> schemaVersionState.andThenTo(state -> schemaVersion(state.schemaVersionId).remove()));
    final Completes<SchemaVersionsView> schemaVersionsView = schemaVersionState
            .andThenTo(state -> schemaVersionsView(state.schemaVersionId));
    final Completes<SchemaVersionId> schemaVersionId = schemaVersionState.andThen(state -> state.schemaVersionId);

    assertCompletes(schemaVersionsView, schemaVersionId, (view, id) ->
            assertOnNotNull(view.get(id.value), (schemaVersionView) ->
                    assertEquals(SchemaVersion.Status.Removed.value, schemaVersionView.status())
            )
    );
  }

  private Completes<SchemaVersionsView> schemaVersionsView(final SchemaId schemaId) {
    return StorageProvider.instance().schemaVersionQueries.schemaVersionsByIds(
            schemaId.organizationId().value,
            schemaId.unitId().value,
            schemaId.contextId.value,
            schemaId.value
    );
  }

  private Completes<SchemaVersionsView> schemaVersionsView(final SchemaVersionId schemaVersionId) {
    return schemaVersionsView(schemaVersionId.schemaId);
  }

  private SchemaVersion schemaVersion(final SchemaVersionId schemaVersionId) {
    return stage.actorFor(SchemaVersion.class, SchemaVersionEntity.class, schemaVersionId);
  }
}
