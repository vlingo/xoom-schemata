// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource.data;

import io.vlingo.schemata.model.SchemaEntity;

import java.util.List;

public class SchemaMetaData {
  public final String id;
  public final String name;
  public final List<SchemaData> versions;

  private SchemaMetaData(final String id, final String name, final List<SchemaData> versions) {
    this.id = id;
    this.name = name;
    this.versions = versions;
  }

  public static SchemaMetaData from(final SchemaEntity ve) {
    // TODO: Adapt from domain model to API model
    throw new UnsupportedOperationException();
  }

  public static SchemaMetaData from(final String id, final String name, final List<SchemaData> versions) {
    return new SchemaMetaData(id, name, versions);
  }
}
