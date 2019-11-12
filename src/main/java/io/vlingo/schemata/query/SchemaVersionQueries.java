// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import java.util.List;

import io.vlingo.common.Completes;
import io.vlingo.schemata.resource.data.SchemaVersionData;

public interface SchemaVersionQueries {
  public final static String GreatestVersion = "99999.99999.99999";

  Completes<List<SchemaVersionData>> schemaVersions(final String organizationId, final String unitId, final String contextId, final String schemaId);
  Completes<SchemaVersionData> schemaVersion(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId);
  Completes<SchemaVersionData> schemaVersionOf(final String organization, final String unit, final String context, final String schema, final String schemaVersion);
  Completes<SchemaVersionData> schemaVersionOfVersion(final String organizationId, final String unitId, final String contextId, final String schemaId, final String version);
}
