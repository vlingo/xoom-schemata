// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource.data;

public class PathData {
  public final String organization;
  public final String unit;
  public final String context;
  public final String schema;
  public final String version;

  public static PathData from(final String reference) {
    return new PathData(reference);
  }

  public PathData(final String reference) {
    final String[] parts = reference.split(":");

    assert parts.length == 5 : "The reference path must have five parts: org:unit:context:schema:version";

    this.organization = parts[0];
    this.unit = parts[1];
    this.context = parts[2];
    this.schema = parts[3];
    this.version = parts[4];
  }

  @Override
  public String toString() {
    return "PathData [organization=" + organization + ", unit=" + unit + ", context=" + context + ", schema=" + schema
            + ", version=" + version + "]";
  }
}
