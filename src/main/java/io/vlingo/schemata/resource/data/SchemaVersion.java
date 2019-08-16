// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource.data;

import io.vlingo.schemata.model.SchemaVersionEntity;

public class SchemaVersion {
  public final String description;
  public final String specification;
  public final String status;
  public final String version;

  private SchemaVersion(final String description, final String specification, final String status, final String version) {
    this.description = description;
    this.specification = specification;
    this.status = status;
    this.version = version;
  }

  public static SchemaVersion from(final SchemaVersionEntity ue) {
    // TODO: Adapt from domain model to API model
    throw new UnsupportedOperationException();
  }

  public static SchemaVersion from(final String description, final String specification, final String status, final String version) {
    return new SchemaVersion(description, specification, status, version);
  }
}
