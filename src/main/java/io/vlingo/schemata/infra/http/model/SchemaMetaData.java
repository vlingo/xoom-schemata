// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.http.model;

import java.util.List;

import io.vlingo.schemata.model.SchemaEntity;

public class SchemaMetadata {
  public final String id;
  public final String name;
  public final List<Schema> versions;

  private SchemaMetadata(final String id, final String name, final List<Schema> versions) {
    this.id = id;
    this.name = name;
    this.versions = versions;
  }

  public static SchemaMetadata from(final SchemaEntity ve) {
    // TODO: Adapt from domain model to API model
    throw new UnsupportedOperationException();
  }

  public static SchemaMetadata from(final String id, final String name, final List<Schema> versions) {
    return new SchemaMetadata(id, name, versions);
  }
}
