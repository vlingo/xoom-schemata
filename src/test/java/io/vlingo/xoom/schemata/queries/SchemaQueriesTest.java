package io.vlingo.xoom.schemata.queries;

import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.actors.testkit.TestWorld;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.xoom.schemata.query.SchemaQueries;
import io.vlingo.xoom.schemata.query.SchemaQueriesActor;
import io.vlingo.xoom.schemata.query.view.NamedSchemaView;
import io.vlingo.xoom.symbio.store.dispatch.NoOpDispatcher;
import io.vlingo.xoom.symbio.store.state.inmemory.InMemoryStateStoreActor;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class SchemaQueriesTest {

  private World world;
  private SchemaQueries queries;
  private CountingStateStore stateStore;

  @Before
  public void init() {
    TestWorld.startWithDefaults("test-state-store-query");
    world = World.startWithDefaults("test-state-store-query");
    stateStore = new CountingStateStore(new InMemoryStateStoreActor<>(Arrays.asList(new NoOpDispatcher())));
    StatefulTypeRegistry.registerAll(world, stateStore, NamedSchemaView.class);
    queries = world.actorFor(SchemaQueries.class, SchemaQueriesActor.class, stateStore);
  }

  @Test
  public void schemaByNames() {
    Completes<NamedSchemaView> res = queries.schemaByNames("OO", "UU", "CC", "SS");
    res.await();
    int readCount = stateStore.getReadCount();
    assertEquals(1, readCount);
  }

  @Test
  public void schemaByNamesWithRetries() {
    Completes<NamedSchemaView> res = queries.schemaByNamesWithRetries("OO", "UU", "CC", "SS", 100, 3);
    res.await();
    int readCount = stateStore.getReadCount();
    assertEquals(3, readCount);
  }
}
