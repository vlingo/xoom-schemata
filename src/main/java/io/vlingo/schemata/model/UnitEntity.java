// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.testkit.TestState;
import io.vlingo.lattice.model.sourcing.EventSourced;
import io.vlingo.schemata.model.Events.UnitDefined;
import io.vlingo.schemata.model.Events.UnitDescribed;
import io.vlingo.schemata.model.Events.UnitRenamed;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.UnitId;

import java.util.function.BiConsumer;

public class UnitEntity extends EventSourced implements Organization {
    static {
        BiConsumer<UnitEntity, UnitDefined> applyOrganizationDefinedFn = UnitEntity::applyDefined;
        EventSourced.registerConsumer ( UnitEntity.class, UnitDefined.class, applyOrganizationDefinedFn );
        BiConsumer<UnitEntity, UnitDescribed> applyOrganizationDescribedFn = UnitEntity::applyDescribed;
        EventSourced.registerConsumer ( UnitEntity.class, UnitDescribed.class, applyOrganizationDescribedFn );
        BiConsumer<UnitEntity, UnitRenamed> applyOrganizationRenamedFn = UnitEntity::applyRenamed;
        EventSourced.registerConsumer ( UnitEntity.class, UnitRenamed.class, applyOrganizationRenamedFn );
    }

    private UnitEntity.State state;

    public UnitEntity(final OrganizationId organizationId, final UnitId unitId, final String name, final String description) {
        apply ( new UnitDefined ( organizationId, unitId, name, description ) );
    }

    @Override
    public void describeAs(final String description) {
        apply ( new UnitDescribed ( state.organizationId, state.unitId, description ) );
    }

    @Override
    public void renameTo(final String name) {
        apply ( new UnitRenamed ( state.organizationId, state.unitId, name ) );
    }

    public void applyDefined(final UnitDefined e) {
        state = new State ( OrganizationId.existing ( e.organizationId ), UnitId.existing ( e.unitId ), e.name, e.description );
    }

    public final void applyDescribed(UnitDescribed event) {
        state = state.withDescription ( event.description );
    }

    public final void applyRenamed(UnitRenamed event) {
        state = state.withName ( event.name );
    }

    public class State {
        public final OrganizationId organizationId;
        public final UnitId unitId;
        public final String name;
        public final String description;

        public UnitEntity.State withDescription(final String description) {
            return new State ( this.organizationId, this.unitId, this.name, description );
        }

        public State withName(final String name) {
            return new State ( this.organizationId, this.unitId, name, this.description );
        }

        public State(final OrganizationId organizationId, final UnitId unitId, final String name, final String description) {
            this.organizationId = organizationId;
            this.unitId = unitId;
            this.name = name;
            this.description = description;
        }
    }

    @Override
    public TestState viewTestState() {
        TestState testState = new TestState ();
        testState.putValue ( "applied", applied () );
        return testState;
    }
}
