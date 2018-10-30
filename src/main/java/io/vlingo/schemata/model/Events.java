// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.UnitId;

public final class Events {
    public static final class ContextDefined extends DomainEvent {
        public final String organizationId;
        public final String unitId;
        public final String contextId;
        public final String namespace;
        public final String description;

        public static ContextDefined with(final OrganizationId organizationId, final UnitId unitId, final ContextId contextId, final String namespace, final String description) {
            return new ContextDefined ( organizationId, unitId, contextId, namespace, description );
        }

        public ContextDefined(final OrganizationId organizationId, final UnitId unitId, final ContextId contextId, final String namespace, final String description) {
            this.organizationId = organizationId.value;
            this.unitId = unitId.value;
            this.contextId = contextId.value;
            this.namespace = namespace;
            this.description = description;
        }
    }

    public static final class ContextDescribed extends DomainEvent {
        public final String organizationId;
        public final String unitId;
        public final String contextId;
        public final String description;

        public static ContextDescribed with(final OrganizationId organizationId, final UnitId unitId, final ContextId contextId, final String description) {
            return new ContextDescribed ( organizationId, unitId, contextId, description );
        }

        public ContextDescribed(final OrganizationId organizationId, final UnitId unitId, final ContextId contextId, final String description) {
            this.organizationId = organizationId.value;
            this.unitId = unitId.value;
            this.contextId = contextId.value;
            this.description = description;
        }
    }

    public static final class ContextRenamed extends DomainEvent {
        public final String organizationId;
        public final String unitId;
        public final String contextId;
        public final String namespace;

        public static ContextRenamed with(final OrganizationId organizationId, final UnitId unitId, final ContextId contextId, final String namespace) {
            return new ContextRenamed ( organizationId, unitId, contextId, namespace );
        }

        public ContextRenamed(final OrganizationId organizationId, final UnitId unitId, final ContextId contextId, String namespace) {
            this.organizationId = organizationId.value;
            this.unitId = unitId.value;
            this.contextId = contextId.value;
            this.namespace = namespace;
        }
    }

    public static final class OrganizationDefined extends DomainEvent {
        public final String organizationId;
        public final String name;
        public final String description;

        public static OrganizationDefined with(final OrganizationId organizationId, final String name, final String description) {
            return new OrganizationDefined ( organizationId, name, description );
        }

        public OrganizationDefined(final OrganizationId organizationId, final String name, final String description) {
            this.organizationId = organizationId.value;
            this.name = name;
            this.description = description;
        }
    }

    public static final class OrganizationDescribed extends DomainEvent {
        public final String organizationId;
        public final String description;

        public static OrganizationDescribed with(final OrganizationId organizationId, final String description) {
            return new OrganizationDescribed ( organizationId, description );
        }

        public OrganizationDescribed(final OrganizationId organizationId, final String description) {
            this.organizationId = organizationId.value;
            this.description = description;
        }
    }

    public static final class OrganizationRenamed extends DomainEvent {
        public final String organizationId;
        public final String name;

        public static OrganizationRenamed with(final OrganizationId organizationId, final String name) {
            return new OrganizationRenamed ( organizationId, name );
        }

        public OrganizationRenamed(final OrganizationId organizationId, final String name) {
            this.organizationId = organizationId.value;
            this.name = name;
        }
    }

    public static final class SchemaDefined extends DomainEvent {
        public final String organizationId;
        public final String unitId;
        public final String contextId;
        public final String schemaId;
        public final String category;
        public final String name;
        public final String description;

        public static SchemaDefined with(
                final OrganizationId organizationId,
                final UnitId unitId,
                final ContextId contextId,
                final SchemaId schemaId,
                final Category category,
                final String name,
                final String description) {
            return new SchemaDefined ( organizationId, unitId, contextId, schemaId, category, name, description );
        }

        public SchemaDefined(
                final OrganizationId organizationId,
                final UnitId unitId,
                final ContextId contextId,
                final SchemaId schemaId,
                final Category category,
                final String name,
                final String description) {
            this.organizationId = organizationId.value;
            this.unitId = unitId.value;
            this.contextId = contextId.value;
            this.schemaId = schemaId.value;
            this.category = category.name ();
            this.name = name;
            this.description = description;
        }
    }

    public static final class SchemaDescribed extends DomainEvent {
        public final String organizationId;
        public final String unitId;
        public final String contextId;
        public final String schemaId;
        public final String description;

        public static SchemaDescribed with(
                final OrganizationId organizationId,
                final UnitId unitId,
                final ContextId contextId,
                final SchemaId schemaId,
                final String description) {
            return new SchemaDescribed ( organizationId, unitId, contextId, schemaId, description );
        }

