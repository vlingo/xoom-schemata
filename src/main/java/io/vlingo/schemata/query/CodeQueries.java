// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.schemata.errors.SchemataBusinessException;
import io.vlingo.schemata.resource.data.AuthorizationData;
import io.vlingo.schemata.resource.data.PathData;
import io.vlingo.schemata.resource.data.SchemaData;
import io.vlingo.schemata.resource.data.SchemaVersionData;

public interface CodeQueries {
  Completes<Outcome<SchemataBusinessException, SchemaVersionData>> schemaVersionFor(final AuthorizationData authorization, final PathData path);
  Completes<Outcome<SchemataBusinessException, SchemaVersionData>> schemaVersionFor(final AuthorizationData authorization, final PathData path, final QueryResultsCollector collector);
  Completes<Outcome<SchemataBusinessException, SchemaVersionData>> schemaVersionFor(final String fullQualifiedTypeName);
}
