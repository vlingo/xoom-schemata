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
  public final String organizationId;
  public final String name;
  public final String description;
  public final List<UnitData> units;

  public static OrganizationData from(final OrganizationState state) {
    return new OrganizationData(state.organizationId.value, state.name, state.description, Collections.emptyList());
  }

  public static OrganizationData from(final String organizationId, final String name, final String description) {
    return new OrganizationData(organizationId, name, description, Collections.emptyList());
  }

  public static OrganizationData from(final String organizationId, final String name, final String description, final List<UnitData> units) {
    return new OrganizationData(organizationId, name, description, units);
  }

  @Override
  public String toString() {
    return "OrganizationData [id=" + organizationId + " name=" + name + " description=" + description + " units=" + units + "]";
  }

  private OrganizationData(final String organizationId, final String name, final String description, final List<UnitData> units) {
    this.organizationId = organizationId;
    this.name = name;
    this.description = description;
    this.units = units;
  }
}
