// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import java.util.List;

import io.vlingo.actors.Actor;
import io.vlingo.common.Completes;
import io.vlingo.schemata.resource.data.UnitData;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiOnDatabase;

public class ContextQueriesActor extends Actor implements UnitQueries {
  private final JdbiOnDatabase jdbi;

  public ContextQueriesActor(final JdbiOnDatabase jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Completes<List<UnitData>> units(final String organizationId) {
    return null;
  }

  @Override
  public Completes<UnitData> unit(final String organizationId, final String unitId) {
    return null;
  }
}
