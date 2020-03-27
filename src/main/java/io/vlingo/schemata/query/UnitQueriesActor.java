// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import io.vlingo.common.Completes;
import io.vlingo.lattice.query.StateStoreQueryActor;
import io.vlingo.schemata.query.view.UnitView;
import io.vlingo.schemata.query.view.UnitsView;
import io.vlingo.symbio.store.state.StateStore;

public class UnitQueriesActor extends StateStoreQueryActor implements UnitQueries {
  public UnitQueriesActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  public Completes<UnitsView> units(final String organizationId) {
    return queryObjectStateFor(organizationId, UnitsView.class).andFinally();
  }

  @Override
  public Completes<UnitView> unit(final String organizationId, final String unitId) {
    final String id = dataIdFrom(":", organizationId, unitId);
    return queryObjectStateFor(id, UnitView.class).andFinally();
  }
}
