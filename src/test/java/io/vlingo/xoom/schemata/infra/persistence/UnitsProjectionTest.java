package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.Id.OrganizationId;
import io.vlingo.xoom.schemata.model.Id.UnitId;
import io.vlingo.xoom.schemata.model.Unit;
import io.vlingo.xoom.schemata.model.UnitEntity;
import io.vlingo.xoom.schemata.model.UnitState;
import io.vlingo.xoom.schemata.query.view.UnitsView;
import org.junit.Test;

import java.util.Collections;

import static io.vlingo.xoom.schemata.test.Assertions.assertCompletes;
import static io.vlingo.xoom.schemata.test.Assertions.assertOnNotNull;
import static org.junit.Assert.assertEquals;

public class UnitsProjectionTest extends ProjectionTest {

  @Test
  public void itCreatesUnitsViewOnOrganizationDefinedEvent() {
    final Completes<UnitsView> unitsView = givenAnyOrganization().andThenTo(state -> unitsView(state.organizationId));

    assertCompletes(unitsView, (view) -> assertEquals(Collections.emptyList(), view.all()));
  }

  @Test
  public void itUpdatesUnitsViewOnUnitDefinedEvent() {
    final Completes<UnitState> unitState = givenAnyUnit();
    final Completes<UnitsView> unitsView = unitState.andThenTo(state -> unitsView(state.unitId));
    final Completes<UnitId> unitId = unitState.andThen(state -> state.unitId);

    assertCompletes(unitsView, unitId, (view, id) ->
            assertOnNotNull(view.get(id.value), (unitItem) -> {
              assertEquals(Fixtures.UnitName, unitItem.name);
              assertEquals(Fixtures.UnitDescription, unitItem.description);
            })
    );
  }

  @Test
  public void itUpdatesUnitsViewOnUnitRedefinedEvent() {
    final Completes<UnitState> unitState = onceProjected(UnitsView.class, () -> givenAnyUnit()
            .andThenTo(state -> unit(state.unitId).redefineWith("New name", "New description")));
    final Completes<UnitsView> unitsView = unitState.andThenTo(state -> unitsView(state.unitId));
    final Completes<UnitId> unitId = unitState.andThen(state -> state.unitId);

    assertCompletes(unitsView, unitId, (view, id) ->
            assertOnNotNull(view.get(id.value), (unitItem) -> {
              assertEquals("New name", unitItem.name);
              assertEquals("New description", unitItem.description);
            })
    );
  }

  @Test
  public void itUpdatesUnitsViewOnUnitRenamedEvent() {
    final Completes<UnitState> unitState = onceProjected(UnitsView.class, () -> givenAnyUnit()
            .andThenTo(state -> unit(state.unitId).renameTo("New name")));
    final Completes<UnitsView> unitsView = unitState.andThenTo(state -> unitsView(state.unitId));
    final Completes<UnitId> unitId = unitState.andThen(state -> state.unitId);

    assertCompletes(unitsView, unitId, (view, id) ->
            assertOnNotNull(view.get(id.value), (unitItem) ->
                    assertEquals("New name", unitItem.name)
            )
    );
  }

  private Completes<UnitsView> unitsView(UnitId unitId) {
    return unitsView(unitId.organizationId);
  }

  private Completes<UnitsView> unitsView(OrganizationId organizationId) {
    return StorageProvider.instance().unitQueries.units(organizationId.value);
  }

  private Unit unit(UnitId unitId) {
    return stage.actorFor(Unit.class, UnitEntity.class, unitId);
  }
}
