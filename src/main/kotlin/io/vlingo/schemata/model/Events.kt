// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model

import io.vlingo.lattice.model.DomainEvent
import io.vlingo.schemata.model.Id.ContextId
import io.vlingo.schemata.model.Id.OrganizationId
import io.vlingo.schemata.model.Id.SchemaId

object Events {
  class ContextDefined(organizationId: OrganizationId, contextId: ContextId, namespace: String, description: String) : DomainEvent() {
    val organizationId: String = organizationId.value
    val contextId: String = contextId.value
    val namespace: String = namespace
    val description: String = description
  }
  class ContextDescribed(organizationId: OrganizationId, contextId: ContextId, description: String) : DomainEvent() {
    val organizationId: String = organizationId.value
    val contextId: String = contextId.value
    val description: String = description
  }
  class ContextNamespaceChanged(organizationId: OrganizationId, contextId: ContextId, val name: String) : DomainEvent() {
    val organizationId: String = organizationId.value
    val contextId: String = contextId.value
    val namespace: String = name
  }


  class OrganizationDefined(organizationId: OrganizationId, parentId: OrganizationId, name: String, description: String) : DomainEvent() {
    val organizationId: String = organizationId.value
    val parentId = parentId.value
    val name: String = name
    val description: String = description
  }
  class OrganizationDescribed(organizationId: OrganizationId, description: String) : DomainEvent() {
    val organizationId: String = organizationId.value
    val description: String = description
  }
  class OrganizationRenamed(organizationId: OrganizationId, name: String) : DomainEvent() {
    val organizationId: String = organizationId.value
    val name: String = name
  }


  class SchemaDefined(organizationId: Id, contextId: ContextId, schemaId: SchemaId, category: Category, name: String, description: String) : DomainEvent() {
    val organizationId: String = organizationId.value
    val contextId: String = contextId.value
    val schemaId: String = schemaId.value
    val category: String = category.name
    val name: String = name
    val description: String = description
  }
  class SchemaDescribed(organizationId: Id, contextId: ContextId, schemaId: SchemaId, description: String) : DomainEvent() {
    val organizationId: String = organizationId.value
    val contextId: String = contextId.value
    val schemaId: String = schemaId.value
    val description: String = description
  }
  class SchemaRecategorized(organizationId: Id, contextId: ContextId, schemaId: SchemaId, category: Category) : DomainEvent() {
    val organizationId: String = organizationId.value
    val contextId: String = contextId.value
    val schemaId: String = schemaId.value
    val category: String = category.name
  }
  class SchemaRenamed(organizationId: Id, contextId: ContextId, schemaId: SchemaId, name: String) : DomainEvent() {
    val organizationId: String = organizationId.value
    val contextId: String = contextId.value
    val schemaId: String = schemaId.value
    val name: String = name
  }
}
