// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.lattice.model.DomainEvent;

public enum SchemaVersionViewType {
    SchemaDefined,
    SchemaVersionDefined,
    SchemaVersionDescribed,
    SchemaVersionAssigned,
    SchemaVersionSpecified,
    SchemaVersionPublished,
    SchemaVersionDeprecated,
    SchemaVersionRemoved,

    Unmatched;

    public static SchemaVersionViewType match(final DomainEvent event) {
        try {
            return SchemaVersionViewType.valueOf(event.typeName());
        } catch (Exception e) {
            return SchemaVersionViewType.Unmatched;
        }
    }
}
