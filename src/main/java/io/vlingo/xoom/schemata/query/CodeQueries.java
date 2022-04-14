// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.model.Path;
import io.vlingo.xoom.schemata.query.view.CodeView;

public interface CodeQueries {
  Completes<CodeView> codeFor(final Path path);
//  Completes<Outcome<SchemataBusinessException, SchemaVersionData>> schemaVersionFor(final AuthorizationData authorization, final PathData path);
//  Completes<Outcome<SchemataBusinessException, SchemaVersionData>> schemaVersionFor(final AuthorizationData authorization, final PathData path, final QueryResultsCollector collector);
//  Completes<Outcome<SchemataBusinessException, SchemaVersionData>> schemaVersionFor(final String fullQualifiedTypeName);
}
