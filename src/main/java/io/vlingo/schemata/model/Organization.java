// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.model;

import io.vlingo.actors.Stage;
import io.vlingo.common.Completes;
import io.vlingo.schemata.model.Id.OrganizationId;

public interface Organization {
  static OrganizationId uniqueId() {
    return OrganizationId.unique();
  }

  static Organization with(
          final Stage stage,
          final String name,
          final String description) {
    return with(stage, uniqueId(), name, description);
  }

  static Organization with(
          final Stage stage,
          final OrganizationId organizationId,
          final String name,
          final String description) {
    final Organization organization = stage.actorFor(Organization.class, OrganizationEntity.class);
    organization.defineWith(organizationId, name, description);
    return organization;
  }

  Completes<OrganizationState> defineWith(final OrganizationId organizationId, final String name, final String description);

  Completes<OrganizationState> describeAs(final String description);

  Completes<OrganizationState> renameTo(final String name);
}
