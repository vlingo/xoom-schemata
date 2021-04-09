// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

import io.vlingo.xoom.actors.ActorInstantiator;
import io.vlingo.xoom.actors.Address;
import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.Id.ContextId;
import io.vlingo.xoom.schemata.model.Id.SchemaId;

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
    final Definition definition = Definition.has(SchemaEntity.class, new SchemaInstantiator(schemaId), actorName);
    final Schema schema = stage.actorFor(Schema.class, definition, address);
    return schema.defineWith(category, scope, name, description);
  }

  Completes<SchemaState> defineWith(final Category category, final Scope scope, final String name, final String description);

  Completes<SchemaState> categorizeAs(final Category category);

  Completes<SchemaState> scopeAs(final Scope scope);

  Completes<SchemaState> describeAs(final String description);

  Completes<SchemaState> renameTo(final String name);

  Completes<SchemaState> redefineWith(final Category category, final Scope scope, final String name, final String description);

  static class SchemaInstantiator implements ActorInstantiator<SchemaEntity> {
    private static final long serialVersionUID = 5120497065379472196L;

    private final SchemaId schemaId;

    public SchemaInstantiator(final SchemaId schemaId) {
      this.schemaId = schemaId;
    }

    @Override
    public SchemaEntity instantiate() {
      return new SchemaEntity(schemaId);
    }

    @Override
    public Class<SchemaEntity> type() {
      return SchemaEntity.class;
    }
  }
}
