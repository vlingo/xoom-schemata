// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.lattice.model.IdentifiedDomainEvent;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.schemata.model.Id.UnitId;

public final class Events {
  public static final class ContextDefined extends IdentifiedDomainEvent {
    public final String unitId;
    public final String contextId;
    public final String name;
    public final String description;

    public static ContextDefined with(final ContextId contextId, final String namespace, final String description) {
      return new ContextDefined(contextId, namespace, description);
    }

    private ContextDefined(final ContextId contextId, final String name, final String description) {
      this.unitId = contextId.unitId.value;
      this.contextId = contextId.value;
      this.name = name;
      this.description = description;
    }

    @Override
    public String parentIdentity() {
      return unitId;
    }

    @Override
    public String identity() {
      return contextId;
    }
  }

  public static final class ContextDescribed extends IdentifiedDomainEvent {
    public final String unitId;
    public final String contextId;
    public final String description;

    public static ContextDescribed with(final ContextId contextId, final String description) {
      return new ContextDescribed(contextId, description);
    }

    public ContextDescribed(final ContextId contextId, final String description) {
      this.unitId = contextId.unitId.value;
      this.contextId = contextId.value;
      this.description = description;
    }

    @Override
    public String parentIdentity() {
      return unitId;
    }

    @Override
    public String identity() {
      return contextId;
    }
  }

  public static final class ContextMovedToNamespace extends IdentifiedDomainEvent {
    public final String unitId;
    public final String contextId;
    public final String namespace;

    public static ContextMovedToNamespace with(final ContextId contextId, final String namespace) {
      return new ContextMovedToNamespace(contextId, namespace);
    }

    public ContextMovedToNamespace(final ContextId contextId, String namespace) {
      this.unitId = contextId.unitId.value;
      this.contextId = contextId.value;
      this.namespace = namespace;
    }

    @Override
    public String parentIdentity() {
      return unitId;
    }

    @Override
    public String identity() {
      return contextId;
    }
  }

  public static final class ContextRedefined extends IdentifiedDomainEvent {
    public final String unitId;
    public final String contextId;
    public final String name;
    public final String description;

    public static ContextRedefined with(final ContextId contextId, final String namespace, final String description) {
      return new ContextRedefined(contextId, namespace, description);
    }

    public ContextRedefined(final ContextId contextId, final String name, final String description) {
      this.unitId = contextId.unitId.value;
      this.contextId = contextId.value;
      this.name = name;
      this.description = description;
    }

    @Override
    public String parentIdentity() {
      return unitId;
    }

    @Override
    public String identity() {
      return contextId;
    }
  }

  public static final class OrganizationDefined extends IdentifiedDomainEvent {
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

    @Override
    public String identity() {
      return organizationId;
    }
  }

  public static final class OrganizationDescribed extends IdentifiedDomainEvent {
    public final String organizationId;
    public final String description;

    public static OrganizationDescribed with(final OrganizationId organizationId, final String description) {
      return new OrganizationDescribed(organizationId, description);
    }

    public OrganizationDescribed(final OrganizationId organizationId, final String description) {
      this.organizationId = organizationId.value;
      this.description = description;
    }

    @Override
    public String identity() {
      return organizationId;
    }
  }

  public static final class OrganizationRedefined extends IdentifiedDomainEvent {
    public final String organizationId;
    public final String name;
    public final String description;

    public static OrganizationRedefined with(final OrganizationId organizationId, final String name, final String description) {
      return new OrganizationRedefined(organizationId, name, description);
    }

    public OrganizationRedefined(final OrganizationId organizationId, final String name, final String description) {
      this.organizationId = organizationId.value;
      this.name = name;
      this.description = description;
    }

    @Override
    public String identity() {
      return organizationId;
    }
  }

  public static final class OrganizationRenamed extends IdentifiedDomainEvent {
    public final String organizationId;
    public final String name;

    public static OrganizationRenamed with(final OrganizationId organizationId, final String name) {
      return new OrganizationRenamed(organizationId, name);
    }

    public OrganizationRenamed(final OrganizationId organizationId, final String name) {
      this.organizationId = organizationId.value;
      this.name = name;
    }

