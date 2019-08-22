// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.schemata.model;

import io.vlingo.actors.Address;
import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.common.version.SemanticVersion;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.SchemaVersionId;

public interface SchemaVersion {
  static String nameFrom(final SchemaVersionId schemaVersionId) {
    return "V:"+schemaVersionId.value;
  }

  static SchemaVersionId uniqueId(final SchemaId schemaId) {
    return SchemaVersionId.uniqueFor(schemaId);
  }

  static Completes<SchemaVersionState> with(
          final Stage stage,
          final SchemaId schemaId,
          final Specification specification,
          final String description,
          final Version parentVersion,
          final Version childVersion) {
    return with(stage, uniqueId(schemaId), specification, description, parentVersion, childVersion);
  }

  static Completes<SchemaVersionState> with(
          final Stage stage,
          final SchemaVersionId schemaVersionId,
          final Specification specification,
          final String description,
          final Version previousVersion,
          final Version nextVersion) {

    final SemanticVersion previous = SemanticVersion.from(previousVersion.value);
    final SemanticVersion next = SemanticVersion.from(nextVersion.value);

    if (!next.isCompatibleWith(previous)) {
      throw new IllegalArgumentException("Versions are incompatible: previous: " + previousVersion.value + " next: " + nextVersion.value);
    }

    final String actorName = nameFrom(schemaVersionId);
    final Address address = stage.addressFactory().from(schemaVersionId.value, actorName);
    final Definition definition = Definition.has(SchemaVersionEntity.class, Definition.parameters(schemaVersionId), actorName);
    final SchemaVersion schemaVersion = stage.actorFor(SchemaVersion.class, definition, address);
    return schemaVersion.defineWith(specification, description, previousVersion, nextVersion);
  }

  Completes<SchemaVersionState> defineWith(final Specification specification, final String description, final Version previousVersion, final Version nextVersion);

  Completes<SchemaVersionState> describeAs(final String description);

  Completes<SchemaVersionState> publish();

  Completes<SchemaVersionState> deprecate();

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
    Deprecated {
      @Override
      public boolean isDeprecated() { return true; }
    },
    Removed {
      @Override
      public boolean isRemoved() { return true; }
    };

    public final String value = this.name();
    public boolean isDraft() { return false; }
    public boolean isPublished() { return false; }
    public boolean isDeprecated() { return false; }
    public boolean isRemoved() { return false; }
  }

  class Version {
    public final String value;

    public static Version of(final String value) {
      return new Version(value);
    }

    public Version(final String value) {
      assert(value != null);

      // asserts valid as a semantic version (not necessarily correct)
      SemanticVersion.from(value);

      this.value = value;
    }

    @Override
    public String toString() {
      return value;
    }
  }
}
