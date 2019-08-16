// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource.data;

import java.util.List;

import io.vlingo.schemata.model.UnitEntity;

public class Unit {
  public final String id;
  public final String name;
  public final String description;
  public final List<ContextData> contexts;

  private Unit(final String id, final String name, final String description, final List<ContextData> contexts) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.contexts = contexts;
  }

  public static Unit from(final UnitEntity ue) {
    // TODO: Adapt from domain model to API model
    throw new UnsupportedOperationException();
  }

  public static Unit from(final String id, final String name, final List<ContextData> contexts) {
    return from(id, name, "", contexts);
  }

  public static Unit from(final String id, final String name, final String description, final List<ContextData> contexts) {
    return new Unit(id, name, description, contexts);
  }
}
