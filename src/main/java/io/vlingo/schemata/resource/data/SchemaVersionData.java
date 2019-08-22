// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource.data;

import io.vlingo.schemata.model.SchemaVersionState;

public class SchemaVersionData {
  public final String organizationId;
  public final String unitId;
  public final String contextId;
  public final String schemaId;
  public final String schemaVersionId;
  public final String description;
  public final String specification;
  public final String status;
  public final String previousVersion;
  public final String currentVersion;

  public static SchemaVersionData from(final SchemaVersionState version) {
    return new SchemaVersionData(
            version.schemaVersionId.organizationId().value,
            version.schemaVersionId.unitId().value,
            version.schemaVersionId.contextId().value,
            version.schemaVersionId.schemaId.value,
            version.schemaVersionId.value,
            version.specification.value,
            version.description,
            version.status.value,
            version.previousVersion.value,
            version.currentVersion.value);
  }

  public static SchemaVersionData from(
          final String organizationId,
          final String unitId,
          final String contextId,
          final String schemaId,
          final String schemaVersionId,
          final String specification,
          final String description,
          final String status,
          final String previousVersion,
          final String nextVersion) {
  return new SchemaVersionData(
          organizationId,
          unitId,
          contextId,
          schemaId,
          schemaVersionId,
          specification,
          description,
          status,
          previousVersion,
          nextVersion);
  }

  private SchemaVersionData(
          final String organizationId,
          final String unitId,
          final String contextId,
          final String schemaId,
          final String schemaVersionId,
          final String specification,
          final String description,
          final String status,
          final String previousVersion,
          final String nextVersion) {
    this.organizationId = organizationId;
    this.unitId = unitId;
    this.contextId = contextId;
    this.schemaId = schemaId;
    this.schemaVersionId = schemaVersionId;
    this.specification = specification;
    this.description = description;
    this.status = status;
    this.previousVersion = previousVersion;
    this.currentVersion = nextVersion;
  }
}
