package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.Id.UnitId;
import io.vlingo.xoom.schemata.model.Unit;
import io.vlingo.xoom.schemata.model.UnitEntity;
import io.vlingo.xoom.schemata.model.UnitState;
import io.vlingo.xoom.schemata.query.view.UnitView;
import org.junit.Test;

import static io.vlingo.xoom.schemata.test.Assertions.assertCompletes;
import static org.junit.Assert.assertEquals;

public class UnitProjectionTest extends ProjectionTest {

  @Test
  public void itCreatesUnitViewOnUnitDefinedEvent() {
    final Completes<UnitView> unitView = givenAnyUnit().andThenTo(state -> unitView(state.unitId));

    assertCompletes(unitView, (view) -> {
      assertEquals(Fixtures.UnitName, view.name());
      assertEquals(Fixtures.UnitDescription, view.description());
    });
  }

  @Test
  public void itUpdatesUnitViewOnUnitDescribedEvent() {
    final Completes<UnitState> unitState = onceProjected(UnitView.class, () -> givenAnyUnit()
            .andThenTo(state -> unit(state.unitId).describeAs("New UNIT")));
    final Completes<UnitView> unitView = unitState.andThenTo(state -> unitView(state.unitId));

    assertCompletes(unitView, (view) -> assertEquals("New UNIT", view.description()));
  }

  @Test
  public void itUpdatesUnitViewOnUnitRedefinedEvent() {
    final Completes<UnitState> unitState = onceProjected(UnitView.class, () -> givenAnyUnit()
            .andThenTo(state -> unit(state.unitId).redefineWith("New Name", "New UNIT")));
    final Completes<UnitView> unitView = unitState.andThenTo(state -> unitView(state.unitId));

    assertCompletes(unitView, (view) -> {
      assertEquals("New Name", view.name());
      assertEquals("New UNIT", view.description());
    });
  }

  @Test
  public void itUpdatesUnitViewOnUnitRenamedEvent() {
    final Completes<UnitState> unitState = onceProjected(UnitView.class, () -> givenAnyUnit()
            .andThenTo(state -> unit(state.unitId).renameTo("New Name")));
    final Completes<UnitView> unitView = unitState.andThenTo(state -> unitView(state.unitId));

    assertCompletes(unitView, (view) -> assertEquals("New Name", view.name()));
  }

  private Completes<UnitView> unitView(UnitId unitId) {
    return StorageProvider.instance().unitQueries.unit(unitId.organizationId.value, unitId.value);
  }

  private Unit unit(UnitId unitId) {
    return stage.actorFor(Unit.class, UnitEntity.class, unitId);
  }
}
