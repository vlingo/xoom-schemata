// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata.infra.persistence;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;

import io.vlingo.xoom.actors.Definition;
import io.vlingo.xoom.actors.World;
import io.vlingo.xoom.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.xoom.schemata.SchemataConfig;
import io.vlingo.xoom.schemata.model.ContextEntity;
import io.vlingo.xoom.schemata.model.Events.ContextDefined;
import io.vlingo.xoom.schemata.model.Events.ContextDescribed;
import io.vlingo.xoom.schemata.model.Events.ContextMovedToNamespace;
import io.vlingo.xoom.schemata.model.Events.ContextRedefined;
import io.vlingo.xoom.schemata.model.Events.OrganizationDefined;
import io.vlingo.xoom.schemata.model.Events.OrganizationDescribed;
import io.vlingo.xoom.schemata.model.Events.OrganizationRedefined;
import io.vlingo.xoom.schemata.model.Events.OrganizationRenamed;
import io.vlingo.xoom.schemata.model.Events.SchemaCategorized;
import io.vlingo.xoom.schemata.model.Events.SchemaDefined;
import io.vlingo.xoom.schemata.model.Events.SchemaDescribed;
import io.vlingo.xoom.schemata.model.Events.SchemaRedefined;
import io.vlingo.xoom.schemata.model.Events.SchemaRenamed;
import io.vlingo.xoom.schemata.model.Events.SchemaScoped;
import io.vlingo.xoom.schemata.model.Events.SchemaVersionAssigned;
import io.vlingo.xoom.schemata.model.Events.SchemaVersionDefined;
import io.vlingo.xoom.schemata.model.Events.SchemaVersionDeprecated;
import io.vlingo.xoom.schemata.model.Events.SchemaVersionDescribed;
import io.vlingo.xoom.schemata.model.Events.SchemaVersionPublished;
import io.vlingo.xoom.schemata.model.Events.SchemaVersionRemoved;
import io.vlingo.xoom.schemata.model.Events.SchemaVersionSpecified;
import io.vlingo.xoom.schemata.model.Events.UnitDefined;
import io.vlingo.xoom.schemata.model.Events.UnitDescribed;
import io.vlingo.xoom.schemata.model.Events.UnitRedefined;
import io.vlingo.xoom.schemata.model.Events.UnitRenamed;
import io.vlingo.xoom.schemata.model.OrganizationEntity;
import io.vlingo.xoom.schemata.model.SchemaEntity;
import io.vlingo.xoom.schemata.model.SchemaVersionEntity;
import io.vlingo.xoom.schemata.model.UnitEntity;
import io.vlingo.xoom.schemata.query.CodeQueries;
import io.vlingo.xoom.schemata.query.CodeQueriesActor;
import io.vlingo.xoom.schemata.query.ContextQueries;
import io.vlingo.xoom.schemata.query.ContextQueriesActor;
import io.vlingo.xoom.schemata.query.OrganizationQueries;
import io.vlingo.xoom.schemata.query.OrganizationQueriesActor;
import io.vlingo.xoom.schemata.query.SchemaQueries;
import io.vlingo.xoom.schemata.query.SchemaQueriesActor;
import io.vlingo.xoom.schemata.query.SchemaVersionQueries;
import io.vlingo.xoom.schemata.query.SchemaVersionQueriesActor;
import io.vlingo.xoom.schemata.query.TypeResolverQueries;
import io.vlingo.xoom.schemata.query.TypeResolverQueriesActor;
import io.vlingo.xoom.schemata.query.UnitQueries;
import io.vlingo.xoom.schemata.query.UnitQueriesActor;
import io.vlingo.xoom.symbio.Entry;
import io.vlingo.xoom.symbio.State.TextState;
import io.vlingo.xoom.symbio.store.DataFormat;
import io.vlingo.xoom.symbio.store.common.jdbc.Configuration;
import io.vlingo.xoom.symbio.store.common.jdbc.postgres.PostgresConfigurationProvider;
import io.vlingo.xoom.symbio.store.dispatch.Dispatchable;
import io.vlingo.xoom.symbio.store.dispatch.Dispatcher;
import io.vlingo.xoom.symbio.store.dispatch.DispatcherControl;
import io.vlingo.xoom.symbio.store.dispatch.control.DispatcherControlActor;
import io.vlingo.xoom.symbio.store.journal.Journal;
import io.vlingo.xoom.symbio.store.journal.inmemory.InMemoryJournalActor;
import io.vlingo.xoom.symbio.store.journal.jdbc.JDBCDispatcherControlDelegate;
import io.vlingo.xoom.symbio.store.journal.jdbc.JDBCJournalActor;
import io.vlingo.xoom.symbio.store.journal.jdbc.JDBCJournalInstantWriter;
import io.vlingo.xoom.symbio.store.journal.jdbc.JDBCJournalWriter;
import io.vlingo.xoom.symbio.store.state.StateStore;

