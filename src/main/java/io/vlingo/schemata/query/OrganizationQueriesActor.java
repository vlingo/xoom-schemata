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
import io.vlingo.schemata.model.OrganizationState;
import io.vlingo.schemata.resource.data.OrganizationData;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiOnDatabase;

public class OrganizationQueriesActor extends QueryActor<OrganizationState> implements OrganizationQueries {

  public OrganizationQueriesActor(final ObjectStore objectStore) {
    super(objectStore);
  }

  @Override
  public Completes<List<OrganizationData>> organizations() {
    return completes();
  }

  @Override
  public Completes<OrganizationData> organization(final String organizationId) {
    return null;
  }
}