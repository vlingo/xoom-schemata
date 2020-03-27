// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.query;

import java.util.List;

import io.vlingo.common.Completes;
import io.vlingo.schemata.query.view.UnitView;
import io.vlingo.schemata.query.view.UnitsView;

public interface UnitQueries {
  Completes<UnitsView> units(final String organizationId);
  Completes<UnitView> unit(final String organizationId, final String unitId);
}
