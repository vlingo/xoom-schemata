// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.resource.data;

import java.util.Collections;
import java.util.List;

import io.vlingo.schemata.model.OrganizationState;

public class OrganizationData {
  public final String id;
  public final String name;
  public final String description;
  public final List<Unit> units;

  public static OrganizationData from(final OrganizationState state) {
    return new OrganizationData(state.organizationId.value, state.name, state.description, Collections.emptyList());
  }

  public static OrganizationData from(final String id, final String name, final String description, final List<Unit> units) {
    return new OrganizationData(id, name, description, units);
  }

  private OrganizationData(final String id, final String name, final String description, final List<Unit> units) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.units = units;
  }
}
