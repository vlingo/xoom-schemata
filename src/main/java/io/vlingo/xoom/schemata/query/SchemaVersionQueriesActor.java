// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.query.StateStoreQueryActor;
import io.vlingo.xoom.schemata.query.view.SchemaVersionView;
import io.vlingo.xoom.schemata.query.view.SchemaVersionsView;
import io.vlingo.xoom.symbio.store.state.StateStore;

public class SchemaVersionQueriesActor extends StateStoreQueryActor implements SchemaVersionQueries {
  public SchemaVersionQueriesActor(final StateStore stateStore) {
    super(stateStore);
  }

  @Override
  public Completes<SchemaVersionsView> schemaVersionsByIds(final String organizationId, final String unitId, final String contextId, final String schemaId) {
    return queryStateFor(schemaId, SchemaVersionsView.class);
  }

  @Override
  public Completes<SchemaVersionView> schemaVersion(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId) {
    final String id = dataIdFrom(":", schemaId, schemaVersionId);
    return queryStateFor(id, SchemaVersionView.class);
  }
}
