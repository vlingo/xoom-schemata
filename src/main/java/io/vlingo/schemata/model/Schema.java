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
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.SchemaId;

public interface Schema {
  static String nameFrom(final SchemaId schemaId) {
    return "S:"+schemaId.value;
  }

  static SchemaId uniqueId(final ContextId contextId) {
    return SchemaId.uniqueFor(contextId);
  }

  static Completes<SchemaState> with(final Stage stage, final ContextId contextId, final Category category, final Scope scope, final String name, final String description) {
    return with(stage, uniqueId(contextId), category, scope, name, description);
  }

  static Completes<SchemaState> with(final Stage stage, final SchemaId schemaId, final Category category, final Scope scope, final String name, final String description) {
    final String actorName = nameFrom(schemaId);
    final Address address = stage.addressFactory().from(schemaId.value, actorName);
    final Definition definition = Definition.has(SchemaEntity.class, Definition.parameters(schemaId), actorName);
    final Schema schema = stage.actorFor(Schema.class, definition, address);
    return schema.defineWith(category, scope, name, description);
  }

  Completes<SchemaState> defineWith(final Category category, final Scope scope, final String name, final String description);

  Completes<SchemaState> categorizeAs(final Category category);

  Completes<SchemaState> scopeAs(final Scope scope);

  Completes<SchemaState> describeAs(final String description);

  Completes<SchemaState> renameTo(final String name);

  Completes<SchemaState> redefineWith(final Category category, final Scope scope, final String name, final String description);
}
