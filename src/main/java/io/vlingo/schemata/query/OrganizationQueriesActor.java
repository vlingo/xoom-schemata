// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
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
import io.vlingo.lattice.query.StateStoreQueryActor;
import io.vlingo.schemata.query.view.OrganizationView;
import io.vlingo.schemata.query.view.OrganizationsView;
import io.vlingo.symbio.store.state.StateStore;

public class OrganizationQueriesActor extends StateStoreQueryActor implements OrganizationQueries {
  public OrganizationQueriesActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  public Completes<OrganizationsView> organizations() {
    return queryObjectStateFor(OrganizationsView.Id, OrganizationsView.class).andFinally();
  }

  @Override
  public Completes<OrganizationView> organization(final String organizationId) {
    return queryObjectStateFor(organizationId, OrganizationView.class).andFinally();
  }
}
