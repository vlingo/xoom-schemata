// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import io.vlingo.common.Completes;
import io.vlingo.schemata.query.view.NamedSchemaView;
import io.vlingo.schemata.query.view.SchemaView;
import io.vlingo.schemata.query.view.SchemasView;

public interface SchemaQueries {
  Completes<SchemasView> schemas(final String organizationId, final String unitId, final String contextId);
  Completes<SchemaView> schema(final String organizationId, final String unitId, final String contextId, final String schemaId);
  Completes<NamedSchemaView> schemaByNames(final String organization, final String unit, final String context, final String schema);
}
