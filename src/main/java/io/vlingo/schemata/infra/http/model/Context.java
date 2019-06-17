// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.http.model;

import io.vlingo.schemata.model.ContextEntity;

public class Context {
  public final String id;
  public final String name;
  public final String description;

  private Context(final String id, String name, final String description) {
    this.id = id;
    this.name = name;
    this.description = description;
  }

  public static Context from(final ContextEntity ce) {
    // TODO: Adapt from domain model to API model
    throw new UnsupportedOperationException();
  }

  public static Context from(final String id, final String name) {
    return from(id, name, "");
  }

  public static Context from(final String id, final String name, final String description) {
    return new Context(id, name, description);
  }
}
