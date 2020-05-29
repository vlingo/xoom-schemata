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
import io.vlingo.schemata.model.Events.*;
import io.vlingo.schemata.model.OrganizationEntity;
import io.vlingo.schemata.model.UnitEntity;
import io.vlingo.schemata.query.OrganizationQueries;
import io.vlingo.schemata.query.OrganizationQueriesActor;
import io.vlingo.schemata.query.UnitQueries;
import io.vlingo.schemata.query.UnitQueriesActor;
import io.vlingo.symbio.store.dispatch.Dispatcher;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;
import io.vlingo.symbio.store.state.StateStore;

public class StorageProvider {
    private static StorageProvider instance;

    public final Journal<String> journal;
    public final OrganizationQueries organizationQueries;
    public final UnitQueries unitQueries;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static StorageProvider with(final World world, final SchemataConfig config, StateStore stateStore, final Dispatcher dispatcher) throws Exception {
        if (instance != null) return instance;

        final Journal<String> journal = world.actorFor(Journal.class, InMemoryJournalActor.class, dispatcher);

        // register Queries
        OrganizationQueries organizationQueries = world.stage().actorFor(OrganizationQueries.class, OrganizationQueriesActor.class, stateStore);
        UnitQueries unitQueries = world.stage().actorFor(UnitQueries.class, UnitQueriesActor.class, stateStore);

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

        instance = new StorageProvider(journal, organizationQueries, unitQueries);
        return instance;
    }

    private StorageProvider(final Journal<String> journal, OrganizationQueries organizationQueries, UnitQueries unitQueries) {
        this.journal = journal;
        this.organizationQueries = organizationQueries;
        this.unitQueries = unitQueries;
    }
}