    @Override
    public String identity() {
      return organizationId;
    }
  }

  public static final class SchemaDefined extends IdentifiedDomainEvent {
    public final String organizationId;
    public final String unitId;
    public final String contextId;
    public final String schemaId;
    public final String category;
    public final String scope;
    public final String name;
    public final String description;

    public static SchemaDefined with(final SchemaId schemaId, final Category category, final Scope scope, final String name, final String description) {
      return new SchemaDefined(schemaId, category, scope, name, description);
    }

    public SchemaDefined(final SchemaId schemaId, final Category category, final Scope scope, final String name, final String description) {
      this.organizationId = schemaId.organizationId().value;
      this.unitId = schemaId.unitId().value;
      this.contextId = schemaId.contextId.value;
      this.schemaId = schemaId.value;
      this.category = category.name();
      this.scope = scope.name();
      this.name = name;
      this.description = description;
    }

    @Override
    public String parentIdentity() {
      return contextId;
    }

    @Override
    public String identity() {
      return schemaId;
    }
  }

  public static final class SchemaDescribed extends IdentifiedDomainEvent {
    public final String contextId;
    public final String schemaId;
    public final String description;

    public static SchemaDescribed with(final SchemaId schemaId, final String description) {
      return new SchemaDescribed(schemaId, description);
    }

    public SchemaDescribed(final SchemaId schemaId, final String description) {
      this.contextId = schemaId.contextId.value;
      this.schemaId = schemaId.value;
      this.description = description;
    }

    @Override
    public String parentIdentity() {
      return contextId;
    }

    @Override
    public String identity() {
      return schemaId;
    }
  }

  public static final class SchemaCategorized extends IdentifiedDomainEvent {
    public final String contextId;
    public final String schemaId;
    public final String category;

    public static SchemaCategorized with(final SchemaId schemaId, final Category category) {
      return new SchemaCategorized(schemaId, category);
    }

    public SchemaCategorized(final SchemaId schemaId, final Category category) {
      this.contextId = schemaId.contextId.value;
      this.schemaId = schemaId.value;
      this.category = category.name();
    }

    @Override
    public String parentIdentity() {
      return contextId;
    }

    @Override
    public String identity() {
      return schemaId;
    }
  }

  public static final class SchemaScoped extends IdentifiedDomainEvent {
    public final String contextId;
    public final String schemaId;
    public final String scope;

    public static SchemaScoped with(final SchemaId schemaId, final Scope scope) {
      return new SchemaScoped(schemaId, scope);
    }

    public SchemaScoped(final SchemaId schemaId, final Scope scope) {
      this.contextId = schemaId.contextId.value;
      this.schemaId = schemaId.value;
      this.scope = scope.name();
    }

    @Override
    public String parentIdentity() {
      return contextId;
    }

    @Override
    public String identity() {
      return schemaId;
    }
  }

  public static final class SchemaRedefined extends IdentifiedDomainEvent {
    public final String contextId;
    public final String schemaId;
    public final String category;
    public final String scope;
    public final String name;
    public final String description;

    public static SchemaRedefined with(final SchemaId schemaId, final Category category, final Scope scope, final String name, final String description) {
      return new SchemaRedefined(schemaId, category, scope, name, description);
    }

    public SchemaRedefined(final SchemaId schemaId, final Category category, final Scope scope, final String name, final String description) {
      this.contextId = schemaId.contextId.value;
      this.schemaId = schemaId.value;
      this.category = category.name();
      this.scope = scope.name();
      this.name = name;
      this.description = description;
    }

    @Override
    public String parentIdentity() {
      return contextId;
    }

    @Override
    public String identity() {
      return schemaId;
    }
  }

  public static final class SchemaRenamed extends IdentifiedDomainEvent {
    public final String contextId;
    public final String schemaId;
    public final String name;

    public static SchemaRenamed with(final SchemaId schemaId, final String name) {
      return new SchemaRenamed(schemaId, name);
    }

    public SchemaRenamed(final SchemaId schemaId, final String name) {
      this.contextId = schemaId.contextId.value;
      this.schemaId = schemaId.value;
      this.name = name;
    }

