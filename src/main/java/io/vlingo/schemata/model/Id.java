// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.common.identity.IdentityGeneratorType;

public abstract class Id {
  private static final String Separator = ":";

  public final String value;

  public final boolean isDefined() {
    return value.length() > 0;
  }

  public final boolean isUndefined() {
    return value.isEmpty();
  }

  public Id(final String value) {
    this.value = value;
  }

  public static class OrganizationId extends Id {
    public static OrganizationId existing(final String id) {
      return new OrganizationId(id);
    }

    public static OrganizationId undefined() {
      return new OrganizationId("");
    }

    public static OrganizationId unique() {
      return new OrganizationId();
    }

    OrganizationId() {
      super(IdentityGeneratorType.Random.generate().toString());
    }

    OrganizationId(final String organizationIdValue) {
      super(organizationIdValue);
    }
  }

  public static class UnitId extends Id {
    public final OrganizationId organizationId;

    public static UnitId existing(final String id) {
      final String[] parts = id.split(Separator);
      return new UnitId(new OrganizationId(parts[0]), parts[1]);
    }

    public static UnitId existing(final String organizationId, final String unitId) {
      return new UnitId(OrganizationId.existing(organizationId), unitId);
    }

    public static UnitId undefined() {
      return new UnitId(OrganizationId.undefined());
    }

    public static UnitId uniqueFor(final OrganizationId organizationId) {
      return new UnitId(organizationId);
    }

    UnitId(final OrganizationId organizationId) {
      this(organizationId, IdentityGeneratorType.Random.generate().toString());
    }

    UnitId(final OrganizationId organizationId, final String unitIdValue) {
      super(unitIdValue);
      this.organizationId = organizationId;
    }
  }

  public static class ContextId extends Id {
    public final UnitId unitId;

    public static ContextId existing(final String compositeId) {
      final String[] parts = compositeId.split(Separator);
      return new ContextId(new UnitId(OrganizationId.existing(parts[0]), parts[1]), parts[2]);
    }

    public static ContextId existing(final String organizationId, final String unitId, final String contextId) {
      return new ContextId(organizationId, unitId, contextId);
    }

    public static ContextId undefined() {
      return new ContextId(UnitId.undefined());
    }

    public static ContextId uniqueFor(final UnitId unitId) {
      return new ContextId(unitId);
    }

    public OrganizationId organizationId() {
      return this.unitId.organizationId;
    }

    ContextId(final UnitId unitId) {
      this(unitId, IdentityGeneratorType.Random.generate().toString());
    }

    ContextId(final UnitId unitId, final String contextIdValue) {
      super(contextIdValue);
      this.unitId = unitId;
    }

    ContextId(final String organizationId, final String unitId, final String contextId) {
      super(contextId);
      this.unitId = UnitId.existing(organizationId, unitId);
    }
  }

  public static class SchemaId extends Id {
    public final ContextId contextId;

    public static SchemaId existing(final String compositeId) {
      final String[] parts = compositeId.split(Separator);
      return new SchemaId(new ContextId(new UnitId(OrganizationId.existing(parts[0]), parts[1]), parts[2]), parts[3]);
    }

    public static SchemaId existing(final String organizationId, final String unitId, final String contextId, final String schemaId) {
      return new SchemaId(organizationId, unitId, contextId, schemaId);
    }

    public static SchemaId undefined() {
      return new SchemaId(ContextId.undefined(), "");
    }

    public static SchemaId uniqueFor(final ContextId contextId) {
      return new SchemaId(contextId);
    }

    public OrganizationId organizationId() {
      return this.contextId.organizationId();
    }

    public UnitId unitId() {
      return this.contextId.unitId;
    }

    SchemaId(final ContextId contextId) {
      this(contextId, IdentityGeneratorType.Random.generate().toString());
    }

    SchemaId(final ContextId contextId, final String schemaIdValue) {
      super(schemaIdValue);
      this.contextId = contextId;
    }

    SchemaId(final String organizationId, final String unitId, final String contextId, final String schemaId) {
      super(schemaId);
      this.contextId = ContextId.existing(organizationId, unitId, contextId);
    }
  }

  public static class SchemaVersionId extends Id {
    public final SchemaId schemaId;

    public static SchemaVersionId existing(final String id) {
      final String[] parts = id.split(Separator);
      return new SchemaVersionId(new SchemaId(new ContextId(new UnitId(OrganizationId.existing(parts[0]), parts[1]), parts[2]), parts[3]), parts[4]);
    }

    public static SchemaVersionId existing(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId) {
      return new SchemaVersionId(organizationId, unitId, contextId, schemaId, schemaVersionId);
    }

    public static SchemaVersionId undefined() {
      return new SchemaVersionId(SchemaId.undefined(), "");
    }

    public static SchemaVersionId uniqueFor(final SchemaId schemaId) {
      return new SchemaVersionId(schemaId);
    }

    public ContextId contextId() {
      return this.schemaId.contextId;
    }

    public OrganizationId organizationId() {
      return this.schemaId.organizationId();
    }

    public UnitId unitId() {
      return this.schemaId.unitId();
    }

    SchemaVersionId(final SchemaId schemaId) {
      this(schemaId, IdentityGeneratorType.Random.generate().toString());
    }

    SchemaVersionId(final SchemaId schemaId, final String value) {
      super(value);
      this.schemaId = schemaId;
    }

    SchemaVersionId(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId) {
      super(schemaVersionId);
      this.schemaId = SchemaId.existing(organizationId, unitId, contextId, schemaId);
    }
  }
}
