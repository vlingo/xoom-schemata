// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.query.StateStoreQueryActor;
import io.vlingo.xoom.schemata.query.view.UnitView;
import io.vlingo.xoom.schemata.query.view.UnitsView;
import io.vlingo.xoom.symbio.store.state.StateStore;

public class UnitQueriesActor extends StateStoreQueryActor implements UnitQueries {
  public UnitQueriesActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  public Completes<UnitsView> units(final String organizationId) {
    return queryStateFor(organizationId, UnitsView.class);
  }

  @Override
  public Completes<UnitView> unit(final String organizationId, final String unitId) {
    final String id = dataIdFrom(":", organizationId, unitId);
    return queryStateFor(id, UnitView.class);
  }
}
