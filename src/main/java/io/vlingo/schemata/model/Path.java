// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

public class Path {
  public final String organization;
  public final String unit;
  public final String context;
  public final String schema;
  public final String version;

  public boolean hasOrganization() {
    return !organization.isEmpty();
  }

  public boolean hasUnit() {
    return !unit.isEmpty();
  }

  public boolean hasContext() {
    return !context.isEmpty();
  }

  public boolean hasSchema() {
    return !schema.isEmpty();
  }

  public boolean hasVersion() {
    return !version.isEmpty();
  }

  public String versionOrElse(final String whenNoVersion) {
    return hasVersion() ? version : whenNoVersion;
  }

  public String toReference() {
    final StringBuilder builder = new StringBuilder();

    final String[] all = new String[] { organization, unit, context, schema, version };
    String separator = "";

    for (final String reference : all) {
      builder.append(separator).append(reference);
      separator = ":";
    }

    return builder.toString();
  }

  @Override
  public String toString() {
    return "Path [organization=" + organization + ", unit=" + unit + ", context=" + context + ", schema=" + schema
            + ", version=" + version + "]";
  }

  public Path(final String organization, final String unit, final String context, final String schema, final String version) {
    this.organization = organization;
    this.unit = unit;
    this.context = context;
    this.schema = schema;
    this.version = version;
  }

  public Path(final String organization, final String unit, final String context, final String schema) {
    this(organization, unit, context, schema, "");
  }

  public Path(final String organization, final String unit, final String context) {
    this(organization, unit, context, "", "");
  }

  public Path(final String organization, final String unit) {
    this(organization, unit, "", "", "");
  }

  public Path(final String organization) {
    this(organization, "", "", "", "");
  }
}
