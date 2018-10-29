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
      return new ContextDefined(organizationId, unitId, contextId, namespace, description);
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
    public final String contextId;
    public final String description;

    public static ContextDescribed with(final OrganizationId organizationId, final ContextId contextId, final String description) {
      return new ContextDescribed(organizationId, contextId, description);
    }

    public ContextDescribed(final OrganizationId organizationId, final ContextId contextId, final String description) {
      this.organizationId = organizationId.value;
      this.contextId = contextId.value;
      this.description = description;
    }
  }

  public static final class ContextRenamed extends DomainEvent {
    public final String organizationId;
    public final String contextId;
    public final String namespace;

    public static ContextRenamed with(final OrganizationId organizationId, final ContextId contextId, final String namespace) {
      return new ContextRenamed(organizationId, contextId, namespace);
    }

    public ContextRenamed(final OrganizationId organizationId, final ContextId contextId, String namespace) {
      this.organizationId = organizationId.value;
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
      return new SchemaDefined(organizationId, unitId, contextId, schemaId, category, name, description);
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
      this.category = category.name();
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
      return new SchemaDescribed(organizationId, unitId, contextId, schemaId, description);
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
      return new SchemaRecategorized(organizationId, unitId, contextId, schemaId, category);
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
      this.category = category.name();
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
      return new SchemaRenamed(organizationId, unitId, contextId, schemaId, name);
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
    public final String category;
    public final String name;
    public final String description;

    public static SchemaVersionDefined with(
            final OrganizationId organizationId,
            final ContextId contextId,
            final SchemaId schemaId,
            final Category category,
            final String name,
            final String description) {
      return new SchemaVersionDefined(organizationId, contextId, schemaId, category, name, description);
    }

    public SchemaVersionDefined(
            final OrganizationId organizationId,
            final ContextId contextId,
            final SchemaId schemaId,
            final Category category,
            final String name,
            final String description) {
      this.organizationId = organizationId.value;
      this.contextId = contextId.value;
      this.schemaId = schemaId.value;
      this.category = category.name();
      this.name = name;
      this.description = description;
    }
  }

  public static final class UnitDefined extends DomainEvent {
    public final String organizationId;
    public final String unitId;
    public final String name;
    public final String description;

    public static UnitDefined with(final OrganizationId organizationId, final UnitId unitId, final String name, final String description) {
      return new UnitDefined(organizationId, unitId, name, description);
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
      return new UnitDescribed(organizationId, unitId, description);
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
      return new UnitRenamed(organizationId, unitId, name);
    }

    public UnitRenamed(final OrganizationId organizationId, final UnitId unitId, final String name) {
      this.organizationId = organizationId.value;
      this.unitId = unitId.value;
      this.name = name;
    }
  }
}
