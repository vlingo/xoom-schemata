// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model

import java.util.UUID
import io.vlingo.lattice.model.identity.IdentityGeneratorType

abstract class Id internal constructor(value: String) {
  val value: String

  fun isDefined(): Boolean = value.isNotEmpty()
  fun isUndefined(): Boolean = value.isEmpty()

  init {
    this.value = value
  }

  class OrganizationId private constructor(value: String) : Id(value) {
    companion object {
      fun existing(id: String): OrganizationId = OrganizationId(UUID.fromString(id).toString())
      fun undefined(): OrganizationId = OrganizationId("")
      fun unique(): OrganizationId = OrganizationId(IdentityGeneratorType.Random.generate().toString())
    }
  }

  class ContextId private constructor(value: String) : Id(value) {
    companion object {
      fun existing(id: String): ContextId = ContextId(UUID.fromString(id).toString())
      fun undefined(): ContextId = ContextId("")
      fun unique(): ContextId = ContextId(IdentityGeneratorType.Random.generate().toString())
    }
  }

  class SchemaId private constructor(value: String) : Id(value) {
    companion object {
      fun existing(id: String): SchemaId = SchemaId(UUID.fromString(id).toString())
      fun undefined(): SchemaId = SchemaId("")
      fun unique(): SchemaId = SchemaId(IdentityGeneratorType.Random.generate().toString())
    }
  }

  class SchemaVersionId private constructor(value: String) : Id(value) {
    companion object {
      fun existing(id: String): SchemaVersionId = SchemaVersionId(UUID.fromString(id).toString())
      fun undefined(): SchemaVersionId = SchemaVersionId("")
      fun unique(): SchemaVersionId = SchemaVersionId(IdentityGeneratorType.Random.generate().toString())
    }
  }
}
