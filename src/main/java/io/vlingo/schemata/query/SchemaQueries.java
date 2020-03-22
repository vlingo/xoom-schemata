// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import java.util.List;

import io.vlingo.common.Completes;
import io.vlingo.common.Outcome;
import io.vlingo.schemata.errors.SchemataBusinessException;
import io.vlingo.schemata.resource.data.SchemaData;

public interface SchemaQueries {
  Completes<List<SchemaData>> schemas(final String organizationId, final String unitId, final String contextId);
  Completes<Outcome<SchemataBusinessException,SchemaData>> schema(final String organizationId, final String unitId, final String contextId, final String schemaId);
  Completes<Outcome<SchemataBusinessException,SchemaData>> schemaNamed(final String organizationId, final String unitId, final String contextId, final String name);
  Completes<Outcome<SchemataBusinessException,SchemaData>> schemaVersionByNames(final String organization, final String unit, final String context, final String schema);
}
