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
import io.vlingo.schemata.errors.EntityNotFoundException;
import io.vlingo.schemata.resource.data.UnitData;

public interface UnitQueries {
  Completes<List<UnitData>> units(final String organizationId);
  Completes<Outcome<EntityNotFoundException,UnitData>> unit(final String organizationId, final String unitId);
  Completes<Outcome<EntityNotFoundException,UnitData>> unitNamed(final String organizationId, final String name);
}
