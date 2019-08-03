// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.SchemaId;

public interface Schema {
  static SchemaId uniqueId(final ContextId contextId) {
    return SchemaId.uniqueFor(contextId);
  }

  static Schema with(final Stage stage, final ContextId contextId, final String name, final String description) {
    return with(stage, uniqueId(contextId), name, description);
  }

  static Schema with(final Stage stage, final SchemaId schemaId, final String name, final String description) {
    final Schema schema = stage.actorFor(Schema.class, SchemaEntity.class, schemaId);
    schema.renameTo(name).andThen(s -> s.withDescription(description));
    return schema;
  }

  Completes<SchemaState> defineWith(final Category category, final String name, final String description);

  Completes<SchemaState> describeAs(final String description);

  Completes<SchemaState> recategorizedAs(final Category category);

  Completes<SchemaState> renameTo(final String name);
}
