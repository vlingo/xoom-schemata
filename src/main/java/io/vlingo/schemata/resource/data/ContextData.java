// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource.data;

import io.vlingo.schemata.model.ContextState;

public class ContextData {
  public final String id;
  public final String name;
  public final String description;

  public static ContextData from(final ContextState state) {
    return new ContextData(state.contextId.value, state.namespace, state.description);
  }

  public static ContextData from(final String id, final String name) {
    return from(id, name, "");
  }

  public static ContextData from(final String id, final String name, final String description) {
    return new ContextData(id, name, description);
  }

  private ContextData(final String id, String name, final String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }
}
