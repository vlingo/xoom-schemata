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
import io.vlingo.schemata.model.Events.ContextDefined;
import io.vlingo.schemata.model.Events.ContextDescribed;
import io.vlingo.schemata.model.Events.ContextMovedToNamespace;
import io.vlingo.schemata.model.Events.ContextRedefined;
import io.vlingo.schemata.model.Events.OrganizationDefined;
import io.vlingo.schemata.model.Events.OrganizationDescribed;
import io.vlingo.schemata.model.Events.OrganizationRedefined;
import io.vlingo.schemata.model.Events.OrganizationRenamed;
import io.vlingo.schemata.model.Events.SchemaCategorized;
import io.vlingo.schemata.model.Events.SchemaDefined;
import io.vlingo.schemata.model.Events.SchemaDescribed;
import io.vlingo.schemata.model.Events.SchemaRedefined;
import io.vlingo.schemata.model.Events.SchemaRenamed;
import io.vlingo.schemata.model.Events.SchemaScoped;
import io.vlingo.schemata.model.Events.SchemaVersionAssigned;
import io.vlingo.schemata.model.Events.SchemaVersionDefined;
import io.vlingo.schemata.model.Events.SchemaVersionDeprecated;
import io.vlingo.schemata.model.Events.SchemaVersionDescribed;
import io.vlingo.schemata.model.Events.SchemaVersionPublished;
import io.vlingo.schemata.model.Events.SchemaVersionRemoved;
import io.vlingo.schemata.model.Events.SchemaVersionSpecified;
import io.vlingo.schemata.model.Events.UnitDefined;
import io.vlingo.schemata.model.Events.UnitDescribed;
import io.vlingo.schemata.model.Events.UnitRedefined;
import io.vlingo.schemata.model.Events.UnitRenamed;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.state.StateStore;

@SuppressWarnings("rawtypes")
public class ProjectionDispatcherProvider {
    public final ProjectionDispatcher projectionDispatcher;
    public final Dispatcher storeDispatcher;

    @SuppressWarnings("unchecked")
    public static ProjectionDispatcherProvider using(final Stage stage, final StateStore stateStore) {
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
                                OrganizationDefined.class,
                                UnitDefined.class,
                                UnitRedefined.class,
                                UnitRenamed.class),
                        ProjectToDescription.with(ContextProjection.class, Optional.of(stateStore),
                                ContextDefined.class,
                                ContextDescribed.class,
                                ContextRedefined.class,
                                ContextMovedToNamespace.class),
                        ProjectToDescription.with(ContextsProjection.class, Optional.of(stateStore),
                                UnitDefined.class,
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
                                ContextDefined.class,
                                SchemaDefined.class,
                                SchemaRedefined.class,
                                SchemaRenamed.class),
                        ProjectToDescription.with(SchemaVersionProjection.class, Optional.of(stateStore),
                                SchemaVersionDefined.class,
                                SchemaVersionDescribed.class,
                                SchemaVersionAssigned.class,
                                SchemaVersionSpecified.class,
                                SchemaVersionPublished.class,
                                SchemaVersionDeprecated.class,
                                SchemaVersionRemoved.class),
                        ProjectToDescription.with(SchemaVersionsProjection.class, Optional.of(stateStore),
                                SchemaDefined.class,
                                SchemaVersionDefined.class,
                                SchemaVersionDescribed.class,
                                SchemaVersionAssigned.class,
                                SchemaVersionSpecified.class,
                                SchemaVersionPublished.class,
                                SchemaVersionDeprecated.class,
                                SchemaVersionRemoved.class),
                        ProjectToDescription.with(NamedSchemaProjection.class, Optional.of(stateStore),
                                SchemaDefined.class,
                                SchemaVersionDefined.class),
                        ProjectToDescription.with(CodeProjection.class, Optional.of(stateStore),
                                SchemaVersionDefined.class));

        final Protocols dispatcherProtocols =
                stage.actorFor(
                        new Class<?>[] { Dispatcher.class, ProjectionDispatcher.class },
                        Definition.has(TextProjectionDispatcherActor.class, Definition.parameters(descriptions)));

        final Protocols.Two<Dispatcher, ProjectionDispatcher> dispatchers = Protocols.two(dispatcherProtocols);

        return new ProjectionDispatcherProvider(dispatchers._1, dispatchers._2);
    }

    private ProjectionDispatcherProvider(final Dispatcher storeDispatcher, final ProjectionDispatcher projectionDispatcher) {
        this.storeDispatcher = storeDispatcher;
        this.projectionDispatcher = projectionDispatcher;
    }
}
