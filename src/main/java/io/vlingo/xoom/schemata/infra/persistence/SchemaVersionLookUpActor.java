// Copyright © 2012-2022 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.infra.persistence;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import io.vlingo.xoom.actors.Actor;
import io.vlingo.xoom.common.Completes;
import io.vlingo.xoom.lattice.model.projection.Projectable;
import io.vlingo.xoom.lattice.model.projection.ProjectionControl;
import io.vlingo.xoom.schemata.query.view.ContextView;
import io.vlingo.xoom.schemata.query.view.OrganizationView;
import io.vlingo.xoom.schemata.query.view.SchemaView;
import io.vlingo.xoom.schemata.query.view.UnitView;
import io.vlingo.xoom.schemata.query.view.View;

public class SchemaVersionLookUpActor extends Actor implements SchemaVersionLookUp {
  private final SchemaVersionLookUp lookUp;
  private final String nextVersion;
  private final Projectable projectable;
  private final ProjectionControl projectionControl;
  private final Map<Class<?>, SchemaVersionQueryExecutor> queryExecutors;
  private final SchemaVersionLookUpInterest queryInterest;

  public SchemaVersionLookUpActor(final SchemaVersionLookUpInterest queryInterest, final String nextVersion, final Projectable projectable, final ProjectionControl projectionControl) {
    this.queryInterest = queryInterest;
    this.nextVersion = nextVersion;
    this.projectable = projectable;
    this.projectionControl = projectionControl;
    this.lookUp = selfAs(SchemaVersionLookUp.class);
    this.queryExecutors = new HashMap<>(4);
  }

  @Override
  public void lookUpOrganizationName(final Class<OrganizationView> view, final Supplier<Completes<OrganizationView>> supplier) {
    lookUp(view, supplier, (View resolvedView) -> ((OrganizationView) resolvedView.typed()).name());
  }

  @Override
  public void lookUpUnitName(final Class<UnitView> view, final Supplier<Completes<UnitView>> supplier) {
    lookUp(view, supplier, (View resolvedView) -> ((UnitView) resolvedView.typed()).name());
  }

  @Override
  public void lookUpContextNamespace(final Class<ContextView> view, final Supplier<Completes<ContextView>> supplier) {
    lookUp(view, supplier, (View resolvedView) -> ((ContextView) resolvedView.typed()).namespace());
  }

  @Override
  public void lookUpSchemaName(Class<SchemaView> view, final Supplier<Completes<SchemaView>> supplier) {
    lookUp(view, supplier, (View resolvedView) -> ((SchemaView) resolvedView.typed()).name());
  }

  @Override
  public void retryLookUps() {
    for (final Class<?> view : queryExecutors.keySet()) {
      final SchemaVersionQueryExecutor queryExecutor = queryExecutors.get(view);

      if (!queryExecutor.isResolved()) {
        queryExecutor.execute();
      }
    }
  }

  @Override
  public void update(final Class<? extends View> view, final SchemaVersionQueryExecutor maybeResolved) {
    //System.out.println("UPDATE: " + view.getSimpleName() + " RESOLVED?: " + maybeResolved);

    queryExecutors.put(view, maybeResolved);

    if (maybeResolved.isResolved()) {
      if (view == SchemaView.class) {
        queryInterest.informFound(projectable, projectionControl, view, maybeResolved.value(), nextVersion);
      } else {
        queryInterest.informFound(projectable, projectionControl, view, maybeResolved.value());
      }
    } else {
      queryInterest.informNotFound(projectable, projectionControl, view);
    }
  }

  private <V extends View> void lookUp(final Class<V> view, final Supplier<Completes<V>> querySupplier, final Function<View,String> valueSupplier) {
    queryExecutors.put(view, SchemaVersionQueryExecutor.initial(queryExecutor -> {
      querySupplier.get().andThenConsume(queriedView -> {
        if (queriedView != null) {
          final String value = valueSupplier.apply(queriedView).toString();
          lookUp.update(view, queryExecutor.resolveWith(value));
        } else {
          lookUp.update(view, queryExecutor.unresolved());
        }
      });
    }));
  }
}
