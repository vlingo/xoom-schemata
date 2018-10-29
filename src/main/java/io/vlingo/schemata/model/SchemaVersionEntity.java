// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.schemata.model.Id.ContextId;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.SchemaId;
import io.vlingo.schemata.model.Id.SchemaVersionId;
import io.vlingo.schemata.model.Id.UnitId;
import io.vlingo.schemata.model.SchemaVersion.Definition;
import io.vlingo.schemata.model.SchemaVersion.Status;
import io.vlingo.schemata.model.SchemaVersion.Version;

public final class SchemaVersionEntity {
  private State state;

  public SchemaVersionEntity(
          final OrganizationId organizationId,
          final UnitId unitId,
          final ContextId contextId,
          final SchemaId schemaId,
          final SchemaVersionId schemaVersionId,
          final String description,
          final Definition definition,
          final Status status,
          final Version version) {
    
  }

  public class State {
    public final OrganizationId organizationId;
    public final UnitId unitId;
    public final ContextId contextId;
    public final SchemaId schemaId;
    public final SchemaVersionId schemaVersionId;
    public final String description;
    public final Definition definition;
    public final Status status;
    public final Version version;

    public State(
            final OrganizationId organizationId,
            final UnitId unitId,
            final ContextId contextId,
            final SchemaId schemaId,
            final SchemaVersionId schemaVersionId,
            final String description,
            final Definition definition,
            final Version version,
            final Status status) {
      this.organizationId = organizationId;
      this.unitId = unitId;
      this.contextId = contextId;
      this.schemaId = schemaId;
      this.schemaVersionId = schemaVersionId;
      this.description = description;
      this.definition = definition;
      this.version = version;
      this.status = status;
    }

    public State asPublished() {
      return new State(this.organizationId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
              this.description, this.definition, this.version, Status.Published);
    }

    public State withDefinition(final Definition definition) {
      return new State(this.organizationId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
              this.description, definition, this.version, this.status);
    }

    public State withDescription(final String description) {
      return new State(this.organizationId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
              description, this.definition, this.version, this.status);
    }

    public State withVersion(final Version version) {
      return new State(this.organizationId, this.unitId, this.contextId, this.schemaId, this.schemaVersionId,
              this.description, this.definition, version, this.status);
    }
  }
}