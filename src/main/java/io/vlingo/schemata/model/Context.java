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
import io.vlingo.schemata.model.Id.UnitId;

public interface Context {
  static ContextId uniqueId(final UnitId unitId) {
    return ContextId.uniqueFor(unitId);
  }

  static Context with(
          final Stage stage,
          final UnitId unitId,
          final String name,
          final String description) {
    return with(stage, uniqueId(unitId), name, description);
  }

  static Context with(
          final Stage stage,
          final ContextId contextId,
          final String name,
          final String description) {
    final Context context = stage.actorFor(Context.class, ContextEntity.class);
    context.defineWith(contextId, name, description);
    return context;
  }

  Completes<ContextState> defineWith(final ContextId contextId, final String name, final String description);

  Completes<ContextState> changeNamespaceTo(final String namespace);

  Completes<ContextState> describeAs(final String description);
}
