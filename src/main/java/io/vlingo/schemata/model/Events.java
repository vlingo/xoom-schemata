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
import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.schemata.model.Id.UnitId;

public final class Events {
  public static final class ContextDefined extends DomainEvent {
    public final String contextId;
    public final String namespace;
    public final String description;

    public static ContextDefined with(final ContextId contextId, final String namespace, final String description) {
      return new ContextDefined(contextId, namespace, description);
    }

    public ContextDefined(final ContextId contextId, final String namespace, final String description) {
      this.contextId = contextId.value;
      this.namespace = namespace;
      this.description = description;
    }
  }

  public static final class ContextDescribed extends DomainEvent {
    public final String contextId;
    public final String description;

    public static ContextDescribed with(final ContextId contextId, final String description) {
      return new ContextDescribed(contextId, description);
    }

    public ContextDescribed(final ContextId contextId, final String description) {
      this.contextId = contextId.value;
      this.description = description;
    }
  }

  public static final class ContextRenamed extends DomainEvent {
    public final String contextId;
    public final String namespace;

    public static ContextRenamed with(final ContextId contextId, final String namespace) {
      return new ContextRenamed(contextId, namespace);
    }

    public ContextRenamed(final ContextId contextId, String namespace) {
      this.contextId = contextId.value;
      this.namespace = namespace;
    }
  }

  public static final class OrganizationDefined extends DomainEvent {
    public final String organizationId;
    public final String name;
    public final String description;

    public static OrganizationDefined with(final OrganizationId organizationId, final String name, final String description) {
      return new OrganizationDefined(organizationId, name, description);
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
      return new OrganizationDescribed(organizationId, description);
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
      return new OrganizationRenamed(organizationId, name);
    }

    public OrganizationRenamed(final OrganizationId organizationId, final String name) {
      this.organizationId = organizationId.value;
      this.name = name;
    }
  }

  public static final class SchemaDefined extends DomainEvent {
    public final String schemaId;
    public final String category;
    public final String name;
    public final String description;

    public static SchemaDefined with(final SchemaId schemaId, final Category category, final String name, final String description) {
      return new SchemaDefined(schemaId, category, name, description);
    }

    public SchemaDefined(final SchemaId schemaId, final Category category, final String name, final String description) {
      this.schemaId = schemaId.value;
      this.category = category.name();
      this.name = name;
      this.description = description;
    }
  }

  public static final class SchemaDescribed extends DomainEvent {
    public final String schemaId;
    public final String description;

    public static SchemaDescribed with(final SchemaId schemaId, final String description) {
      return new SchemaDescribed(schemaId, description);
    }

    public SchemaDescribed(final SchemaId schemaId, final String description) {
      this.schemaId = schemaId.value;
      this.description = description;
    }
  }

  public static final class SchemaRecategorized extends DomainEvent {
    public final String schemaId;
    public final String category;

    public static SchemaRecategorized with(final SchemaId schemaId, final Category category) {
      return new SchemaRecategorized(schemaId, category);
    }

    public SchemaRecategorized(final SchemaId schemaId, final Category category) {
      this.schemaId = schemaId.value;
      this.category = category.name();
    }
  }

  public static final class SchemaRenamed extends DomainEvent {
    public final String schemaId;
    public final String name;

    public static SchemaRenamed with(final SchemaId schemaId, final String name) {
      return new SchemaRenamed(schemaId, name);
    }

    public SchemaRenamed(final SchemaId schemaId, final String name) {
      this.schemaId = schemaId.value;
      this.name = name;
    }
  }

  public static final class SchemaVersionDefined extends DomainEvent {
    public final String schemaVersionId;
    public final String category;
    public final String name;
    public final String description;
    public final String status;
    public final String specification;
    public final String version;

