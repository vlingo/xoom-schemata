// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.query.view.NamedSchemaView;
import io.vlingo.xoom.schemata.query.view.SchemaView;
import io.vlingo.xoom.schemata.query.view.SchemasView;

public interface SchemaQueries {
  Completes<SchemasView> schemas(final String organizationId, final String unitId, final String contextId);
  Completes<SchemaView> schema(final String organizationId, final String unitId, final String contextId, final String schemaId);
  Completes<NamedSchemaView> schemaByNames(final String organization, final String unit, final String context, final String schema);
  Completes<NamedSchemaView> schemaByNamesWithRetries(final String organization, final String unit, final String context, final String schema, int retryInterval, int retryTotal);
}
