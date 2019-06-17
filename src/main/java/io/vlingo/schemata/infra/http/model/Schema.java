// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.http.model;

import io.vlingo.schemata.model.SchemaEntity;

public class Schema {
  public final String id;
  public final String status;

  private Schema(final String id, final String status) {
    this.id = id;
    this.status = status;
  }

  public static Schema from(final SchemaEntity ue) {
    // TODO: Adapt from domain model to API model
    throw new UnsupportedOperationException();
  }

  public static Schema from(final String id, final String status) {
    return new Schema(id, status);
  }
}
