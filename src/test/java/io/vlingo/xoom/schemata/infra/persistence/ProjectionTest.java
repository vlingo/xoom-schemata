package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.actors.Configuration;
import io.vlingo.xoom.cluster.ClusterProperties;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.grid.Grid;
import io.vlingo.xoom.schemata.Schemata;
import io.vlingo.xoom.schemata.SchemataConfig;
import io.vlingo.xoom.schemata.model.*;
import io.vlingo.xoom.schemata.query.view.*;
import io.vlingo.xoom.schemata.test.symbio.FakeStateStoreDispatcher;
import org.junit.After;
import org.junit.Before;

import java.util.Collections;
import java.util.function.Supplier;

import static io.vlingo.xoom.schemata.infra.persistence.Fixtures.*;

public abstract class ProjectionTest {
  protected Grid stage;
  private FakeStateStoreDispatcher stateStoreDispatcher;

  protected <R> R onceProjected(Class<?> projectionType, Supplier<R> supplier) {
    return stateStoreDispatcher.onceProjected(projectionType, supplier);
  }

  protected Completes<SchemaVersionState> givenAnySchemaVersion() {
    return givenAnySchema()
            .andThenTo(this::givenAnySchemaVersion);
  }

  protected Completes<SchemaVersionState> givenAnySchemaVersion(SchemaState schemaState) {
    return onceProjected(CodeView.class, () -> SchemaVersion.with(
            stage,
            schemaState.schemaId,
            SchemaVersionSpecification,
            SchemaVersionDescription,
            SchemaVersionVersion000,
            SchemaVersionVersion100
    ));
  }

  protected Completes<SchemaState> givenAnySchema() {
    return givenAnyOrganization()
            .andThenTo(this::givenAnyUnit)
            .andThenTo(this::givenAnyContext)
            .andThenTo(this::givenAnySchema);
  }

  protected Completes<SchemaState> givenAnySchema(ContextState context) {
    return onceProjected(SchemaView.class, () -> Schema.with(
            stage,
            context.contextId,
            Fixtures.SchemaCategory,
            Fixtures.SchemaScope,
            Fixtures.SchemaName,
            Fixtures.SchemaDescription
    ));
  }

  protected Completes<ContextState> givenAnyContext(UnitState unit) {
    return onceProjected(ContextView.class, () -> Context.with(
            stage,
            unit.unitId,
            Fixtures.ContextNamespace,
            Fixtures.ContextDescription
    ));
  }

  protected Completes<UnitState> givenAnyUnit(OrganizationState organization) {
    return onceProjected(UnitView.class, () -> Unit.with(
            stage,
            organization.organizationId,
            Fixtures.UnitName,
            Fixtures.UnitDescription
    ));
  }

  protected Completes<OrganizationState> givenAnyOrganization() {
    return onceProjected(OrganizationView.class, () -> Organization.with(stage, OrgName, OrgDescription));
  }

  @Before
  public void setUp() throws Exception {
    stage = Grid.start("test-projection", Configuration.define(), ClusterProperties.oneNode(), Schemata.NodeName);
    stateStoreDispatcher = new FakeStateStoreDispatcher(stage.world().defaultLogger());

    final SchemataConfig config = SchemataConfig.forRuntime(SchemataConfig.RUNTIME_TYPE_DEV);
    final StateStoreProvider stateStoreProvider = StateStoreProvider.using(stage.world(), config, Collections.singletonList(stateStoreDispatcher));
    final ProjectionDispatcherProvider projectionDispatcherProvider = ProjectionDispatcherProvider.using(stage, stateStoreProvider.stateStore);

    StorageProvider.newInstance(stage.world(), stateStoreProvider.stateStore, projectionDispatcherProvider.storeDispatcher, config);
  }

  @After
  public void tearDown() {
    stage.world().terminate();
  }
}
