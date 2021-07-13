package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.Context;
import io.vlingo.xoom.schemata.model.ContextEntity;
import io.vlingo.xoom.schemata.model.ContextState;
import io.vlingo.xoom.schemata.model.Id.ContextId;
import io.vlingo.xoom.schemata.query.view.ContextView;
import org.junit.Test;

import static io.vlingo.xoom.schemata.infra.persistence.Fixtures.ContextDescription;
import static io.vlingo.xoom.schemata.infra.persistence.Fixtures.ContextNamespace;
import static io.vlingo.xoom.schemata.test.Assertions.assertCompletes;
import static org.junit.Assert.assertEquals;

public class ContextProjectionTest extends ProjectionTest {

  @Test
  public void itCreatesTheContextViewOnContextDefinedEvent() {

    final Completes<ContextState> contextState = onceProjected(ContextView.class, () -> givenAnyUnit()
            .andThenTo(u -> Context.with(
                    stage,
                    u.unitId,
                    ContextNamespace,
                    ContextDescription
            ))
    );
    final Completes<ContextView> contextView = contextState.andThenTo(this::contextView);

    assertCompletes(contextView, (context) -> {
      assertEquals(ContextNamespace, context.namespace());
      assertEquals(ContextDescription, context.description());
    });
  }

  @Test
  public void itRedefinesTheContextViewOnContextRedefinedEvent() {

    final Completes<ContextState> contextState = onceProjected(ContextView.class, () -> givenAnyContext()
            .andThenTo(state -> context(state.contextId)
                    .redefineWith("io.vlingo.xoom.schemata.new", "XOOM Schemata")
            )
    );
    final Completes<ContextView> contextView = contextState.andThenTo(this::contextView);

    assertCompletes(contextView, (context) -> {
      assertEquals("io.vlingo.xoom.schemata.new", context.namespace());
      assertEquals("XOOM Schemata", context.description());
    });
  }

  @Test
  public void itMovesContextViewNamespaceOnContextMovedToNamespaceEvent() {

    final Completes<ContextState> contextState = onceProjected(ContextView.class, () -> givenAnyContext()
            .andThenTo(state -> context(state.contextId)
                    .moveToNamespace("io.vlingo.xoom.schemata.new")
            )
    );
    final Completes<ContextView> contextView = contextState.andThenTo(this::contextView);

    assertCompletes(contextView, (context) -> assertEquals("io.vlingo.xoom.schemata.new", context.namespace()));
  }

  @Test
  public void itUpdatesDescriptionOnContextDescribedEvent() {

    final Completes<ContextState> contextState = onceProjected(ContextView.class, () -> givenAnyContext()
            .andThenTo(state -> context(state.contextId)
                    .describeAs("XOOM Team")
            )
    );
    final Completes<ContextView> contextView = contextState.andThenTo(this::contextView);

    assertCompletes(contextView, (context) -> assertEquals("XOOM Team", context.description()));
  }

  private Context context(ContextId contextId) {
    return stage.actorFor(Context.class, ContextEntity.class, contextId);
  }

  private Completes<ContextView> contextView(ContextState state) {
    return StorageProvider.instance().contextQueries.context(
            state.contextId.organizationId().value,
            state.contextId.unitId.value,
            state.contextId.value
    );
  }
}
