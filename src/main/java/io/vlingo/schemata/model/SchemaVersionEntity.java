// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.testkit.TestState;
import io.vlingo.lattice.model.sourcing.EventSourced;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.schemata.model.Id.UnitId;

public final class SchemaVersionEntity extends EventSourced implements SchemaVersion {
    private State state;

    public SchemaVersionEntity(
            final OrganizationId organizationId,
            final UnitId unitId,
            final ContextId contextId,
            final SchemaId schemaId,
            final SchemaVersionId schemaVersionId,
            final String description,
            final Definition definition,
            final Status status,
            final Version version,
            final String name) {
        assert (name != null && !name.isEmpty ());
        assert (description != null && !description.isEmpty ());
        apply ( new Events.SchemaVersionDefined ( organizationId, contextId, schemaId, Category.Commands, name,
                description, schemaVersionId, status, definition, unitId, version ) );
    }

    @Override
    public void describeAs(final String description) {
        assert (description != null && !description.isEmpty ());
        apply ( new Events.SchemaVersionDescribed ( state.organizationId, state.contextId, state.schemaId, state.schemaVersionId, state.unitId, description ) );
    }

    @Override
    public void assignStatus(final Status status) {
        assert (status != null);
        apply ( new Events.SchemaVersionAssigned ( state.organizationId, state.contextId, state.schemaId, state.schemaVersionId, state.unitId, status ) );

    }

    @Override
    public void definedAs(final Definition definition) {
        assert (definition != null);
        apply ( new Events.SchemaVersionDefinition ( state.organizationId, state.contextId, state.schemaId, state.schemaVersionId, state.unitId, definition ) );

    }

    public void applyDefined(final Events.SchemaVersionDefined defined) {
        this.state = new State ( OrganizationId.existing ( defined.organizationId ), UnitId.existing ( defined.unitId ), ContextId.existing ( defined.contextId ),
                SchemaId.existing ( defined.schemaId ), SchemaVersionId.existing ( defined.schemaVersionId ), defined.description, defined.definition,
                defined.version, defined.status );
    }

    public void applyDefiniton(final Definition definition) {
        this.state = this.state.withDefinition ( definition );
    }

    public void applyDescribed(final String description) {
        this.state = this.state.withDescription ( description );
    }

    public void applyVersion(final Version version) {
        this.state = this.state.withVersion ( version );
    }

    public class State {
        public final OrganizationId organizationId;
        public final UnitId unitId;
        public final ContextId contextId;
        public final SchemaId schemaId;
        public final SchemaVersionId schemaVersionId;
        public final String description;
        public final Definition definition;
        public final Status status;
        public final Version version;

        public State(
                final OrganizationId organizationId,
                final UnitId unitId,
                final ContextId contextId,
                final SchemaId schemaId,
                final SchemaVersionId schemaVersionId,
                final String description,
                final Definition definition,
                final Version version,
                final Status status) {
            this.organizationId = organizationId;
            this.unitId = unitId;
            this.contextId = contextId;
            this.schemaId = schemaId;
            this.schemaVersionId = schemaVersionId;
            this.description = description;
            this.definition = definition;
            this.version = version;
            this.status = status;
        }

        public State asPublished() {
            return new State ( this.organizationId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
                    this.description, this.definition, this.version, Status.Published );
        }

        public State withDefinition(final Definition definition) {
            return new State ( this.organizationId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
                    this.description, definition, this.version, this.status );
        }

        public State withDescription(final String description) {
            return new State ( this.organizationId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
                    description, this.definition, this.version, this.status );
        }

        public State withVersion(final Version version) {
            return new State ( this.organizationId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
                    this.description, this.definition, version, this.status );
        }
    }

    @Override
    public TestState viewTestState() {
        TestState testState = new TestState ();
        testState.putValue ( "applied", applied () );
        return testState;
    }
}