// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model

import java.util.function.BiConsumer
import io.vlingo.lattice.model.sourcing.EventSourced
import io.vlingo.schemata.model.Events.SchemaDefined
import io.vlingo.schemata.model.Events.SchemaDescribed
import io.vlingo.schemata.model.Events.SchemaRecategorized
import io.vlingo.schemata.model.Events.SchemaRenamed
import io.vlingo.schemata.model.Id.ContextId
import io.vlingo.schemata.model.Id.OrganizationId
import io.vlingo.schemata.model.Id.SchemaId

class Schema private constructor() : EventSourced() {
  private var state: State

  init {
    this.state = State()
  }

  fun describeAs(description: String) {
    apply(SchemaDescribed(state.organizationId, state.contextId, state.schemaId, description))
  }

  fun recategorizedAs(category: Category) {
    apply(SchemaRecategorized(state.organizationId, state.contextId, state.schemaId, category))
  }

  fun renameTo(name: String) {
    apply(SchemaRenamed(state.organizationId, state.contextId, state.schemaId, name))
  }

  internal fun applyDefined(event: SchemaDefined) {
    state = State(OrganizationId.existing(event.organizationId), ContextId.existing(event.contextId), SchemaId.existing(event.schemaId), Category.valueOf(event.category), event.name, event.description)
  }

  internal fun applyDescribed(event: SchemaDescribed) {
    state = state.withDescription(event.description)
  }

  internal fun applyRecategorized(event: SchemaRecategorized) {
    state = state.withCategory(Category.valueOf(event.category))
  }

  internal fun applyRenamed(event: SchemaRenamed) {
    state = state.withName(event.name)
  }

  inner class State @JvmOverloads internal constructor(
      organizationId: OrganizationId = OrganizationId.undefined(),
      contextId: ContextId = ContextId.undefined(),
      schemaId: SchemaId = SchemaId.undefined(),
      category: Category = Category.None,
      name: String = "",
      description: String = "") {

    val category: Category
    val contextId: ContextId
    val description: String
    val name: String
    val organizationId: OrganizationId
    val schemaId: SchemaId

    init {
      this.organizationId = organizationId
      this.contextId = contextId
      this.schemaId = schemaId
      this.category = category
      this.name = name
      this.description = description
    }

    internal fun withCategory(category: Category): State {
      return State(organizationId, contextId, schemaId, category, name, description)
    }

    internal fun withDescription(description: String): State {
      return State(organizationId, contextId, schemaId, category, name, description)
    }

    internal fun withName(name: String): State {
      return State(organizationId, contextId, schemaId, category, name, description)
    }
  }

  companion object {
    init {
      val applySchemaDefinedFn = BiConsumer<Schema, SchemaDefined>({ obj, event -> obj.applyDefined(event) })
      registerConsumer(Schema::class.java, SchemaDefined::class.java, applySchemaDefinedFn)
      val applySchemaRecategorizedFn = BiConsumer<Schema, SchemaRecategorized>({ obj, event -> obj.applyRecategorized(event) })
      registerConsumer(Schema::class.java, SchemaDefined::class.java, applySchemaRecategorizedFn)
      val applySchemaDescribedFn = BiConsumer<Schema, SchemaDescribed>({ obj, event -> obj.applyDescribed(event) })
      registerConsumer(Schema::class.java, SchemaDescribed::class.java, applySchemaDescribedFn)
      val applySchemaRenamedFn = BiConsumer<Schema, SchemaRenamed>({ obj, event -> obj.applyRenamed(event) })
      registerConsumer(Schema::class.java, SchemaRenamed::class.java, applySchemaRenamedFn)
    }

    fun defineWith(organizationId: OrganizationId, contextId: ContextId, category: Category, name: String, description: String): Schema {
      assert(organizationId.isDefined())
      assert(name.isNotEmpty())
      assert(description.isNotEmpty())
      val schema = Schema()
      schema.apply(SchemaDefined(organizationId, contextId, SchemaId.unique(), category, name, description))
      return schema
    }
  }
}
