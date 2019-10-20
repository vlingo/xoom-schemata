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
  private static final String ById =
          "SELECT * FROM TBL_ORGANIZATIONS " +
          "WHERE organizationId = :organizationId";

  private static final String ByName =
          "SELECT * FROM TBL_ORGANIZATIONS " +
          "WHERE name = :name";

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

    return queryOne(ById, parameters);
  }

  @Override
  public Completes<OrganizationData> organization(final String organizationId, final QueryResultsCollector collector) {
    final Completes<OrganizationData> data = organization(organizationId);
    collector.expectOrganization(data);
    return data;
  }

  @Override
  public Completes<OrganizationData> organizationNamed(final String name) {
    parameters.clear();
    parameters.put("name", name);

    return queryOne(ByName, parameters);
  }

  @Override
  public Completes<OrganizationData> organizationNamed(final String name, final QueryResultsCollector collector) {
    final Completes<OrganizationData> data = organizationNamed(name);
    collector.expectOrganization(data);
    return data;
  }

  private Completes<OrganizationData> queryOne(final String query, final Map<String,String> parameters) {
    final QueryExpression expression = MapQueryExpression.using(OrganizationState.class, query, parameters);

    return queryObject(OrganizationState.class, expression, (OrganizationState state) -> OrganizationData.from(state));
  }
}
