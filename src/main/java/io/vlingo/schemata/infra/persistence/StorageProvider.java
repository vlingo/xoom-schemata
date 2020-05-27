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
import io.vlingo.schemata.model.Events;
import io.vlingo.schemata.model.OrganizationEntity;
import io.vlingo.schemata.query.OrganizationQueries;
import io.vlingo.schemata.query.OrganizationQueriesActor;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;
import io.vlingo.symbio.store.state.StateStore;

public class StorageProvider {
    private static StorageProvider instance;

    public final Journal<String> journal;
    public final OrganizationQueries organizationQueries;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static StorageProvider with(final World world, final SchemataConfig config, StateStore stateStore, final Dispatcher dispatcher) throws Exception {
        if (instance != null) return instance;

//    final EntryAdapterProvider entryAdapterProvider = EntryAdapterProvider.instance(world);

        // TODO Do we need below registrations? (look for: new EntryAdapterProvider(stage.world()); // future?)
//    entryAdapterProvider.registerAdapter(ContextDefined.class, new DefaultTextEntryAdapter());
//    entryAdapterProvider.registerAdapter(ContextDescribed.class, new DefaultTextEntryAdapter());
//    entryAdapterProvider.registerAdapter(ContextMovedToNamespace.class, new DefaultTextEntryAdapter());
//    entryAdapterProvider.registerAdapter(ContextRedefined.class, new DefaultTextEntryAdapter());

        final Journal<String> journal = world.actorFor(Journal.class, InMemoryJournalActor.class, dispatcher);

        // register Queries
        OrganizationQueries organizationQueries = world.stage().actorFor(OrganizationQueries.class, OrganizationQueriesActor.class, stateStore);

        SourcedTypeRegistry registry = new SourcedTypeRegistry(world);
        registry.register(new SourcedTypeRegistry.Info(journal, OrganizationEntity.class, OrganizationEntity.class.getSimpleName()));
        registry.info(OrganizationEntity.class)
                .registerEntryAdapter(Events.OrganizationDefined.class, new EventAdapter(Events.OrganizationDefined.class))
                .registerEntryAdapter(Events.OrganizationRedefined.class, new EventAdapter(Events.OrganizationRedefined.class))
                .registerEntryAdapter(Events.OrganizationDescribed.class, new EventAdapter(Events.OrganizationDescribed.class));

//    final SchemataObjectStore schemataObjectStore = SchemataObjectStore.instance(config);
//    final ObjectStore objectStore = schemataObjectStore.objectStoreFor(world, dispatcher, schemataObjectStore.persistentMappers());
//    final ObjectTypeRegistry registry = new ObjectTypeRegistry(world);
//    schemataObjectStore.register(registry, objectStore);
//
//    Queries.startAll(world.stageNamed(StageName), objectStore);

        instance = new StorageProvider(journal, organizationQueries);
        return instance;
    }

    private StorageProvider(final Journal<String> journal, OrganizationQueries organizationQueries) {
        this.journal = journal;
        this.organizationQueries = organizationQueries;
    }
}
