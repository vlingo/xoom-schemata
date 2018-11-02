// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;

public interface Unit {
    static Id.UnitId uniqueId() {
        return Id.UnitId.unique();
    }

    static Unit newWith(final Stage stage, final String name, final String description) {
        return stage.actorFor(Definition.has(UnitEntity.class, Definition.parameters(Organization.uniqueId(), Unit.uniqueId(), name, description)), Unit.class);
    }

    void describeAs(final String description);

    void renameTo(final String name);
}
