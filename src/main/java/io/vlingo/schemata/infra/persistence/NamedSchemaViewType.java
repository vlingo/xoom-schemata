// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import io.vlingo.lattice.model.DomainEvent;

public enum NamedSchemaViewType {
	SchemaDefined,
	SchemaVersionDefined,

	Unmatched;

	public static NamedSchemaViewType match(final DomainEvent event) {
		try {
			return NamedSchemaViewType.valueOf(event.typeName());
		} catch (Exception e) {
			return Unmatched;
		}
	}
}
