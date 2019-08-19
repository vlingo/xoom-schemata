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

    public static ContextId existing(final String id) {
      final String[] parts = id.split(Separator);
      return new ContextId(new UnitId(OrganizationId.existing(parts[0]), parts[1]), parts[2]);
    }

    public static ContextId undefined() {
      return new ContextId(UnitId.undefined());
    }

    public static ContextId uniqueFor(final UnitId unitId) {
      return new ContextId(unitId);
    }

    private ContextId(final UnitId unitId) {
      this(unitId, IdentityGeneratorType.Random.generate().toString());
    }

    private ContextId(final UnitId unitId, final String contextIdValue) {
      super(contextIdValue);
      this.unitId = unitId;
    }
  }

  public static class SchemaId extends Id {
    public final ContextId contextId;

    public static SchemaId existing(final String id) {
      final String[] parts = id.split(Separator);
      return new SchemaId(new ContextId(new UnitId(OrganizationId.existing(parts[0]), parts[1]), parts[2]), parts[3]);
    }

    public static SchemaId undefined() {
      return new SchemaId(ContextId.undefined(), "");
    }

    public static SchemaId uniqueFor(final ContextId contextId) {
      return new SchemaId(contextId);
    }

    SchemaId(final ContextId contextId) {
      this(contextId, IdentityGeneratorType.Random.generate().toString());
    }

    SchemaId(final ContextId contextId, final String schemaIdValue) {
      super(schemaIdValue);
      this.contextId = contextId;
    }
  }

  public static class SchemaVersionId extends Id {
    public final SchemaId schemaId;

    public static SchemaVersionId existing(final String id) {
      final String[] parts = id.split(Separator);
      return new SchemaVersionId(new SchemaId(new ContextId(new UnitId(OrganizationId.existing(parts[0]), parts[1]), parts[2]), parts[3]), parts[4]);
    }

    public static SchemaVersionId undefined() {
      return new SchemaVersionId(SchemaId.undefined(), "");
    }

    public static SchemaVersionId uniqueFor(final SchemaId schemaId) {
      return new SchemaVersionId(schemaId);
    }

    public static SchemaVersionId nextUniqueFrom(final SchemaVersionId previousSchemaVersionId) {
      final int index = previousSchemaVersionId.value.lastIndexOf(':');
      assert(index > 0);
      final String prefix = previousSchemaVersionId.value.substring(0, index);
      final SchemaId schemaId = SchemaId.existing(prefix);
      final int previousSequence = Integer.parseInt(previousSchemaVersionId.value.substring(index + 1));
      return new SchemaVersionId(schemaId, String.valueOf(previousSequence + 1));
    }

    public SchemaVersionId(final SchemaId schemaId) {
      this(schemaId, IdentityGeneratorType.Random.generate().toString());
    }

    public SchemaVersionId(final SchemaId schemaId, final String value) {
      super(value);
      this.schemaId = schemaId;
    }
  }
}
