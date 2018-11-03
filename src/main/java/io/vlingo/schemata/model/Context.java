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
    return stage.actorFor(Definition.has(ContextEntity.class, Definition.parameters(contextId, name, description)), Context.class);
  }

  void changeNamespaceTo(final String namespace);

  void describeAs(final String description);
}
