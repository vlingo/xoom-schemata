// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;

public interface Schema {
    static Id.SchemaId uniqueId() {
        return Id.SchemaId.unique();
    }

    static Schema newWith(final Stage stage, final String name, final String description) {
        return stage.actorFor(Definition.has(SchemaEntity.class, Definition.parameters(Organization.uniqueId(), Unit.uniqueId(), Context.uniqueId(),
                uniqueId(), name, description)), Schema.class);
    }

    void describeAs(final String description);

    void recategorizedAs(final Category category);

    void renameTo(final String name);
}
