// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.infra.persistence;

import io.vlingo.xoom.lattice.model.projection.Projectable;
import io.vlingo.xoom.lattice.model.projection.ProjectionControl;
import io.vlingo.xoom.schemata.query.view.View;

public interface SchemaVersionLookUpInterest {
  void informFound(final Projectable projectable, final ProjectionControl projectionControl, final Class<? extends View> view, final String value);
  void informFound(final Projectable projectable, final ProjectionControl projectionControl, final Class<? extends View> view, final String value1, final String value2);
  void informNotFound(final Projectable projectable, final ProjectionControl projectionControl, final Class<? extends View> view);
}
