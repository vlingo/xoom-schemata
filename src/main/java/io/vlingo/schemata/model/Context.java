// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.ActorInstantiator;
import io.vlingo.actors.Address;
import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.UnitId;

public interface Context {
  static String nameFrom(final ContextId contextId) {
    return "C:"+contextId.value;
  }

  static ContextId uniqueId(final UnitId unitId) {
    return ContextId.uniqueFor(unitId);
  }

  static Completes<ContextState> with(
          final Stage stage,
          final UnitId unitId,
          final String namespace,
          final String description) {
    return with(stage, uniqueId(unitId), namespace, description);
  }

  static Completes<ContextState> with(
          final Stage stage,
          final ContextId contextId,
          final String namespace,
          final String description) {
    final String actorName = nameFrom(contextId);
    final Address address = stage.addressFactory().from(contextId.value, actorName);
    final Definition definition = Definition.has(ContextEntity.class, new ContextInstantiator(contextId), actorName);
    final Context context = stage.actorFor(Context.class, definition, address);
    return context.defineWith(namespace, description);
  }

  Completes<ContextState> defineWith(final String name, final String description);

  Completes<ContextState> describeAs(final String description);

  Completes<ContextState> moveToNamespace(final String namespace);

  Completes<ContextState> redefineWith(final String namespace, final String description);

  static class ContextInstantiator implements ActorInstantiator<ContextEntity> {
    private final ContextId contextId;

    public ContextInstantiator(final ContextId contextId) {
      this.contextId = contextId;
    }

    @Override
    public ContextEntity instantiate() {
      return new ContextEntity(contextId);
    }

    @Override
    public Class<ContextEntity> type() {
      return ContextEntity.class;
    }
  }
}
