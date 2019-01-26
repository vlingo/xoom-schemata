// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.Stage;
import io.vlingo.schemata.model.Id.OrganizationId;
import io.vlingo.schemata.model.Id.UnitId;

public interface Unit {
    static UnitId uniqueId(final OrganizationId organizationId) {
        return UnitId.uniqueFor(organizationId);
    }

    static Unit with(final Stage stage, final OrganizationId organizationId, final String name, final String description) {
        return with(stage, uniqueId(organizationId), name, description);
    }

    static Unit with(final Stage stage, final UnitId unitId, final String name, final String description) {
        return stage.actorFor(Unit.class, UnitEntity.class, unitId, name, description);
    }

    void defineWith(final String name, final String description);

    void describeAs(final String description);

    void renameTo(final String name);
}
