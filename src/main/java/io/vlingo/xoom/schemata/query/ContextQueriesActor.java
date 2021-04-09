// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.query.StateStoreQueryActor;
import io.vlingo.xoom.schemata.query.view.ContextView;
import io.vlingo.xoom.schemata.query.view.ContextsView;
import io.vlingo.xoom.symbio.store.state.StateStore;

public class ContextQueriesActor extends StateStoreQueryActor implements ContextQueries {
    public ContextQueriesActor(StateStore stateStore) {
        super(stateStore);
    }

    @Override
    public Completes<ContextsView> contexts(final String organizationId, String unitId) {
        return queryStateFor(unitId, ContextsView.class);
    }

    @Override
    public Completes<ContextView> context(final String organizationId, String unitId, String contextId) {
        final String id = dataIdFrom(":", unitId, contextId);
        return queryStateFor(id, ContextView.class);
    }
}