    public static SchemaVersionDefined with(final SchemaVersionId schemaVersionId,
            final Category category, final String name, final String description, 
            final SchemaVersion.Specification specification, final SchemaVersion.Status status,
            final SchemaVersion.Version version) {
      return new SchemaVersionDefined(schemaVersionId, category, name, description, specification, status, version);
    }

    public SchemaVersionDefined(SchemaVersionId schemaVersionId, 
            final Category category, final String name, final String description,
            final SchemaVersion.Specification specification, final SchemaVersion.Status status,
            final SchemaVersion.Version version) {
      this.schemaVersionId = schemaVersionId.value;
      this.category = category.name();
      this.name = name;
      this.description = description;
      this.status = status.toString();
      this.specification = specification.value;
      this.version = version.value;
    }
  }

  public static final class SchemaVersionDescribed extends DomainEvent {
    public final String schemaVersionId;
    public final String description;

    public static SchemaVersionDescribed with(final SchemaVersionId schemaVersionId, final String description) {
      return new SchemaVersionDescribed(schemaVersionId, description);
    }

    public SchemaVersionDescribed(final SchemaVersionId schemaVersionId, final String description) {
      this.schemaVersionId = schemaVersionId.value;
      this.description = description;
    }
  }

  public static final class SchemaVersionStatusChanged extends DomainEvent {
    public final String schemaVersionId;
    public final String status;

    public static SchemaVersionStatusChanged with(final SchemaVersionId schemaVersionId, final SchemaVersion.Status status) {
      return new SchemaVersionStatusChanged(schemaVersionId, status);
    }

    public SchemaVersionStatusChanged(final SchemaVersionId schemaVersionId, final SchemaVersion.Status status) {
      this.schemaVersionId = schemaVersionId.value;
      this.status = status.name();
    }
  }

  public static final class SchemaVersionAssignedVersion extends DomainEvent {
    public final String schemaVersionId;
    public final String version;

    public static SchemaVersionAssignedVersion with(final SchemaVersionId schemaVersionId, final SchemaVersion.Version version) {
      return new SchemaVersionAssignedVersion(schemaVersionId, version);
    }

    public SchemaVersionAssignedVersion(SchemaVersionId schemaVersionId, final SchemaVersion.Version version) {
      this.schemaVersionId = schemaVersionId.value;
      this.version = version.value;
    }
  }

  public static final class SchemaVersionSpecified extends DomainEvent {
    public final String schemaVersionId;
    public final String specification;

    public static SchemaVersionSpecified with(SchemaVersionId schemaVersionId, final SchemaVersion.Specification specification) {
      return new SchemaVersionSpecified(schemaVersionId, specification);
    }

    public SchemaVersionSpecified(SchemaVersionId schemaVersionId, final SchemaVersion.Specification specification) {
      this.schemaVersionId = schemaVersionId.value;
      this.specification = specification.value;
    }
  }

  public static final class UnitDefined extends DomainEvent {
    public final String unitId;
    public final String name;
    public final String description;

    public static UnitDefined with(final UnitId unitId, final String name, final String description) {
      return new UnitDefined(unitId, name, description);
    }

    public UnitDefined(final UnitId unitId, final String name, final String description) {
      this.unitId = unitId.value;
      this.name = name;
      this.description = description;
    }
  }

  public static final class UnitDescribed extends DomainEvent {
    public final String unitId;
    public final String description;

    public static UnitDescribed with(final UnitId unitId, final String description) {
      return new UnitDescribed(unitId, description);
    }

    public UnitDescribed(final UnitId unitId, final String description) {
      this.unitId = unitId.value;
      this.description = description;
    }
  }

  public static final class UnitRenamed extends DomainEvent {
    public final String unitId;
    public final String name;

    public static UnitRenamed with(final UnitId unitId, final String name) {
      return new UnitRenamed(unitId, name);
    }

    public UnitRenamed(final UnitId unitId, final String name) {
      this.unitId = unitId.value;
      this.name = name;
    }
  }
}
