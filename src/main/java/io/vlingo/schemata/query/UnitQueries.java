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
import io.vlingo.schemata.resource.data.UnitData;

public interface UnitQueries {
  Completes<List<UnitData>> units(final String organizationId);
  Completes<Outcome<SchemataBusinessException,UnitData>> unit(final String organizationId, final String unitId);
  Completes<Outcome<SchemataBusinessException,UnitData>> unitNamed(final String organizationId, final String name);
}
