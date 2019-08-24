// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import io.vlingo.actors.Stage;
import io.vlingo.symbio.store.object.ObjectStore;
import io.vlingo.symbio.store.object.jdbc.jdbi.JdbiOnDatabase;

public class Queries {
  public static OrganizationQueries forOrganizations(final Stage stage, final ObjectStore objectStore) {
    return stage.actorFor(OrganizationQueries.class, OrganizationQueriesActor.class, objectStore);
  }

  public static UnitQueries forUnits(final Stage stage, final JdbiOnDatabase jdbi) {
    return stage.actorFor(UnitQueries.class, UnitQueriesActor.class, jdbi);
  }

  public static ContextQueries forContexts(final Stage stage, final JdbiOnDatabase jdbi) {
    return stage.actorFor(ContextQueries.class, ContextQueriesActor.class, jdbi);
  }

  public static SchemaQueries forSchemas(final Stage stage, final JdbiOnDatabase jdbi) {
    return stage.actorFor(SchemaQueries.class, SchemaQueriesActor.class, jdbi);
  }

  public static SchemaVersionQueries forSchemaVersions(final Stage stage, final JdbiOnDatabase jdbi) {
    return stage.actorFor(SchemaVersionQueries.class, SchemaVersionQueriesActor.class, jdbi);
  }
}
