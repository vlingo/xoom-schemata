// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model

import io.vlingo.lattice.model.sourcing.EventSourced
import io.vlingo.schemata.model.Events.OrganizationDefined
import io.vlingo.schemata.model.Events.OrganizationDescribed
import io.vlingo.schemata.model.Events.OrganizationRenamed
import io.vlingo.schemata.model.Id.OrganizationId

import java.util.function.BiConsumer

class Organization private constructor() : EventSourced() {
  private var state: State

  init {
    this.state = State()
  }

  fun describeAs(description: String) {
    apply(OrganizationDescribed(state.id, description))
  }

  fun renameTo(name: String) {
    apply(OrganizationRenamed(state.id, name))
  }

  internal fun applyDefined(event: OrganizationDefined) {
    state = State(OrganizationId.existing(event.organizationId), OrganizationId.existing(event.parentId), event.name, event.description)
  }

  internal fun applyDescribed(event: OrganizationDescribed) {
    state = state.withDescription(event.description)
  }

  internal fun applyRenamed(event: OrganizationRenamed) {
    state = state.withName(event.name)
  }

  inner class State @JvmOverloads internal constructor(val id: OrganizationId = OrganizationId.undefined(), val parentId: OrganizationId = OrganizationId.undefined(), val name: String = "", val description: String = "") {
    internal fun withDescription(description: String): State {
      return State(id, parentId, name, description)
    }

    internal fun withName(name: String): State {
      return State(id, parentId, name, description)
    }
  }

  companion object {
    init {
      val applyOrganizationDefinedFn = BiConsumer<Organization, OrganizationDefined>({ obj, event -> obj.applyDefined(event) })
      registerConsumer(Organization::class.java, OrganizationDefined::class.java, applyOrganizationDefinedFn)
      val applyOrganizationDescribedFn = BiConsumer<Organization, OrganizationDescribed>({ obj, event -> obj.applyDescribed(event) })
      registerConsumer(Organization::class.java, OrganizationDescribed::class.java, applyOrganizationDescribedFn)
      val applyOrganizationRenamedFn = BiConsumer<Organization, OrganizationRenamed>({ obj, event -> obj.applyRenamed(event) })
      registerConsumer(Organization::class.java, OrganizationRenamed::class.java, applyOrganizationRenamedFn)
    }

    fun defineWith(name: String, description: String): Organization {
      return defineWith(OrganizationId.undefined(), name, description)
    }

    fun defineWith(parentId: OrganizationId, name: String, description: String): Organization {
      assert(parentId.isDefined())
      assert(name.isNotEmpty())
      assert(description.isNotEmpty())
      val organization = Organization()
      organization.apply(OrganizationDefined(OrganizationId.unique(), parentId, name, description))
      return organization
    }
  }
}