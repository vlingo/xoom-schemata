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
import io.vlingo.schemata.resource.data.SchemaVersionData;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiOnDatabase;

public class SchemaVersionQueriesActor extends Actor implements SchemaVersionQueries {
  private final JdbiOnDatabase jdbi;

  public SchemaVersionQueriesActor(final JdbiOnDatabase jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Completes<List<SchemaVersionData>> schemaVersions(final String organizationId, final String unitId, final String contextId, final String schemaId) {
    return null;
  }

  @Override
  public Completes<SchemaVersionData> schemaVersion(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId) {
    return null;
  }
}
