// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.function.BiConsumer;

import io.vlingo.actors.testkit.TestState;
import io.vlingo.lattice.model.sourcing.EventSourced;
import io.vlingo.schemata.model.Events.UnitDefined;
import io.vlingo.schemata.model.Events.UnitDescribed;
import io.vlingo.schemata.model.Events.UnitRenamed;
import io.vlingo.schemata.model.Id.UnitId;

public class UnitEntity extends EventSourced implements Unit {
    static {
        BiConsumer<UnitEntity, UnitDefined> applyOrganizationDefinedFn = UnitEntity::applyDefined;
        EventSourced.registerConsumer(UnitEntity.class, UnitDefined.class, applyOrganizationDefinedFn);
        BiConsumer<UnitEntity, UnitDescribed> applyOrganizationDescribedFn = UnitEntity::applyDescribed;
        EventSourced.registerConsumer(UnitEntity.class, UnitDescribed.class, applyOrganizationDescribedFn);
        BiConsumer<UnitEntity, UnitRenamed> applyOrganizationRenamedFn = UnitEntity::applyRenamed;
        EventSourced.registerConsumer(UnitEntity.class, UnitRenamed.class, applyOrganizationRenamedFn);
    }

    private UnitEntity.State state;

    public UnitEntity(final UnitId unitId, final String name, final String description) {
        apply(new UnitDefined(unitId, name, description));
    }

    @Override
    public void describeAs(final String description) {
        apply(new UnitDescribed(state.unitId, description));
    }

    @Override
    public void renameTo(final String name) {
        apply(new UnitRenamed(state.unitId, name));
    }

    private void applyDefined(final UnitDefined e) {
        state = new State(UnitId.existing(e.unitId), e.name, e.description);
    }

    public final void applyDescribed(UnitDescribed event) {
        state = state.withDescription(event.description);
    }

    public final void applyRenamed(UnitRenamed event) {
        state = state.withName(event.name);
    }

    public class State {
        public final UnitId unitId;
        public final String name;
        public final String description;

        public UnitEntity.State withDescription(final String description) {
            return new State(this.unitId, this.name, description);
        }

        public State withName(final String name) {
            return new State(this.unitId, name, this.description);
        }

        public State(final UnitId unitId, final String name, final String description) {
            this.unitId = unitId;
            this.name = name;
            this.description = description;
        }
    }

    @Override
    public TestState viewTestState() {
        TestState testState = new TestState();
        testState.putValue("sourced", this);
        return testState;
    }
}
