// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.schemata.model;

import io.vlingo.actors.Stage;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.SchemaVersionId;

public interface SchemaVersion {
  static SchemaVersionId uniqueId(final SchemaId schemaId) {
    return SchemaVersionId.uniqueFor(schemaId);
  }

  static SchemaVersion with(final Stage stage, final SchemaId schemaId,
          final Category category, final String name, final String description,
          final Specification definition, final Status status, final Version version) {
    return with(stage, uniqueId(schemaId), category, name, description, definition, status, version);
  }

  static SchemaVersion with(final Stage stage, final SchemaVersionId schemaVersionId,
          final Category category, final String name, final String description,
          final Specification definition, final Status status, final Version version) {
    return stage.actorFor(io.vlingo.actors.Definition.has(SchemaVersionEntity.class,
            io.vlingo.actors.Definition.parameters(schemaVersionId, category, name, description, definition, status, version)),
            SchemaVersion.class);
  }

  void assignStatus(final Status status);

  void assignVersion(final Version version);

  void describeAs(final String description);

  void specifiedAs(final Specification specification);

  class Specification {
    public final String value;

    public Specification(final String value) {
      this.value = value;
    }
  }

  enum Status {
    Draft, Published, Removed, Undefined;
  }

  class Version {
    public final String value;

    public Version(final String value) {
      this.value = value;
    }
  }
}
