// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import io.vlingo.common.Completes;
import io.vlingo.lattice.query.StateStoreQueryActor;
import io.vlingo.schemata.model.Path;
import io.vlingo.schemata.query.view.CodeView;
import io.vlingo.symbio.store.state.StateStore;

public class CodeQueriesActor extends StateStoreQueryActor implements CodeQueries {
  public CodeQueriesActor(StateStore stateStore) {
    super(stateStore);
  }

  @Override
  public Completes<CodeView> codeFor(Path path) {
    String reference = path.toReference();
    return queryStateFor(reference, CodeView.class);
  }
}
