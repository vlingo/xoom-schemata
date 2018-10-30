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

import java.util.function.BiConsumer;

public final class SchemaVersionEntity extends EventSourced implements SchemaVersion {

    static {
        BiConsumer<SchemaVersionEntity, Events.SchemaVersionDefined> applySchemaDefinedFn = SchemaVersionEntity::applyDefined;
        EventSourced.registerConsumer ( SchemaVersionEntity.class, Events.SchemaVersionDefined.class, applySchemaDefinedFn );
        BiConsumer<SchemaVersionEntity, Events.SchemaVersionDefinition> applySchemaVersionDefinitionFn = SchemaVersionEntity::applyDefiniton;
        EventSourced.registerConsumer ( SchemaVersionEntity.class, Events.SchemaVersionDefinition.class, applySchemaVersionDefinitionFn );
        BiConsumer<SchemaVersionEntity, Events.SchemaVersionDescribed> applySchemaDescribedFn = SchemaVersionEntity::applyDescribed;
        EventSourced.registerConsumer ( SchemaVersionEntity.class, Events.SchemaVersionDescribed.class, applySchemaDescribedFn );
        BiConsumer<SchemaVersionEntity, Events.SchemaVersionStatus> applySchemaVersionStatusFn = SchemaVersionEntity::applyStatus;
        EventSourced.registerConsumer ( SchemaVersionEntity.class, Events.SchemaVersionStatus.class, applySchemaVersionStatusFn );
        BiConsumer<SchemaVersionEntity, Events.SchemaVersionAssignedVersion> applySchemaVersionFn = SchemaVersionEntity::applyVersioned;
        EventSourced.registerConsumer ( SchemaVersionEntity.class, Events.SchemaVersionAssignedVersion.class, applySchemaVersionFn );
    }

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
        apply ( new Events.SchemaVersionStatus ( state.organizationId, state.contextId, state.schemaId, state.schemaVersionId, state.unitId, status ) );

    }

    @Override
    public void definedAs(final Definition definition) {
        assert (definition != null);
        apply ( new Events.SchemaVersionDefinition ( state.organizationId, state.contextId, state.schemaId, state.schemaVersionId, state.unitId, definition ) );

    }

    @Override
    public void assignVersion(Version version) {
        assert (version != null);
        apply ( new Events.SchemaVersionAssignedVersion ( state.organizationId, state.contextId, state.schemaId, state.schemaVersionId, state.unitId, version ) );
    }

    public void applyDefined(final Events.SchemaVersionDefined defined) {
        this.state = new State ( OrganizationId.existing ( defined.organizationId ), UnitId.existing ( defined.unitId ), ContextId.existing ( defined.contextId ),
                SchemaId.existing ( defined.schemaId ), SchemaVersionId.existing ( defined.schemaVersionId ), defined.description, defined.definition,
                defined.version, defined.status );
    }

    public void applyDefiniton(final Events.SchemaVersionDefinition definition) {
        this.state = this.state.withDefinition ( definition.definition );
    }

    public void applyDescribed(final Events.SchemaVersionDescribed description) {
        this.state = this.state.withDescription ( description.description );
    }

    public void applyVersioned(final Events.SchemaVersionAssignedVersion version) {
        this.state = this.state.withVersion ( version.version );
    }

    public void applyStatus(final Events.SchemaVersionStatus status) {
        this.state = this.state.withStatus ( status.status );
    }

    public class State {
        public final OrganizationId organizationId;
        public final UnitId unitId;
        public final ContextId contextId;
        public final SchemaId schemaId;
        public final SchemaVersionId schemaVersionId;
        public final String description;
        public final String definition;
        public final String status;
        public final String version;

        public State(
                final OrganizationId organizationId,
                final UnitId unitId,
                final ContextId contextId,
                final SchemaId schemaId,
                final SchemaVersionId schemaVersionId,
                final String description,
                final String definition,
                final String version,
                final String status) {
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
                    this.description, this.definition, this.version, Status.Published.name () );
        }

        public State withDefinition(final String definition) {
            return new State ( this.organizationId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
                    this.description, definition, this.version, this.status );
        }

        public State withDescription(final String description) {
            return new State ( this.organizationId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
                    description, this.definition, this.version, this.status );
        }

        public State withVersion(final String version) {
            return new State ( this.organizationId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
                    this.description, this.definition, version, this.status );
        }

        public State withStatus(final String status) {
            return new State ( this.organizationId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
                    this.description, this.definition, this.version, status );
        }
    }

    @Override
    public TestState viewTestState() {
        TestState testState = new TestState ();
        testState.putValue ( "applied", applied () );
        return testState;
    }
}