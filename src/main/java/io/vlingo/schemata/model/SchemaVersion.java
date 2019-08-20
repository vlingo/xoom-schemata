// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.schemata.model;

import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.SchemaVersionId;

public interface SchemaVersion {
  static SchemaVersionId uniqueId(final SchemaId schemaId) {
    return SchemaVersionId.uniqueFor(schemaId);
  }

  static SchemaVersionId uniqueId(final SchemaVersionId previousSchemaVersionId) {
    return SchemaVersionId.nextUniqueFrom(previousSchemaVersionId);
  }

  static SchemaVersion with(final Stage stage, final SchemaId schemaId,
          final String description, final Specification definition,
          final Status status, final Version version) {
    return with(stage, uniqueId(schemaId), description, definition, status, version);
  }

  static SchemaVersion with(final Stage stage, final SchemaVersionId previousSchemaVersionId,
          final String description, final Specification definition, final Status status, final Version version) {
    final SchemaVersion schemaVersion = stage.actorFor(SchemaVersion.class, SchemaVersionEntity.class, uniqueId(previousSchemaVersionId));
    schemaVersion.defineWith(description, definition, version);
    return schemaVersion;
  }

  Completes<SchemaVersionState> defineWith(final String description, final Specification specification, final Version version);

  Completes<SchemaVersionState> assignVersionOf(final Version version);

  Completes<SchemaVersionState> describeAs(final String description);

  Completes<SchemaVersionState> publish();

  Completes<SchemaVersionState> remove();

  Completes<SchemaVersionState> specifyWith(final Specification specification);

  class Specification {
    public final String value;

    public static Specification of(final String value) {
      return new Specification(value);
    }

    public Specification(final String value) {
      assert(value != null && !value.trim().isEmpty());
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }

  enum Status {

    Draft {
      @Override
      public boolean isDraft() { return true; }
    },
    Published {
      @Override
      public boolean isPublished() { return true; }
    },
    Removed {
      @Override
      public boolean isRemoved() { return true; }
    };

    public final String value = this.name();
    public boolean isDraft() { return false; }
    public boolean isPublished() { return false; }
    public boolean isRemoved() { return false; }
  }

  class Version {
    public final String value;

    public static Version of(final String value) {
      return new Version(value);
    }

    public Version(final String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }
}
