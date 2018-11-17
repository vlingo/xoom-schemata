// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import java.util.UUID;

import io.vlingo.common.identity.IdentityGeneratorType;

public abstract class Id {
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
      return new OrganizationId(UUID.fromString(id).toString());
    }

    public static OrganizationId undefined() {
      return new OrganizationId("");
    }

    public static OrganizationId unique() {
      return new OrganizationId(IdentityGeneratorType.Random.generate().toString());
    }

    private OrganizationId(final String value) {
      super(value);
    }
  }

  public static class UnitId extends Id {
    public static UnitId existing(final String id) {
      return new UnitId(id);
    }

    public static UnitId undefined() {
      return new UnitId("");
    }

    public static UnitId uniqueFor(final OrganizationId organizationId) {
      return new UnitId(organizationId.value + ":" + IdentityGeneratorType.Random.generate().toString());
    }

    private UnitId(final String value) {
      super(value);
    }
  }

  public static class ContextId extends Id {
    public static ContextId existing(final String id) {
      return new ContextId(id);
    }

    public static ContextId undefined() {
      return new ContextId("");
    }

    public static ContextId uniqueFor(final UnitId unitId) {
      return new ContextId(unitId.value + ":" + IdentityGeneratorType.Random.generate().toString());
    }

    private ContextId(final String value) {
      super(value);
    }
  }

  public static class SchemaId extends Id {
    public static SchemaId existing(final String id) {
      return new SchemaId(id);
    }

    public static SchemaId undefined() {
      return new SchemaId("");
    }

    public static SchemaId uniqueFor(final ContextId contextId) {
      return new SchemaId(contextId.value + ":" + IdentityGeneratorType.Random.generate().toString());
    }

    private SchemaId(final String value) {
      super(value);
    }
  }

  public static class SchemaVersionId extends Id {
    public static SchemaVersionId existing(final String id) {
      return new SchemaVersionId(id);
    }

    public static SchemaVersionId undefined() {
      return new SchemaVersionId("");
    }

    public static SchemaVersionId uniqueFor(final SchemaId schemaId) {
      return new SchemaVersionId(schemaId.value + ":1");
    }

    public static SchemaVersionId uniqueFor(final SchemaVersionId previousSchemaVersionId) {
      final int index = previousSchemaVersionId.value.lastIndexOf(':');
      assert(index > 0);
      final String prefix = previousSchemaVersionId.value.substring(0, index);
      final int previousSequence = Integer.parseInt(previousSchemaVersionId.value.substring(index + 1));
      return new SchemaVersionId(prefix + ":" + (previousSequence + 1));
    }

    public SchemaVersionId(final String value) {
      super(value);
    }
  }
}
