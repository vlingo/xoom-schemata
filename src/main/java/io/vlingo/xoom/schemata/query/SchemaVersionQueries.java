// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.query.view.SchemaVersionView;
import io.vlingo.xoom.schemata.query.view.SchemaVersionsView;

public interface SchemaVersionQueries {
  String GreatestVersion = "99999.99999.99999";

  Completes<SchemaVersionsView> schemaVersionsByIds(final String organizationId, final String unitId, final String contextId, final String schemaId);
  Completes<SchemaVersionView> schemaVersion(final String organizationId, final String unitId, final String contextId, final String schemaId, final String schemaVersionId);
}
