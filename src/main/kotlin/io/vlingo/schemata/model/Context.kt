// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model

import java.util.function.BiConsumer
import io.vlingo.lattice.model.sourcing.EventSourced
import io.vlingo.schemata.model.Events.ContextDefined
import io.vlingo.schemata.model.Events.ContextDescribed
import io.vlingo.schemata.model.Events.ContextNamespaceChanged
import io.vlingo.schemata.model.Id.ContextId
import io.vlingo.schemata.model.Id.OrganizationId

class Context private constructor() : EventSourced() {
  private var state: State

  init {
    this.state = State()
  }

  fun changeNamespaceTo(namespace: String) {
    assert(namespace.isNotEmpty() && namespace.isNotBlank())
    apply(ContextNamespaceChanged(state.organizationId, state.contextId, namespace))
  }

  fun describeAs(description: String) {
    assert(description.isNotEmpty() && description.isNotBlank())
    apply(ContextDescribed(state.organizationId, state.contextId, description))
  }

  internal fun applyDefined(event: ContextDefined) {
    state = State(OrganizationId.existing(event.organizationId), ContextId.existing(event.contextId), event.namespace, event.description)
  }

  internal fun applyDescribed(event: ContextDescribed) {
    state = state.withDescription(event.description)
  }

  internal fun applyNamespaceChanged(event: ContextNamespaceChanged) {
    state = state.withNamespace(event.namespace)
  }

  inner class State @JvmOverloads internal constructor(
      organizationId: OrganizationId = OrganizationId.undefined(),
      contextId: ContextId = ContextId.undefined(),
      namespace: String = "",
      description: String = "") {

    val contextId: ContextId
    val description: String
    val namespace: String
    val organizationId: OrganizationId

    init {
      this.organizationId = organizationId
      this.contextId = contextId
      this.namespace = namespace
      this.description = description
    }

    internal fun withDescription(description: String): State {
      return State(organizationId, contextId, namespace, description)
    }

    internal fun withNamespace(namespace: String): State {
      return State(organizationId, contextId, namespace, description)
    }
  }

  companion object {
    init {
      val applyContextDefinedFn = BiConsumer<Context, ContextDefined>({ obj, event -> obj.applyDefined(event) })
      registerConsumer(Context::class.java, ContextDefined::class.java, applyContextDefinedFn)
      val applyContextDescribedFn = BiConsumer<Context, ContextDescribed>({ obj, event -> obj.applyDescribed(event) })
      registerConsumer(Context::class.java, ContextDescribed::class.java, applyContextDescribedFn)
      val applyContextNamespaceChangedFn = BiConsumer<Context, ContextNamespaceChanged>({ obj, event -> obj.applyNamespaceChanged(event) })
      registerConsumer(Context::class.java, ContextNamespaceChanged::class.java, applyContextNamespaceChangedFn)
    }

    fun defineWith(organizationId: OrganizationId, namespace: String, description: String): Context {
      assert(organizationId.isDefined())
       assert(namespace.isNotEmpty() && namespace.isNotBlank())
       assert(description.isNotEmpty() && description.isNotBlank())
      val context = Context()
      context.apply(ContextDefined(organizationId, ContextId.unique(), namespace, description))
      return context
    }
  }
}