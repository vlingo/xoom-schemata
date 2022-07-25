// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.resource.data;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vlingo.xoom.schemata.model.OrganizationState;

public class OrganizationData {
  public final String organizationId;
  public final String name;
  public final String description;

  public static OrganizationData from(final OrganizationState state) {
    return new OrganizationData(state.organizationId.value, state.name, state.description);
  }

  public static List<OrganizationData> from(final List<OrganizationState> states) {
    final List<OrganizationData> data = new ArrayList<>(states.size());

    for (final OrganizationState state : states) {
      data.add(OrganizationData.from(state));
    }

    return data;
  }

  @JsonCreator
  public static OrganizationData from(@JsonProperty("organizationId") final String organizationId,
                                      @JsonProperty("name") final String name,
                                      @JsonProperty("description") final String description) {
    return new OrganizationData(organizationId, name, description);
  }

  public static OrganizationData just(final String name, final String description) {
    return new OrganizationData("", name, description);
  }

  @Override
  public String toString() {
    return "OrganizationData [organizationId=" + organizationId + " name=" + name + " description=" + description + "]";
  }

  private OrganizationData(final String organizationId, final String name, final String description) {
    this.organizationId = organizationId;
    this.name = name;
    this.description = description;
  }
}
