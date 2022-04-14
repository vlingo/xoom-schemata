// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query.view;

public class PathBuilder {
  public final String organization;
  public final String unit;
  public final String context;
  public final String schema;
  public final String version;

  public static PathBuilder instance() {
    return new PathBuilder("", "", "", "", "");
  }

  public PathBuilder withOrganization(final String organization) {
    return new PathBuilder(organization, unit, context, schema, version);
  }

  public PathBuilder withUnit(final String unit) {
    return new PathBuilder(organization, unit, context, schema, version);
  }

  public PathBuilder withContext(final String context) {
    return new PathBuilder(organization, unit, context, schema, version);
  }

  public PathBuilder withSchema(final String schema) {
    return new PathBuilder(organization, unit, context, schema, version);
  }

  public PathBuilder withVersion(final String version) {
    return new PathBuilder(organization, unit, context, schema, version);
  }

  public boolean isFullyDefined() {
    return !organization.isEmpty() && !unit.isEmpty() && !context.isEmpty() && !schema.isEmpty() && !version.isEmpty();
  }

  @Override
  public String toString() {
    final StringBuilder builder = new StringBuilder();

    builder
      .append(organization)
      .append(":")
      .append(unit)
      .append(":")
      .append(context)
      .append(":")
      .append(schema)
      .append(":")
      .append(version);

    return builder.toString();
  }

  private PathBuilder(final String organization, final String unit, final String context, final String schema, final String version) {
    this.organization = organization;
    this.unit = unit;
    this.context = context;
    this.schema = schema;
    this.version = version;
  }
}
