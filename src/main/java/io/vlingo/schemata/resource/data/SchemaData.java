// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource.data;

import java.util.ArrayList;
import java.util.List;

import io.vlingo.schemata.model.SchemaState;

public class SchemaData {
  public final String organizationId;
  public final String unitId;
  public final String contextId;
  public final String schemaId;
  public final String category;
  public final String scope;
  public final String name;
  public final String description;

  public static SchemaData from(final SchemaState state) {
    return new SchemaData(state.schemaId.organizationId().value, state.schemaId.unitId().value, state.schemaId.contextId.value, state.schemaId.value, state.category.name(), state.scope.name(), state.name, state.description);
  }

  public static List<SchemaData> from(final List<SchemaState> states) {
    final List<SchemaData> data = new ArrayList<>(states.size());

    for (final SchemaState state : states) {
      data.add(SchemaData.from(state));
    }

    return data;
  }

  public static SchemaData from(final String organizationId, final String unitId, final String contextId, final String schemaId, final String category, final String scope, final String name, final String description) {
    return new SchemaData(organizationId, unitId, contextId, schemaId, category, scope, name, description);
  }

  public static SchemaData just(final String category, final String scope, final String name, final String description) {
    return new SchemaData("", "", "", "", category, scope, name, description);
  }

  @Override
  public String toString() {
    return "SchemaData [organizationId=" + organizationId + ", unitId=" + unitId + ", contextId=" + contextId
            + ", schemaId=" + schemaId + ", category=" + category + ", scope=" + scope + ", name=" + name + ", description=" + description
            + "]";
  }

  private SchemaData(final String organizationId, final String unitId, final String contextId, final String schemaId, final String category, final String scope, final String name, final String description) {
    this.organizationId = organizationId;
    this.unitId = unitId;
    this.contextId = contextId;
    this.schemaId = schemaId;
    this.category = category;
    this.scope = scope;
    this.name = name;
    this.description = description;
  }
}
