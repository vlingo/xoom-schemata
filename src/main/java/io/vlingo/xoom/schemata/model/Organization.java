// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.model;

import io.vlingo.xoom.actors.ActorInstantiator;
import io.vlingo.xoom.actors.Address;
import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.Id.OrganizationId;

public interface Organization {
  static String nameFrom(final OrganizationId organizationId) {
    return "O:"+organizationId.value;
  }

  static OrganizationId uniqueId() {
    return OrganizationId.unique();
  }

  static Completes<OrganizationState> with(
          final Stage stage,
          final String name,
          final String description) {
    return with(stage, uniqueId(), name, description);
  }

  static Completes<OrganizationState> with(
          final Stage stage,
          final OrganizationId organizationId,
          final String name,
          final String description) {
    final String actorName = nameFrom(organizationId);
    final Address address = stage.addressFactory().from(organizationId.value, actorName);
    final Definition definition = Definition.has(OrganizationEntity.class, new OrganizationInstantiator(organizationId), actorName);
    final Organization organization = stage.actorFor(Organization.class, definition, address);
    return organization.defineWith(name, description);
  }

  Completes<OrganizationState> defineWith(final String name, final String description);

  Completes<OrganizationState> describeAs(final String description);

  Completes<OrganizationState> redefineWith(final String name, final String description);

  Completes<OrganizationState> renameTo(final String name);

  static class OrganizationInstantiator implements ActorInstantiator<OrganizationEntity> {
    private static final long serialVersionUID = -8795622651394034414L;

    private final OrganizationId organizationId;

    public OrganizationInstantiator(final OrganizationId organizationId) {
      this.organizationId = organizationId;
    }

    @Override
    public OrganizationEntity instantiate() {
      return new OrganizationEntity(organizationId);
    }

    @Override
    public Class<OrganizationEntity> type() {
      return OrganizationEntity.class;
    }
  }
}
