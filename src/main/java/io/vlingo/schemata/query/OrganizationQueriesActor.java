// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.vlingo.common.Completes;
import io.vlingo.lattice.query.StateObjectQueryActor;
import io.vlingo.schemata.model.OrganizationState;
import io.vlingo.schemata.resource.data.OrganizationData;
import io.vlingo.symbio.store.object.MapQueryExpression;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.QueryExpression;

public class OrganizationQueriesActor extends StateObjectQueryActor implements OrganizationQueries {
  private final Map<String,String> parameters;
  private final QueryExpression queryAll;

  public OrganizationQueriesActor(final ObjectStore objectStore) {
    super(objectStore);

    this.parameters = new HashMap<>(1);

    this.queryAll = QueryExpression.using(OrganizationState.class, "SELECT * FROM TBL_ORGANIZATIONS");
  }

  @Override
  public Completes<List<OrganizationData>> organizations() {
    return queryAll(OrganizationState.class, queryAll, (List<OrganizationState> states) -> OrganizationData.from(states));
  }

  @Override
  public Completes<OrganizationData> organization(final String organizationId) {
    parameters.clear();
    parameters.put("organizationId", organizationId);

    final QueryExpression query =
            MapQueryExpression.using(
                    OrganizationState.class,
                    "SELECT * FROM TBL_ORGANIZATIONS " +
                    "WHERE organizationId = :organizationId",
                    parameters);

    return queryObject(OrganizationState.class, query, (OrganizationState state) -> OrganizationData.from(state));
  }
}
