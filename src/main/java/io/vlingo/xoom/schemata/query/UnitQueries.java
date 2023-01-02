// Copyright © 2012-2023 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.query;

import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.schemata.query.view.UnitView;
import io.vlingo.xoom.schemata.query.view.UnitsView;

public interface UnitQueries {
  Completes<UnitsView> units(final String organizationId);
  Completes<UnitView> unit(final String organizationId, final String unitId);
}
