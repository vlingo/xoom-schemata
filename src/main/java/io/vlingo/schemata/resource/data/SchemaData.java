// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource.data;

import io.vlingo.schemata.model.SchemaState;

public class SchemaData {
  public final String id;
  public final String category;
  public final String description;
  public final String name;

  public static SchemaData from(final SchemaState state) {
    return new SchemaData(state.schemaId.value, state.category.name(), state.name, state.description);
  }

  public static SchemaData from(final String id, final String category, final String name, final String description) {
    return new SchemaData(id, category, name, description);
  }

  private SchemaData(final String id, final String category, final String name, final String description) {
    this.id = id;
    this.category = category;
    this.name = name;
    this.description = description;
  }
}