        public SchemaDescribed(
                final OrganizationId organizationId,
                final UnitId unitId,
                final ContextId contextId,
                final SchemaId schemaId,
                final String description) {
            this.organizationId = organizationId.value;
            this.unitId = unitId.value;
            this.contextId = contextId.value;
            this.schemaId = schemaId.value;
            this.description = description;
        }
    }

    public static final class SchemaRecategorized extends DomainEvent {
        public final String organizationId;
        public final String unitId;
        public final String contextId;
        public final String schemaId;
        public final String category;

        public static SchemaRecategorized with(
                final OrganizationId organizationId,
                final UnitId unitId,
                final ContextId contextId,
                final SchemaId schemaId,
                final Category category) {
            return new SchemaRecategorized ( organizationId, unitId, contextId, schemaId, category );
        }

        public SchemaRecategorized(
                final OrganizationId organizationId,
                final UnitId unitId,
                final ContextId contextId,
                final SchemaId schemaId,
                final Category category) {
            this.organizationId = organizationId.value;
            this.unitId = unitId.value;
            this.contextId = contextId.value;
            this.schemaId = schemaId.value;
            this.category = category.name ();
        }
    }

    public static final class SchemaRenamed extends DomainEvent {
        public final String organizationId;
        public final String unitId;
        public final String contextId;
        public final String schemaId;
        public final String name;

        public static SchemaRenamed with(
                final OrganizationId organizationId,
                final UnitId unitId,
                final ContextId contextId,
                final SchemaId schemaId,
                final String name) {
            return new SchemaRenamed ( organizationId, unitId, contextId, schemaId, name );
        }

        public SchemaRenamed(
                final OrganizationId organizationId,
                final UnitId unitId,
                final ContextId contextId,
                final SchemaId schemaId,
                final String name) {
            this.organizationId = organizationId.value;
            this.unitId = unitId.value;
            this.contextId = contextId.value;
            this.schemaId = schemaId.value;
            this.name = name;
        }
    }

    public static final class SchemaVersionDefined extends DomainEvent {
        public final String organizationId;
        public final String contextId;
        public final String schemaId;
        public final String schemaVersionId;
        public final String category;
        public final String name;
        public final String description;
        public final String status;
        public final String definition;
        public final String unitId;
        public final String version;

        public static SchemaVersionDefined with(
                final OrganizationId organizationId,
                final ContextId contextId,
                final SchemaId schemaId,
                final Category category,
                final String name,
                final String description,
                final Id.SchemaVersionId schemaVersionId,
                final SchemaVersion.Status status,
                final SchemaVersion.Definition definition,
                final UnitId unitId,
                final SchemaVersion.Version version) {
            return new SchemaVersionDefined ( organizationId, contextId, schemaId, category, name, description, schemaVersionId, status, definition, unitId, version );
        }

        public SchemaVersionDefined(
                final OrganizationId organizationId,
                final ContextId contextId,
                final SchemaId schemaId,
                final Category category,
                final String name,
                final String description,
                final Id.SchemaVersionId schemaVersionId,
                final SchemaVersion.Status status,
                final SchemaVersion.Definition definition,
                final UnitId unitId,
                final SchemaVersion.Version version) {
            this.organizationId = organizationId.value;
            this.contextId = contextId.value;
            this.schemaId = schemaId.value;
            this.category = category.name ();
            this.name = name;
            this.description = description;
            this.schemaVersionId = schemaVersionId.value;
            this.status = status.toString ();
            this.definition = definition.value;
            this.unitId = unitId.value;
            this.version = version.value;
        }
    }

    public static final class SchemaVersionDescribed extends DomainEvent {
        public final String organizationId;
        public final String contextId;
        public final String schemaId;
        public final String schemaVersionId;
        public final String unitId;
        public final String description;

        public static SchemaVersionDescribed with(final OrganizationId organizationId, final ContextId contextId, final SchemaId schemaId,
                                                  final String description, final Id.SchemaVersionId schemaVersionId, final UnitId unitId) {
            return new SchemaVersionDescribed ( organizationId, contextId, schemaId, schemaVersionId, unitId, description );
        }

        public SchemaVersionDescribed(final OrganizationId organizationId, final ContextId contextId, final SchemaId schemaId, final Id.SchemaVersionId schemaVersionId, final UnitId unitId, final String description) {
            this.organizationId = organizationId.value;
            this.contextId = contextId.value;
            this.schemaId = schemaId.value;
            this.schemaVersionId = schemaVersionId.value;
            this.unitId = unitId.value;
            this.description = description;


        }
    }

