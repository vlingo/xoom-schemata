package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.Context;
import io.vlingo.xoom.schemata.model.ContextEntity;
import io.vlingo.xoom.schemata.model.ContextState;
import io.vlingo.xoom.schemata.model.Id.ContextId;
import io.vlingo.xoom.schemata.model.Id.UnitId;
import io.vlingo.xoom.schemata.query.view.ContextsView;
import io.vlingo.xoom.schemata.query.view.ContextsView.ContextItem;
import org.junit.Test;

import java.util.Collections;

import static io.vlingo.xoom.schemata.test.Assertions.assertCompletes;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ContextsProjectionTest extends ProjectionTest {

  @Test
  public void itCreatesTheContextsViewOnUnitDefineEvent() {
    final Completes<ContextsView> contextsView = givenAnyUnit().andThenTo(unit -> contextsView(unit.unitId));

    assertCompletes(contextsView, (contexts) -> assertEquals(Collections.emptyList(), contexts.all()));
  }

  @Test
  public void itAddsContextItemOnContextDefinedEvent() {
    final Completes<ContextState> contextState = givenAnyContext();
    final Completes<ContextsView> contextsView = contextState.andThenTo(context -> contextsView(context.contextId.unitId));
    final ContextId contextId = contextState.andThen(context -> context.contextId).await();

    assertCompletes(contextsView, (contexts) -> {
      final ContextItem contextItem = contexts.get(contextId.value);
      assertNotNull(contextItem);
      assertEquals(Fixtures.ContextNamespace, contextItem.namespace);
      assertEquals(Fixtures.ContextDescription, contextItem.description);
    });
  }

  @Test
  public void itReplacesContextItemOnContextRedefinedEvent() {
    final Completes<ContextState> contextState = onceProjected(ContextsView.class, () -> givenAnyContext()
            .andThenTo(context -> context(context.contextId).redefineWith(context.namespace, "XOOM Schemata")));
    final Completes<ContextsView> contextView = contextState.andThenTo(context -> contextsView(context.contextId.unitId));
    final ContextId contextId = contextState.andThen(context -> context.contextId).await();

    assertCompletes(contextView, (contexts) -> {
      final ContextItem contextItem = contexts.get(contextId.value);
      assertNotNull(contextItem);
      assertEquals("XOOM Schemata", contextItem.description);
    });
  }

  @Test
  public void itReplacesContextItemOnContextMovedToNamespaceEvent() {
    final Completes<ContextState> contextState = onceProjected(ContextsView.class, () -> givenAnyContext()
            .andThenTo(context -> context(context.contextId).moveToNamespace("io.vlingo.xoom.schemata.new")));
    final Completes<ContextsView> contextView = contextState.andThenTo(context -> contextsView(context.contextId.unitId));
    final ContextId contextId = contextState.andThen(context -> context.contextId).await();

    assertCompletes(contextView, (contexts) -> {
      final ContextItem contextItem = contexts.get(contextId.value);
      assertNotNull(contextItem);
      assertEquals("io.vlingo.xoom.schemata.new", contextItem.namespace);
    });
  }

  private Context context(ContextId contextId) {
    return stage.actorFor(Context.class, ContextEntity.class, contextId);
  }

  private Completes<ContextsView> contextsView(UnitId unitId) {
    return StorageProvider.instance().contextQueries.contexts(unitId.organizationId.value, unitId.value);
  }
}
