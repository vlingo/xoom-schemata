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
import io.vlingo.schemata.model.Events.ContextDefined;
import io.vlingo.schemata.model.Events.ContextDescribed;
import io.vlingo.schemata.model.Events.ContextMovedToNamespace;
import io.vlingo.schemata.model.Events.ContextRedefined;
import io.vlingo.schemata.model.OrganizationEntity;
import io.vlingo.schemata.query.OrganizationQueries;
import io.vlingo.schemata.query.OrganizationQueriesActor;
import io.vlingo.schemata.query.view.OrganizationView;
import io.vlingo.symbio.BaseEntry.TextEntry;
import io.vlingo.symbio.DefaultTextEntryAdapter;
import io.vlingo.symbio.EntryAdapterProvider;
import io.vlingo.symbio.State.TextState;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;
import io.vlingo.symbio.store.state.StateStore;

public class StorageProvider {
  public final Journal<String> journal;

  @SuppressWarnings({"unchecked", "rawtypes"})
  public static void initialize(final World world, final SchemataConfig config, StateStore stateStore, final Dispatcher dispatcher) throws Exception {
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
            .registerEntryAdapter(Events.OrganizationDefined.class, new EventAdapter(Events.OrganizationDefined.class));

//    final SchemataObjectStore schemataObjectStore = SchemataObjectStore.instance(config);
//    final ObjectStore objectStore = schemataObjectStore.objectStoreFor(world, dispatcher, schemataObjectStore.persistentMappers());
//    final ObjectTypeRegistry registry = new ObjectTypeRegistry(world);
//    schemataObjectStore.register(registry, objectStore);
//
//    Queries.startAll(world.stageNamed(StageName), objectStore);
  }

  private StorageProvider(final Journal<String> journal) {
    this.journal = journal;
  }
}