public class StorageProvider {
    private static StorageProvider instance;

    public final Journal<String> journal;
    public final OrganizationQueries organizationQueries;
    public final UnitQueries unitQueries;
    public final ContextQueries contextQueries;
    public final SchemaQueries schemaQueries;
    public final SchemaVersionQueries schemaVersionQueries;
    public final CodeQueries codeQueries;
    public final TypeResolverQueries typeResolverQueries;

    @SuppressWarnings({"rawtypes"})
    public static StorageProvider with(final World world, StateStore stateStore, final Dispatcher dispatcher, final SchemataConfig config) throws Exception {
        if (instance != null) return instance;

        return newInstance(world, stateStore, dispatcher, config);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static StorageProvider newInstance(final World world, StateStore stateStore, final Dispatcher dispatcher, final SchemataConfig config) throws Exception {
        final Journal<String> journal = startJounral(world, dispatcher, config);

        SourcedTypeRegistry registry = new SourcedTypeRegistry(world);

        registry.register(new SourcedTypeRegistry.Info(journal, OrganizationEntity.class, OrganizationEntity.class.getSimpleName()));
        registry.info(OrganizationEntity.class)
                .registerEntryAdapter(OrganizationDefined.class, new EventAdapter(OrganizationDefined.class))
                .registerEntryAdapter(OrganizationRedefined.class, new EventAdapter(OrganizationRedefined.class))
                .registerEntryAdapter(OrganizationDescribed.class, new EventAdapter(OrganizationDescribed.class))
                .registerEntryAdapter(OrganizationRenamed.class, new EventAdapter(OrganizationRenamed.class));

        registry.register(new SourcedTypeRegistry.Info(journal, UnitEntity.class, UnitEntity.class.getSimpleName()));
        registry.info(UnitEntity.class)
                .registerEntryAdapter(UnitDefined.class, new EventAdapter<>(UnitDefined.class))
                .registerEntryAdapter(UnitRedefined.class, new EventAdapter<>(UnitRedefined.class))
                .registerEntryAdapter(UnitDescribed.class, new EventAdapter<>(UnitDescribed.class))
                .registerEntryAdapter(UnitRenamed.class, new EventAdapter<>(UnitRenamed.class));

        registry.register(new SourcedTypeRegistry.Info(journal, ContextEntity.class, ContextEntity.class.getSimpleName()));
        registry.info(ContextEntity.class)
                .registerEntryAdapter(ContextDefined.class, new EventAdapter<>(ContextDefined.class))
                .registerEntryAdapter(ContextRedefined.class, new EventAdapter<>(ContextRedefined.class))
                .registerEntryAdapter(ContextDescribed.class, new EventAdapter<>(ContextDescribed.class))
                .registerEntryAdapter(ContextMovedToNamespace.class, new EventAdapter<>(ContextMovedToNamespace.class));

        registry.register(new SourcedTypeRegistry.Info(journal, SchemaEntity.class, SchemaEntity.class.getSimpleName()));
        registry.info(SchemaEntity.class)
                .registerEntryAdapter(SchemaDefined.class, new EventAdapter<>(SchemaDefined.class))
                .registerEntryAdapter(SchemaDescribed.class, new EventAdapter<>(SchemaDescribed.class))
                .registerEntryAdapter(SchemaCategorized.class, new EventAdapter<>(SchemaCategorized.class))
                .registerEntryAdapter(SchemaScoped.class, new EventAdapter<>(SchemaScoped.class))
                .registerEntryAdapter(SchemaRedefined.class, new EventAdapter<>(SchemaRedefined.class))
                .registerEntryAdapter(SchemaRenamed.class, new EventAdapter<>(SchemaRenamed.class));

        registry.register(new SourcedTypeRegistry.Info(journal, SchemaVersionEntity.class, SchemaVersionEntity.class.getSimpleName()));
        registry.info(SchemaVersionEntity.class)
                .registerEntryAdapter(SchemaVersionDefined.class, new EventAdapter<>(SchemaVersionDefined.class))
                .registerEntryAdapter(SchemaVersionDescribed.class, new EventAdapter<>(SchemaVersionDescribed.class))
                .registerEntryAdapter(SchemaVersionAssigned.class, new EventAdapter<>(SchemaVersionAssigned.class))
                .registerEntryAdapter(SchemaVersionSpecified.class, new EventAdapter<>(SchemaVersionSpecified.class))
                .registerEntryAdapter(SchemaVersionPublished.class, new EventAdapter<>(SchemaVersionPublished.class))
                .registerEntryAdapter(SchemaVersionDeprecated.class, new EventAdapter<>(SchemaVersionDeprecated.class))
                .registerEntryAdapter(SchemaVersionRemoved.class, new EventAdapter<>(SchemaVersionRemoved.class));

        // register Queries
        OrganizationQueries organizationQueries = world.stage().actorFor(OrganizationQueries.class, OrganizationQueriesActor.class, stateStore);
        UnitQueries unitQueries = world.stage().actorFor(UnitQueries.class, UnitQueriesActor.class, stateStore);
        ContextQueries contextQueries = world.stage().actorFor(ContextQueries.class, ContextQueriesActor.class, stateStore);
        SchemaQueries schemaQueries = world.stage().actorFor(SchemaQueries.class, SchemaQueriesActor.class, stateStore);
        SchemaVersionQueries schemaVersionQueries = world.stage().actorFor(SchemaVersionQueries.class, SchemaVersionQueriesActor.class, stateStore);
        CodeQueries codeQueries = world.stage().actorFor(CodeQueries.class, CodeQueriesActor.class, stateStore);
        TypeResolverQueries typeResolverQueries = world.stage().actorFor(TypeResolverQueries.class, TypeResolverQueriesActor.class, codeQueries);

        instance = new StorageProvider(journal, organizationQueries, unitQueries, contextQueries, schemaQueries, schemaVersionQueries, codeQueries, typeResolverQueries);

        return instance;
    }

    public static StorageProvider instance() {
        return instance;
    }

    private StorageProvider(final Journal<String> journal, OrganizationQueries organizationQueries, UnitQueries unitQueries,
                            ContextQueries contextQueries, SchemaQueries schemaQueries, SchemaVersionQueries schemaVersionQueries, CodeQueries codeQueries,
                            TypeResolverQueries typeResolverQueries) {
        this.journal = journal;
        this.organizationQueries = organizationQueries;
        this.unitQueries = unitQueries;
        this.contextQueries = contextQueries;
        this.schemaQueries = schemaQueries;
        this.schemaVersionQueries = schemaVersionQueries;
        this.codeQueries = codeQueries;
        this.typeResolverQueries = typeResolverQueries;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static Journal<String> startJounral(final World world, final Dispatcher dispatcher, final SchemataConfig config) throws Exception {
      final Journal<String> journal;

      if (config.isProductionRuntimeType()) {
        final Configuration postgresConfiguration =
                PostgresConfigurationProvider.configuration(
                        DataFormat.Text,
                        config.databaseUrl,
                        config.databaseName,
                        config.databaseUsername,
                        config.databasePassword,
                        config.databaseOriginator,
                        true);

        final JDBCDispatcherControlDelegate dispatcherControlDelegate =
                new JDBCDispatcherControlDelegate(Configuration.cloneOf(postgresConfiguration), world.defaultLogger());

        final List<Dispatcher<Dispatchable<Entry<String>, TextState>>> dispatchers =
                Collections.singletonList(typed(dispatcher));

        DispatcherControl dispatcherControl = world.stage().actorFor(DispatcherControl.class,
                Definition.has(DispatcherControlActor.class,
                        new DispatcherControl.DispatcherControlInstantiator(
                                dispatchers,
                                dispatcherControlDelegate,
                                config.confirmationExpirationInterval,
                                config.confirmationExpiration)));

        JDBCJournalWriter journalWriter = new JDBCJournalInstantWriter(postgresConfiguration, dispatchers, dispatcherControl);

        journal = world.stage().actorFor(Journal.class, JDBCJournalActor.class, postgresConfiguration, journalWriter);

      } else {
        journal = world.actorFor(Journal.class, InMemoryJournalActor.class, Arrays.asList(dispatcher));
      }

      return journal;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Dispatcher<Dispatchable<Entry<String>, TextState>> typed(Dispatcher dispatcher) {
        return dispatcher;
    }
}
