// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.testkit.TestState;
import io.vlingo.lattice.model.sourcing.EventSourced;
import io.vlingo.schemata.model.Events.ContextDefined;
import io.vlingo.schemata.model.Events.ContextDescribed;
import io.vlingo.schemata.model.Events.ContextRenamed;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.UnitId;

import java.util.function.BiConsumer;

public class ContextEntity extends EventSourced implements Context {
    static {
        BiConsumer<ContextEntity, ContextDefined> applyContextDefinedFn = ContextEntity::applyDefined;
        EventSourced.registerConsumer(ContextEntity.class, ContextDefined.class, applyContextDefinedFn);
        BiConsumer<ContextEntity, ContextDescribed> applyContextDescribedFn = ContextEntity::applyDescribed;
        EventSourced.registerConsumer(ContextEntity.class, ContextDescribed.class, applyContextDescribedFn);
        BiConsumer<ContextEntity, ContextRenamed> applyContextNamespaceChangedFn = ContextEntity::applyNamespaceChangedVlingoSchemata;
        EventSourced.registerConsumer(ContextEntity.class, ContextRenamed.class, applyContextNamespaceChangedFn);
    }

    private State state;

    public ContextEntity(final OrganizationId organizationId, final UnitId unitId, final ContextId contextId, final String namespace, final String description) {
        assert (namespace != null && !namespace.isEmpty());
        assert (description != null && !description.isEmpty());
        apply(ContextDefined.with(organizationId, unitId, contextId, namespace, description));
    }

    @Override
    public void changeNamespaceTo(final String namespace) {
        assert (namespace != null && !namespace.isEmpty());
        this.apply(ContextRenamed.with(state.organizationId, state.unitId, state.contextId, namespace));
    }

    @Override
    public void describeAs(final String description) {
        assert (description != null && !description.isEmpty());
        this.apply(ContextDescribed.with(state.organizationId, state.unitId, state.contextId, description));
    }

    public void applyDefined(final ContextDefined e) {
        this.state = new State(OrganizationId.existing(e.organizationId), UnitId.existing(e.unitId), ContextId.existing(e.contextId), e.namespace, e.description);
    }

    public void applyDescribed(final ContextDescribed e) {
        this.state = this.state.withDescription(e.description);
    }

    public void applyNamespaceChangedVlingoSchemata(final ContextRenamed e) {
        state = state.withNamespace(e.namespace);
    }

    public class State {
        public final OrganizationId organizationId;
        public final UnitId unitId;
        public final ContextId contextId;
        public final String description;
        public final String namespace;

        public State(final OrganizationId organizationId, final UnitId unitId, final ContextId contextId, final String namespace, final String description) {
            this.organizationId = organizationId;
            this.unitId = unitId;
            this.contextId = contextId;
            this.namespace = namespace;
            this.description = description;
        }

        public State withDescription(final String description) {
            return new State(this.organizationId, this.unitId, this.contextId, this.namespace, description);
        }

        public State withNamespace(final String namespace) {
            return new State(this.organizationId, this.unitId, this.contextId, namespace, this.description);
        }
    }

    @Override
    public TestState viewTestState() {
        TestState testState = new TestState();
        testState.putValue("applied", applied());
        return testState;
    }
}
