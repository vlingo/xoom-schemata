// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import java.util.List;

import io.vlingo.common.Completes;
import io.vlingo.schemata.resource.data.ContextData;

public interface ContextQueries {
  Completes<List<ContextData>> contexts(final String organizationId, final String unitId);
  Completes<ContextData> context(final String organizationId, final String unitId, final String contextId);
}
