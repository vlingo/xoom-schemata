// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import io.vlingo.common.Completes;
import io.vlingo.schemata.resource.data.AuthorizationData;
import io.vlingo.schemata.resource.data.PathData;
import io.vlingo.schemata.resource.data.SchemaVersionData;

public interface CodeQueries {
  Completes<SchemaVersionData> schemaVersionFor(final AuthorizationData authorization, final PathData path);
}
