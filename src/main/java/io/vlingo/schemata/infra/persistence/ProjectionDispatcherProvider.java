// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import io.vlingo.actors.Definition;
import io.vlingo.actors.Protocols;
import io.vlingo.actors.Stage;
import io.vlingo.lattice.model.projection.ProjectionDispatcher;
import io.vlingo.lattice.model.projection.ProjectionDispatcher.ProjectToDescription;
import io.vlingo.lattice.model.projection.TextProjectionDispatcherActor;
import io.vlingo.schemata.model.Events.*;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.state.StateStore;

@SuppressWarnings("rawtypes")
public class ProjectionDispatcherProvider {
    private static ProjectionDispatcherProvider instance;

    public final ProjectionDispatcher projectionDispatcher;
    public final Dispatcher storeDispatcher;

    public static ProjectionDispatcherProvider instance() {
        return instance;
    }

    public static ProjectionDispatcherProvider using(final Stage stage, final StateStore stateStore) {
        if (instance != null) return instance;

        final List<ProjectToDescription> descriptions =
                Arrays.asList(
                        ProjectToDescription.with(OrganizationProjection.class, Optional.of(stateStore),
                                OrganizationDefined.class,
                                OrganizationDescribed.class,
                                OrganizationRedefined.class,
                                OrganizationRenamed.class),
                        ProjectToDescription.with(OrganizationsProjection.class, Optional.of(stateStore),
                                OrganizationDefined.class,
                                OrganizationRedefined.class,
                                OrganizationRenamed.class),
                        ProjectToDescription.with(UnitProjection.class, Optional.of(stateStore),
                                UnitDefined.class,
                                UnitDescribed.class,
                                UnitRedefined.class,
                                UnitRenamed.class),
                        ProjectToDescription.with(UnitsProjection.class, Optional.of(stateStore),
                                UnitDefined.class,
                                UnitRedefined.class,
                                UnitRenamed.class),
                        ProjectToDescription.with(ContextProjection.class, Optional.of(stateStore),
                                ContextDefined.class,
                                ContextDescribed.class,
                                ContextRedefined.class,
                                ContextMovedToNamespace.class),
                        ProjectToDescription.with(ContextsProjection.class, Optional.of(stateStore),
                                ContextDefined.class,
                                ContextRedefined.class,
                                ContextMovedToNamespace.class),
                        ProjectToDescription.with(SchemaProjection.class, Optional.of(stateStore),
                                SchemaDefined.class,
                                SchemaDescribed.class,
                                SchemaCategorized.class,
                                SchemaScoped.class,
                                SchemaRedefined.class,
                                SchemaRenamed.class),
                        ProjectToDescription.with(SchemasProjection.class, Optional.of(stateStore),
                                SchemaDefined.class,
                                SchemaRedefined.class,
                                SchemaRenamed.class));

        final Protocols dispatcherProtocols =
                stage.actorFor(
                        new Class<?>[] { Dispatcher.class, ProjectionDispatcher.class },
                        Definition.has(TextProjectionDispatcherActor.class, Definition.parameters(descriptions)));

        final Protocols.Two<Dispatcher, ProjectionDispatcher> dispatchers = Protocols.two(dispatcherProtocols);

        instance = new ProjectionDispatcherProvider(dispatchers._1, dispatchers._2);

        return instance;
    }

    private ProjectionDispatcherProvider(final Dispatcher storeDispatcher, final ProjectionDispatcher projectionDispatcher) {
        this.storeDispatcher = storeDispatcher;
        this.projectionDispatcher = projectionDispatcher;
    }
}
