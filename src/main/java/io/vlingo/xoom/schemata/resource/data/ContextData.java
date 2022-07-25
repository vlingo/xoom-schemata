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
import io.vlingo.xoom.schemata.model.ContextState;

public class ContextData {
  public final String organizationId;
  public final String unitId;
  public final String contextId;
  public final String namespace;
  public final String description;

  public static ContextData from(final ContextState state) {
    return new ContextData(state.contextId.organizationId().value, state.contextId.unitId.value, state.contextId.value, state.namespace, state.description);
  }

  public static List<ContextData> from(final List<ContextState> states) {
    final List<ContextData> data = new ArrayList<>(states.size());

    for (final ContextState state : states) {
      data.add(ContextData.from(state));
    }

    return data;
  }

  @JsonCreator
  public static ContextData from(@JsonProperty("organizationId") final String organizationId,
                                 @JsonProperty("unitId") final String unitId,
                                 @JsonProperty("contextId") final String contextId,
                                 @JsonProperty("namespace") final String namespace,
                                 @JsonProperty("description") final String description) {
    return new ContextData(organizationId, unitId, contextId, namespace, description);
  }

  public static ContextData identity(final String organizationId, final String unitId, final String contextId) {
    return new ContextData(organizationId, unitId, contextId, null, null);
  }

  public static ContextData just(final String namespace, final String description) {
    return new ContextData("", "", "", namespace, description);
  }

  @Override
  public String toString() {
    return "ContextData [organizationId=" + organizationId + ", unitId=" + unitId + ", contextId=" + contextId
            + ", namespace=" + namespace + ", description=" + description + "]";
  }

  private ContextData(final String organizationId, final String unitId, final String contextId, String namespace, final String description) {
    this.organizationId = organizationId;
    this.unitId = unitId;
    this.contextId = contextId;
    this.namespace = namespace;
    this.description = description;
  }
}
