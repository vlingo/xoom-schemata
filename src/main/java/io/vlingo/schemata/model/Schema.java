// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
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
    return stage.actorFor(Definition.has(SchemaEntity.class, Definition.parameters(schemaId, name, description)),
            Schema.class);
  }

  void describeAs(final String description);

  void recategorizedAs(final Category category);

  void renameTo(final String name);
}
