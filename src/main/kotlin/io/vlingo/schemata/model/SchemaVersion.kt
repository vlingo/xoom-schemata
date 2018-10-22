// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model

import io.vlingo.schemata.model.Id.ContextId
import io.vlingo.schemata.model.Id.OrganizationId
import io.vlingo.schemata.model.Id.SchemaId
import io.vlingo.schemata.model.Id.SchemaVersionId

class SchemaVersion {
  var state: State

  init {
    this.state = State()
  }

  inner class State @JvmOverloads internal constructor(
      organizationId: OrganizationId = OrganizationId.undefined(),
      contextId: ContextId = ContextId.undefined(),
      schemaId: SchemaId = SchemaId.undefined(),
      schemaVersionId: SchemaVersionId = SchemaVersionId.undefined(),
      description: String = "",
      definition: Definition = Definition(""),
      version: Version = Version(""),
      status: Status = Status.Undefined) {

    val contextId: ContextId
    val definition: Definition
    val description: String
    val organizationId: OrganizationId
    val schemaId: SchemaId
    val schemaVersionId: SchemaVersionId
    val status: Status
    val version: Version

    init {
      this.organizationId = organizationId
      this.contextId = contextId
      this.schemaId = schemaId
      this.schemaVersionId = schemaVersionId
      this.description = description
      this.definition = definition
      this.version = version
      this.status = status
    }

    internal fun asPublished(): State {
      return State(organizationId, contextId, schemaId, schemaVersionId, description, definition, version, Status.Published)
    }

    internal fun withDefinition(definition: Definition): State {
      return State(organizationId, contextId, schemaId, schemaVersionId, description, definition, version, status)
    }

    internal fun withDescription(description: String): State {
      return State(organizationId, contextId, schemaId, schemaVersionId, description, definition, version, status)
    }

    internal fun withVersion(version: Version): State {
      return State(organizationId, contextId, schemaId, schemaVersionId, description, definition, version, status)
    }
  }

  inner class Definition(val value: String)

	enum class Status { Draft, Published, Removed, Undefined }

  inner class Version(val value: String)
}