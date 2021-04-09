// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.resource.data;

import io.vlingo.xoom.schemata.Schemata;

public class PathData {
  public final String organization;
  public final String unit;
  public final String context;
  public final String schema;
  public final String version;

  public final String reference;

  public static PathData versionOptional(final String reference) {
    return new PathData(reference, true);
  }

  public static PathData withVersion(final String reference) {
    return new PathData(reference, false);
  }

  public boolean hasVersion() {
    return !version.isEmpty();
  }

  public String versionOrElse(final String whenNoVersion) {
    return hasVersion() ? version : whenNoVersion;
  }

  @Override
  public String toString() {
    return "PathData [organization=" + organization + ", unit=" + unit + ", context=" + context + ", schema=" + schema
            + ", version=" + version + "]";
  }

  private PathData(final String reference, final boolean versionOptional) {
    this.reference = reference;

    final String[] parts = reference.split(Schemata.ReferenceSeparator);

    if (!versionOptional && parts.length != 5) {
      throw new IllegalArgumentException("The reference path must have five parts: org:unit:context:schema:version");
    }

    if (parts.length < 4) {
      throw new IllegalArgumentException("The reference path must have five parts: org:unit:context:schema[:version]");
    }

    this.organization = parts[0].trim();
    this.unit = parts[1].trim();
    this.context = parts[2].trim();
    this.schema = parts[3].trim();
    this.version = parts.length == 5 ? parts[4].trim() : "";
  }
}
