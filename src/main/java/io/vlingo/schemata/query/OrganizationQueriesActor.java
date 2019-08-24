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
import io.vlingo.schemata.resource.data.OrganizationData;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiOnDatabase;

public class OrganizationQueriesActor extends Actor implements OrganizationQueries {
  private final JdbiOnDatabase jdbi;

  public OrganizationQueriesActor(final JdbiOnDatabase jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Completes<List<OrganizationData>> organizations() {
    return null;
  }

  @Override
  public Completes<OrganizationData> organization(final String organizationId) {
    return null;
  }
}
