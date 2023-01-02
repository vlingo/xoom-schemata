// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

public class SchemaVersionReference {
  public final String organizationId;
  public final String unitId;
  public final String contextId;
  public final String schemaId;
  public final String schemaVersionId;

  public static SchemaVersionReference identity(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId) {
    return new SchemaVersionReference(organizationId, unitId, contextId, schemaId, schemaVersionId);
  }

  private SchemaVersionReference(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId) {
    this.organizationId = organizationId;
    this.unitId = unitId;
    this.contextId = contextId;
    this.schemaId = schemaId;
    this.schemaVersionId = schemaVersionId;
  }
}