    @Override
    public String parentIdentity() {
      return contextId;
    }

    @Override
    public String identity() {
      return schemaId;
    }
  }

  public static final class SchemaVersionDefined extends IdentifiedDomainEvent {
    public final String organizationId;
    public final String unitId;
    public final String contextId;
    public final String schemaId;
    public final String schemaVersionId;
    public final String specification;
    public final String description;
    public final String status;
    public final String previousVersion;
    public final String nextVersion;

    public static SchemaVersionDefined with(
            final SchemaVersionId schemaVersionId,
            final SchemaVersion.Specification specification,
            final String description,
            final SchemaVersion.Status status,
            final SchemaVersion.Version previousVersion,
            final SchemaVersion.Version nextVersion) {
      return new SchemaVersionDefined(schemaVersionId, specification, description, status, previousVersion, nextVersion);
    }

    public SchemaVersionDefined(
            SchemaVersionId schemaVersionId,
            final SchemaVersion.Specification specification,
            final String description,
            final SchemaVersion.Status status,
            final SchemaVersion.Version previousVersion,
            final SchemaVersion.Version nextVersion) {
      this.organizationId = schemaVersionId.organizationId().value;
      this.unitId = schemaVersionId.unitId().value;
      this.contextId = schemaVersionId.contextId().value;
      this.schemaId = schemaVersionId.schemaId.value;
      this.schemaVersionId = schemaVersionId.value;
      this.specification = specification.value;
      this.description = description;
      this.status = status.toString();
      this.previousVersion = previousVersion.value;
      this.nextVersion = nextVersion.value;
    }

    @Override
    public String parentIdentity() {
      return schemaId;
    }

    @Override
    public String identity() {
      return schemaVersionId;
    }
  }

  public static final class SchemaVersionDescribed extends IdentifiedDomainEvent {
    public final String schemaId;
    public final String schemaVersionId;
    public final String description;

    public static SchemaVersionDescribed with(final SchemaVersionId schemaVersionId, final String description) {
      return new SchemaVersionDescribed(schemaVersionId, description);
    }

    public SchemaVersionDescribed(final SchemaVersionId schemaVersionId, final String description) {
      this.schemaId = schemaVersionId.schemaId.value;
      this.schemaVersionId = schemaVersionId.value;
      this.description = description;
    }

    @Override
    public String parentIdentity() {
      return schemaId;
    }

    @Override
    public String identity() {
      return schemaVersionId;
    }
  }

  public static final class SchemaVersionAssigned extends IdentifiedDomainEvent {
    public final String schemaId;
    public final String schemaVersionId;
    public final String version;

    public static SchemaVersionAssigned with(final SchemaVersionId schemaVersionId, final SchemaVersion.Version version) {
      return new SchemaVersionAssigned(schemaVersionId, version);
    }

    public SchemaVersionAssigned(SchemaVersionId schemaVersionId, final SchemaVersion.Version version) {
      this.schemaId = schemaVersionId.schemaId.value;
      this.schemaVersionId = schemaVersionId.value;
      this.version = version.value;
    }

    @Override
    public String parentIdentity() {
      return schemaId;
    }

    @Override
    public String identity() {
      return schemaVersionId;
    }
  }

  public static final class SchemaVersionSpecified extends IdentifiedDomainEvent {
    public final String schemaId;
    public final String schemaVersionId;
    public final String specification;

    public static SchemaVersionSpecified with(SchemaVersionId schemaVersionId, final SchemaVersion.Specification specification) {
      return new SchemaVersionSpecified(schemaVersionId, specification);
    }

    public SchemaVersionSpecified(SchemaVersionId schemaVersionId, final SchemaVersion.Specification specification) {
      this.schemaId = schemaVersionId.schemaId.value;
      this.schemaVersionId = schemaVersionId.value;
      this.specification = specification.value;
    }

    @Override
    public String parentIdentity() {
      return schemaId;
    }

    @Override
    public String identity() {
      return schemaVersionId;
    }
  }

  public static final class SchemaVersionPublished extends IdentifiedDomainEvent {
    public final String schemaId;
    public final String schemaVersionId;

