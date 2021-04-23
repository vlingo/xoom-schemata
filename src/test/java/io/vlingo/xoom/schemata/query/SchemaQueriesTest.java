package io.vlingo.xoom.schemata.query;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.TestWorld;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.common.Outcome;
import io.vlingo.xoom.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.xoom.schemata.Schemata;
import io.vlingo.xoom.schemata.model.Category;
import io.vlingo.xoom.schemata.model.Scope;
import io.vlingo.xoom.schemata.query.view.NamedSchemaView;
import io.vlingo.xoom.schemata.query.view.SchemaView;
import io.vlingo.xoom.symbio.Source;
import io.vlingo.xoom.symbio.store.Result;
import io.vlingo.xoom.symbio.store.StorageException;
import io.vlingo.xoom.symbio.store.dispatch.NoOpDispatcher;
import io.vlingo.xoom.symbio.store.state.StateStore;
import io.vlingo.xoom.symbio.store.state.inmemory.InMemoryStateStoreActor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class SchemaQueriesTest {
  private World world;
  private FailingStateStore stateStore;
  private SchemaQueries queries;

  @Test
  public void schemaReturnsTheSchemaIfFound() {
    givenSchemaView("vlingo", "schemata-test", "io.vlingo.xoom.schemata", "SchemaDefined", "First schema", "My first schema");

    Completes<SchemaView> query = queries.schema("vlingo", "schemata-test", "io.vlingo.xoom.schemata", "SchemaDefined");
    SchemaView view = query.await(1000);

    assertEquals("First schema", view.name());
  }

  @Test
  public void schemaReturnsNullIfNotFound() {
    Completes<SchemaView> query = queries.schema("vlingo", "schemata-test", "io.vlingo.xoom.schemata", "SchemaDefined");
    SchemaView view = query.await(1000);

    assertEquals(null, view);
  }

  @Test
  public void schemaByNamesReturnsTheSchemaIfFound() {
    givenNamedSchemaView("vlingo", "schemata-test", "io.vlingo.xoom.schemata", "SchemaDefined", "First schema", "My first schema");

    Completes<NamedSchemaView> query = queries.schemaByNames("vlingo", "schemata-test", "io.vlingo.xoom.schemata", "SchemaDefined");
    NamedSchemaView view = query.await(1000);

    assertEquals("First schema", view.name());
  }

  @Test
  public void schemaByNamesReturnsNullIfNotFound() {
    Completes<NamedSchemaView> query = queries.schemaByNames("vlingo", "schemata-test", "io.vlingo.xoom.schemata", "SchemaDefined");
    NamedSchemaView view = query.await(1000);

    assertEquals(null, view);
  }

  @Test
  public void schemaByNamesWithRetriesReturnsTheSchemaDespiteInitialFailures() {
    givenNamedSchemaView("vlingo", "schemata-test", "io.vlingo.xoom.schemata", "SchemaDefined", "First schema", "My first schema");
    givenStateReadFailures(3);

    Completes<NamedSchemaView> query = queries.schemaByNamesWithRetries(
            "vlingo",
            "schemata-test",
            "io.vlingo.xoom.schemata",
            "SchemaDefined",
            100,
            3
    );
    NamedSchemaView view = query.await(1000);

    assertEquals("First schema", view.name());
  }

  @Before
  public void init() {
    world = TestWorld.startWithDefaults("test-state-store-query").world();
    stateStore = new FailingStateStore(world.actorFor(StateStore.class, InMemoryStateStoreActor.class, Arrays.asList(new NoOpDispatcher())));
    queries = world.actorFor(SchemaQueries.class, SchemaQueriesActor.class, stateStore);
    StatefulTypeRegistry.registerAll(world, stateStore, SchemaView.class);
    StatefulTypeRegistry.registerAll(world, stateStore, NamedSchemaView.class);
  }

  @After
  public void shutdown() {
    if (world != null) {
      world.terminate();
    }
  }

  private void givenStateReadFailures(int failures) {
    stateStore.expectReadFailures(failures);
  }

  private void givenNamedSchemaView(String organizationId, String unitId, String contextId, String schemaId, String name, String description) {
    String reference = Arrays.asList(organizationId, unitId, contextId, schemaId).stream().collect(Collectors.joining(Schemata.ReferenceSeparator));
    stateStore.write(
            reference,
            NamedSchemaView.with(
                    reference,
                    SchemaView.with(organizationId, unitId, contextId, schemaId, Category.Event, Scope.Public, name, description)
            ),
            1,
            new NoOpWriteResultInterest()
    );
  }

  private void givenSchemaView(String organizationId, String unitId, String contextId, String schemaId, String name, String description) {
    String id = Arrays.asList(contextId, schemaId).stream().collect(Collectors.joining(Schemata.ReferenceSeparator));
    stateStore.write(
            id,
            SchemaView.with(organizationId, unitId, contextId, schemaId, Category.Event, Scope.Public, name, description),
            1,
            new NoOpWriteResultInterest()
    );
  }

  private class NoOpWriteResultInterest implements StateStore.WriteResultInterest {
    @Override
    public <S, C> void writeResultedIn(Outcome<StorageException, Result> outcome, String s, S s1, int i, List<Source<C>> list, Object o) {
    }
  }
}
