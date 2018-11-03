// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.schemata.model.Id.OrganizationId;

public interface Organization {
  static OrganizationId uniqueId() {
    return OrganizationId.unique();
  }

  static Organization with(final Stage stage, final String name, final String description) {
    return with(stage, uniqueId(), name, description);
  }

  static Organization with(final Stage stage, final OrganizationId organizationId, final String name, final String description) {
    return stage.actorFor(Definition.has(OrganizationEntity.class, Definition.parameters(organizationId, name, description)), Organization.class);
  }

  void describeAs(final String description);

  void renameTo(final String name);
}
