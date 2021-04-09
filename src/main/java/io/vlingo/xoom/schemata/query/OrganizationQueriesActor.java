// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.query.StateStoreQueryActor;
import io.vlingo.xoom.schemata.query.view.OrganizationView;
import io.vlingo.xoom.schemata.query.view.OrganizationsView;
import io.vlingo.xoom.symbio.store.state.StateStore;

public class OrganizationQueriesActor extends StateStoreQueryActor implements OrganizationQueries {
  public OrganizationQueriesActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  public Completes<OrganizationsView> organizations() {
    return queryStateFor(OrganizationsView.Id, OrganizationsView.class);
  }

  @Override
  public Completes<OrganizationView> organization(final String organizationId) {
    return queryStateFor(organizationId, OrganizationView.class);
  }
}
