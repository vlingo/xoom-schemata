// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import io.vlingo.actors.World;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.schemata.SchemataConfig;
import io.vlingo.schemata.model.*;
import io.vlingo.schemata.model.Events.*;
import io.vlingo.schemata.query.*;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;
import io.vlingo.symbio.store.state.StateStore;

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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static StorageProvider with(final World world, final SchemataConfig config, StateStore stateStore, final Dispatcher dispatcher) throws Exception {
        if (instance != null) return instance;

        final Journal<String> journal = world.actorFor(Journal.class, InMemoryJournalActor.class, dispatcher);
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

        instance = new StorageProvider(journal, organizationQueries, unitQueries, contextQueries, schemaQueries, schemaVersionQueries, codeQueries,
                typeResolverQueries);
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
}