    public static final class SchemaVersionStatus extends DomainEvent {
        public final String organizationId;
        public final String contextId;
        public final String schemaId;
        public final String schemaVersionId;
        public final String unitId;
        public final String status;

        public static SchemaVersionStatus with(final OrganizationId organizationId, final ContextId contextId, final SchemaId schemaId,
                                               final Id.SchemaVersionId schemaVersionId, final UnitId unitId, final SchemaVersion.Status status) {
            return new SchemaVersionStatus ( organizationId, contextId, schemaId, schemaVersionId, unitId, status );
        }

        public SchemaVersionStatus(final OrganizationId organizationId, final ContextId contextId, final SchemaId schemaId,
                                   final Id.SchemaVersionId schemaVersionId, final UnitId unitId, final SchemaVersion.Status status) {
            this.organizationId = organizationId.value;
            this.contextId = contextId.value;
            this.schemaId = schemaId.value;
            this.schemaVersionId = schemaVersionId.value;
            this.unitId = unitId.value;
            this.status = status.name ();
        }

    }

    public static final class SchemaVersionAssignedVersion extends DomainEvent {
        public final String organizationId;
        public final String contextId;
        public final String schemaId;
        public final String schemaVersionId;
        public final String unitId;
        public final String version;

        public static SchemaVersionAssignedVersion with(final OrganizationId organizationId, final ContextId contextId, final SchemaId schemaId,
                                                        final Id.SchemaVersionId schemaVersionId, final UnitId unitId, final SchemaVersion.Version version) {
            return new SchemaVersionAssignedVersion ( organizationId, contextId, schemaId, schemaVersionId, unitId, version );
        }

        public SchemaVersionAssignedVersion(final OrganizationId organizationId, final ContextId contextId, final SchemaId schemaId,
                                            final Id.SchemaVersionId schemaVersionId, final UnitId unitId, final SchemaVersion.Version version) {
            this.organizationId = organizationId.value;
            this.contextId = contextId.value;
            this.schemaId = schemaId.value;
            this.schemaVersionId = schemaVersionId.value;
            this.unitId = unitId.value;
            this.version = version.value;
        }
    }


    public static final class SchemaVersionDefinition extends DomainEvent {
        public final String organizationId;
        public final String contextId;
        public final String schemaId;
        public final String schemaVersionId;
        public final String unitId;
        public final String definition;

        public static SchemaVersionDefinition with(final OrganizationId organizationId, final ContextId contextId, final SchemaId schemaId,
                                                   final Id.SchemaVersionId schemaVersionId, final UnitId unitId, final SchemaVersion.Definition definition) {
            return new SchemaVersionDefinition ( organizationId, contextId, schemaId, schemaVersionId, unitId, definition );
        }

        public SchemaVersionDefinition(final OrganizationId organizationId, final ContextId contextId, final SchemaId schemaId,
                                       final Id.SchemaVersionId schemaVersionId, final UnitId unitId, final SchemaVersion.Definition definition) {
            this.organizationId = organizationId.value;
            this.contextId = contextId.value;
            this.schemaId = schemaId.value;
            this.schemaVersionId = schemaVersionId.value;
            this.unitId = unitId.value;
            this.definition = definition.value;
        }
    }

    public static final class UnitDefined extends DomainEvent {
        public final String organizationId;
        public final String unitId;
        public final String name;
        public final String description;

        public static UnitDefined with(final OrganizationId organizationId, final UnitId unitId, final String name, final String description) {
            return new UnitDefined ( organizationId, unitId, name, description );
        }

        public UnitDefined(final OrganizationId organizationId, final UnitId unitId, final String name, final String description) {
            this.organizationId = organizationId.value;
            this.unitId = unitId.value;
            this.name = name;
            this.description = description;
        }
    }

    public static final class UnitDescribed extends DomainEvent {
        public final String organizationId;
        public final String unitId;
        public final String description;

        public static UnitDescribed with(final OrganizationId organizationId, final UnitId unitId, final String description) {
            return new UnitDescribed ( organizationId, unitId, description );
        }

        public UnitDescribed(final OrganizationId organizationId, final UnitId unitId, final String description) {
            this.organizationId = organizationId.value;
            this.unitId = unitId.value;
            this.description = description;
        }
    }

    public static final class UnitRenamed extends DomainEvent {
        public final String organizationId;
        public final String unitId;
        public final String name;

        public static UnitRenamed with(final OrganizationId organizationId, final UnitId unitId, final String name) {
            return new UnitRenamed ( organizationId, unitId, name );
        }

        public UnitRenamed(final OrganizationId organizationId, final UnitId unitId, final String name) {
            this.organizationId = organizationId.value;
            this.unitId = unitId.value;
            this.name = name;
        }
    }
}