    public static SchemaVersionPublished with(final SchemaVersionId schemaVersionId) {
      return new SchemaVersionPublished(schemaVersionId);
    }

    public SchemaVersionPublished(final SchemaVersionId schemaVersionId) {
      this.schemaId = schemaVersionId.schemaId.value;
      this.schemaVersionId = schemaVersionId.value;
    }

    @Override
    public String parentIdentity() {
      return schemaId;
    }

    @Override
    public String identity() {
      return schemaVersionId;
    }
  }

  public static final class SchemaVersionDeprecated extends IdentifiedDomainEvent {
    public final String schemaId;
    public final String schemaVersionId;

    public static SchemaVersionDeprecated with(final SchemaVersionId schemaVersionId) {
      return new SchemaVersionDeprecated(schemaVersionId);
    }

    public SchemaVersionDeprecated(final SchemaVersionId schemaVersionId) {
      this.schemaId = schemaVersionId.schemaId.value;
      this.schemaVersionId = schemaVersionId.value;
    }

    @Override
    public String parentIdentity() {
      return schemaId;
    }

    @Override
    public String identity() {
      return schemaVersionId;
    }
  }

  public static final class SchemaVersionRemoved extends IdentifiedDomainEvent {
    public final String schemaId;
    public final String schemaVersionId;

    public static SchemaVersionRemoved with(final SchemaVersionId schemaVersionId) {
      return new SchemaVersionRemoved(schemaVersionId);
    }

    public SchemaVersionRemoved(final SchemaVersionId schemaVersionId) {
      this.schemaId = schemaVersionId.schemaId.value;
      this.schemaVersionId = schemaVersionId.value;
    }

    @Override
    public String parentIdentity() {
      return schemaId;
    }

    @Override
    public String identity() {
      return schemaVersionId;
    }
  }

  public static final class UnitDefined extends IdentifiedDomainEvent {
    public final String organizationId;
    public final String unitId;
    public final String name;
    public final String description;

    public static UnitDefined with(final UnitId unitId, final String name, final String description) {
      return new UnitDefined(unitId, name, description);
    }

    public UnitDefined(final UnitId unitId, final String name, final String description) {
      this.organizationId = unitId.organizationId.value;
      this.unitId = unitId.value;
      this.name = name;
      this.description = description;
    }

    @Override
    public String identity() {
      return unitId;
    }

    @Override
    public String parentIdentity() {
      return organizationId;
    }
  }

  public static final class UnitDescribed extends IdentifiedDomainEvent {
    public final String organizationId;
    public final String unitId;
    public final String description;

    public static UnitDescribed with(final UnitId unitId, final String description) {
      return new UnitDescribed(unitId, description);
    }

    public UnitDescribed(final UnitId unitId, final String description) {
      this.organizationId = unitId.organizationId.value;
      this.unitId = unitId.value;
      this.description = description;
    }

    @Override
    public String identity() {
      return unitId;
    }

    @Override
    public String parentIdentity() {
      return organizationId;
    }
  }

  public static final class UnitRedefined extends IdentifiedDomainEvent {
    public final String organizationId;
    public final String unitId;
    public final String name;
    public final String description;

    public static UnitRedefined with(final UnitId unitId, final String name, final String description) {
      return new UnitRedefined(unitId, name, description);
    }

    public UnitRedefined(final UnitId unitId, final String name, final String description) {
      this.organizationId = unitId.organizationId.value;
      this.unitId = unitId.value;
      this.name = name;
      this.description = description;
    }

    @Override
    public String identity() {
      return unitId;
    }

    @Override
    public String parentIdentity() {
      return organizationId;
    }
  }

  public static final class UnitRenamed extends IdentifiedDomainEvent {
    public final String organizationId;
    public final String unitId;
    public final String name;

    public static UnitRenamed with(final UnitId unitId, final String name) {
      return new UnitRenamed(unitId, name);
    }

    public UnitRenamed(final UnitId unitId, final String name) {
      this.organizationId = unitId.organizationId.value;
      this.unitId = unitId.value;
      this.name = name;
    }

    @Override
    public String identity() {
      return unitId;
    }

    @Override
    public String parentIdentity() {
      return organizationId;
    }
  }
}
