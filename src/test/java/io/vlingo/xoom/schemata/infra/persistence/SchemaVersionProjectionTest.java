package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.Id.SchemaVersionId;
import io.vlingo.xoom.schemata.model.SchemaVersion;
import io.vlingo.xoom.schemata.model.SchemaVersion.Specification;
import io.vlingo.xoom.schemata.model.SchemaVersionEntity;
import io.vlingo.xoom.schemata.model.SchemaVersionState;
import io.vlingo.xoom.schemata.query.view.SchemaVersionView;
import org.junit.Test;

import static io.vlingo.xoom.schemata.test.Assertions.assertCompletes;
import static org.junit.Assert.assertEquals;

public class SchemaVersionProjectionTest extends ProjectionTest {

  @Test
  public void itCreatesSchemaVersionViewOnSchemaVersionDefinedEvent() {
    final Completes<SchemaVersionView> schemaVersionView = givenAnySchemaVersion()
            .andThenTo(state -> schemaVersionView(state.schemaVersionId));

    assertCompletes(schemaVersionView, (view) -> {
      assertEquals(Fixtures.SchemaVersionDescription, view.description());
      assertEquals(Fixtures.SchemaVersionSpecification.value, view.specification());
      assertEquals(Fixtures.SchemaVersionVersion000.value, view.previousVersion());
      assertEquals(Fixtures.SchemaVersionVersion100.value, view.currentVersion());
      assertEquals(SchemaVersion.Status.Draft.value, view.status());
    });
  }

  @Test
  public void itUpdatesSchemaVersionViewOnSchemaVersionDescribedEvent() {
    final Completes<SchemaVersionState> schemaVersionState = onceProjected(SchemaVersionView.class, () -> givenAnySchemaVersion()
            .andThenTo(state -> schemaVersion(state.schemaVersionId).describeAs("New description")));
    final Completes<SchemaVersionView> schemaVersionView = schemaVersionState
            .andThenTo(state -> schemaVersionView(state.schemaVersionId));

    assertCompletes(schemaVersionView, (view) -> assertEquals("New description", view.description()));
  }

  @Test
  public void itUpdatesSchemaVersionOnSchemaVersionSpecifiedEvent() {
    final String newSpecification = "event SchemaDefined { type eventType }";
    final Completes<SchemaVersionState> schemaVersionState = onceProjected(SchemaVersionView.class, () -> givenAnySchemaVersion()
            .andThenTo(state -> schemaVersion(state.schemaVersionId).specifyWith(Specification.of(newSpecification))));
    final Completes<SchemaVersionView> schemaVersionView = schemaVersionState
            .andThenTo(state -> schemaVersionView(state.schemaVersionId));

    assertCompletes(schemaVersionView, (view) -> assertEquals(newSpecification, view.specification()));
  }

  @Test
  public void itUpdatesSchemaVersionOnSchemaVersionPublishedEvent() {
    final Completes<SchemaVersionState> schemaVersionState = onceProjected(SchemaVersionView.class, () -> givenAnySchemaVersion()
            .andThenTo(state -> schemaVersion(state.schemaVersionId).publish()));
    final Completes<SchemaVersionView> schemaVersionView = schemaVersionState
            .andThenTo(state -> schemaVersionView(state.schemaVersionId));

    assertCompletes(schemaVersionView, (view) -> assertEquals(SchemaVersion.Status.Published.value, view.status()));
  }

  @Test
  public void itUpdatesSchemaVersionOnSchemaVersionDeprecatedEvent() {
    final Completes<SchemaVersionState> schemaVersionState = givenAnySchemaVersion();
    onceProjected(SchemaVersionView.class, () -> schemaVersionState.andThenTo(state -> schemaVersion(state.schemaVersionId).publish()));
    onceProjected(SchemaVersionView.class, () -> schemaVersionState.andThenTo(state -> schemaVersion(state.schemaVersionId).deprecate()));
    final Completes<SchemaVersionView> schemaVersionView = schemaVersionState.andThenTo(state -> schemaVersionView(state.schemaVersionId));

    assertCompletes(schemaVersionView, (view) -> assertEquals(SchemaVersion.Status.Deprecated.value, view.status()));
  }

  @Test
  public void itUpdatesSchemaVersionOnSchemaVersionRemovedEvent() {
    final Completes<SchemaVersionState> schemaVersionState = givenAnySchemaVersion();
    onceProjected(SchemaVersionView.class, () -> schemaVersionState.andThenTo(state -> schemaVersion(state.schemaVersionId).publish()));
    onceProjected(SchemaVersionView.class, () -> schemaVersionState.andThenTo(state -> schemaVersion(state.schemaVersionId).deprecate()));
    onceProjected(SchemaVersionView.class, () -> schemaVersionState.andThenTo(state -> schemaVersion(state.schemaVersionId).remove()));
    final Completes<SchemaVersionView> schemaVersionView = schemaVersionState.andThenTo(state -> schemaVersionView(state.schemaVersionId));

    assertCompletes(schemaVersionView, (view) -> assertEquals(SchemaVersion.Status.Removed.value, view.status()));
  }

  private Completes<SchemaVersionView> schemaVersionView(final SchemaVersionId schemaVersionId) {
    return StorageProvider.instance().schemaVersionQueries.schemaVersion(
            schemaVersionId.organizationId().value,
            schemaVersionId.unitId().value,
            schemaVersionId.contextId().value,
            schemaVersionId.schemaId.value,
            schemaVersionId.value
    );
  }

  private SchemaVersion schemaVersion(final SchemaVersionId schemaVersionId) {
    return stage.actorFor(SchemaVersion.class, SchemaVersionEntity.class, schemaVersionId);
  }
}
