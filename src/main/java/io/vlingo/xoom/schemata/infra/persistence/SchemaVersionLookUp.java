// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.infra.persistence;

import java.util.function.Supplier;

import io.vlingo.xoom.actors.ActorInstantiator;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.projection.Projectable;
import io.vlingo.xoom.lattice.model.projection.ProjectionControl;
import io.vlingo.xoom.schemata.query.view.ContextView;
import io.vlingo.xoom.schemata.query.view.OrganizationView;
import io.vlingo.xoom.schemata.query.view.SchemaView;
import io.vlingo.xoom.schemata.query.view.UnitView;
import io.vlingo.xoom.schemata.query.view.View;

public interface SchemaVersionLookUp {
  void lookUpOrganizationName(final Class<OrganizationView> view, final Supplier<Completes<OrganizationView>> supplier);
  void lookUpUnitName(final Class<UnitView> view, final Supplier<Completes<UnitView>> supplier);
  void lookUpContextNamespace(final Class<ContextView> view, final Supplier<Completes<ContextView>> supplier);
  void lookUpSchemaName(Class<SchemaView> view, final Supplier<Completes<SchemaView>> supplier);
  void retryLookUps();
  void update(final Class<? extends View> view, final SchemaVersionQueryExecutor resolved);

  public static class SchemaVersionLookUpInstantiator implements ActorInstantiator<SchemaVersionLookUpActor> {
    private static final long serialVersionUID = 4108078659146662356L;

    private final String nextVersion;
    private final Projectable projectable;
    private final ProjectionControl projectionControl;
    private final SchemaVersionLookUpInterest queryInterest;

    public SchemaVersionLookUpInstantiator(
            final SchemaVersionLookUpInterest queryInterest,
            final String nextVersion,
            final Projectable projectable,
            final ProjectionControl projectionControl) {
      this.queryInterest = queryInterest;
      this.nextVersion = nextVersion;
      this.projectable = projectable;
      this.projectionControl = projectionControl;
    }

    @Override
    public SchemaVersionLookUpActor instantiate() {
      return new SchemaVersionLookUpActor(queryInterest, nextVersion, projectable, projectionControl);
    }
  }
}
