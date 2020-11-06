// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.schemata.model.Id.SchemaVersionId;

public class SchemaVersionState {
  public final SchemaVersionId schemaVersionId;
  public final String description;
  public final SchemaVersion.Specification specification;
  public final SchemaVersion.Status status;
  public final SchemaVersion.Version previousVersion;
  public final SchemaVersion.Version currentVersion;

  public static SchemaVersionState from(final SchemaVersionId schemaVersionId) {
    return new SchemaVersionState(schemaVersionId);
  }

  public static SchemaVersionState from(
          final SchemaVersionId schemaVersionId,
          final SchemaVersion.Specification specification,
          final String description,
          final SchemaVersion.Status status,
          final SchemaVersion.Version previousVersion,
          final SchemaVersion.Version currentVersion) {
    return new SchemaVersionState(
            schemaVersionId,
            specification,
            description,
            status,
            previousVersion,
            currentVersion);
  }

  public SchemaVersionState asPublished() {
    return new SchemaVersionState(this.schemaVersionId, this.specification, this.description, SchemaVersion.Status.Published, this.previousVersion, this.currentVersion);
  }

  public SchemaVersionState asDeprecated() {
    return new SchemaVersionState(this.schemaVersionId, this.specification, this.description, SchemaVersion.Status.Deprecated, this.previousVersion, this.currentVersion);
  }

  public SchemaVersionState asRemoved() {
    return new SchemaVersionState(this.schemaVersionId, this.specification, this.description, SchemaVersion.Status.Removed, this.previousVersion, this.currentVersion);
  }

  public SchemaVersionState defineWith(final String description, final SchemaVersion.Specification specification, final SchemaVersion.Version previousVersion, final SchemaVersion.Version currentVersion) {
    return new SchemaVersionState(this.schemaVersionId, specification, description, SchemaVersion.Status.Draft, previousVersion, currentVersion);
  }

  public SchemaVersionState withSpecification(final SchemaVersion.Specification specification) {
    return new SchemaVersionState(this.schemaVersionId, specification, this.description, this.status, this.previousVersion, this.currentVersion);
  }

  public SchemaVersionState withDescription(final String description) {
    return new SchemaVersionState(this.schemaVersionId, this.specification, description, this.status, this.previousVersion, this.currentVersion);
  }

  public SchemaVersionState withVersion(final SchemaVersion.Version currentVersion) {
    return new SchemaVersionState(this.schemaVersionId, this.specification, this.description, this.status, this.previousVersion, currentVersion);
  }

  @Override
  public int hashCode() {
    return 31 * this.schemaVersionId.value.hashCode();
  }

  @Override
  public boolean equals(final Object other) {
    if (other == null || other.getClass() != getClass()) {
      return false;
    } else if (this == other) {
      return true;
    }

    final SchemaVersionState otherState = (SchemaVersionState) other;

    return this.schemaVersionId == otherState.schemaVersionId;
  }

  @Override
  public String toString() {
    return "SchemaVersionState[schemaVersionId=" + schemaVersionId.value +
            " specification=" + specification +
            " description=" + description +
            " status=" + status.name() +
            " previousVersion=" + previousVersion +
            " currentVersion=" + currentVersion + "]";
  }

  private SchemaVersionState(final SchemaVersionId schemaVersionId) {
    this(schemaVersionId,
         new SchemaVersion.Specification("(unknown)"),
         "",
         SchemaVersion.Status.Draft,
         new SchemaVersion.Version("0.0.0"),
         new SchemaVersion.Version("0.0.0"));
  }

  private SchemaVersionState(
          final SchemaVersionId schemaVersionId,
          final SchemaVersion.Specification specification,
          final String description,
          final SchemaVersion.Status status,
          final SchemaVersion.Version previousVersion,
          final SchemaVersion.Version currentVersion) {
    this.schemaVersionId = schemaVersionId;
    this.specification = specification;
    this.description = description;
    this.status = status;
    this.previousVersion = previousVersion;
    this.currentVersion = currentVersion;
  }
}
