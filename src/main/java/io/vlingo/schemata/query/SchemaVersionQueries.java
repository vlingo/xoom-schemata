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
import io.vlingo.schemata.query.view.SchemaVersionView;
import io.vlingo.schemata.query.view.SchemaVersionsView;
import io.vlingo.schemata.resource.data.SchemaVersionData;

public interface SchemaVersionQueries {
  public final static String GreatestVersion = "99999.99999.99999";

  Completes<SchemaVersionsView> schemaVersionsByIds(final String organizationId, final String unitId, final String contextId, final String schemaId);
  Completes<SchemaVersionsView> schemaVersionsByNames(final String organization, final String unit, final String context, final String schema);
  Completes<SchemaVersionView> schemaVersion(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId);
  Completes<SchemaVersionView> schemaVersion(final String fullyQualifiedTypeName);
  Completes<SchemaVersionView> schemaVersionOf(final String organization, final String unit, final String context, final String schema, final String schemaVersion);
  Completes<SchemaVersionView> schemaVersionOfVersion(final String organizationId, final String unitId, final String contextId, final String schemaId, final String version);
}
