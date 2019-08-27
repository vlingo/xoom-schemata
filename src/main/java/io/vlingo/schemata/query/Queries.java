// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import io.vlingo.actors.Stage;
import io.vlingo.symbio.store.object.ObjectStore;

public class Queries {
  public static OrganizationQueries forOrganizations(final Stage stage, final ObjectStore objectStore) {
    return stage.actorFor(OrganizationQueries.class, OrganizationQueriesActor.class, objectStore);
  }

  public static UnitQueries forUnits(final Stage stage, final ObjectStore objectStore) {
    return stage.actorFor(UnitQueries.class, UnitQueriesActor.class, objectStore);
  }

  public static ContextQueries forContexts(final Stage stage, final ObjectStore objectStore) {
    return stage.actorFor(ContextQueries.class, ContextQueriesActor.class, objectStore);
  }

  public static SchemaQueries forSchemas(final Stage stage, final ObjectStore objectStore) {
    return stage.actorFor(SchemaQueries.class, SchemaQueriesActor.class, objectStore);
  }

  public static SchemaVersionQueries forSchemaVersions(final Stage stage, final ObjectStore objectStore) {
    return stage.actorFor(SchemaVersionQueries.class, SchemaVersionQueriesActor.class, objectStore);
  }
}
