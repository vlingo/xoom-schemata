// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata.infra.persistence;

import io.vlingo.actors.World;
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry;
import io.vlingo.lattice.model.stateful.StatefulTypeRegistry.Info;
import io.vlingo.schemata.NoopDispatcher;
import io.vlingo.schemata.query.view.*;
import io.vlingo.symbio.store.state.StateStore;
import io.vlingo.symbio.store.state.inmemory.InMemoryStateStoreActor;

import java.util.Arrays;

public class StateStoreProvider {
    private static StateStoreProvider instance;

    public final StateStore stateStore;

    public static StateStoreProvider instance() {
        return instance;
    }

    public static StateStoreProvider using(World world) {
        if (instance != null) return instance;

        final StatefulTypeRegistry registry = new StatefulTypeRegistry(world);
        final StateStore stateStore = world.stage().actorFor(StateStore.class,
                InMemoryStateStoreActor.class,
                Arrays.asList(new NoopDispatcher()));

        // registerStateAdapters(world.stage());
        registerStatefulTypes(stateStore, registry);

        instance = new StateStoreProvider(stateStore);
        return instance;
    }

    private static void registerStatefulTypes(StateStore stateStore, StatefulTypeRegistry registry) {
        registry
                .register(new Info<>(stateStore, OrganizationView.class, OrganizationView.class.getSimpleName()))
                .register(new Info<>(stateStore, OrganizationsView.class, OrganizationsView.class.getSimpleName()))
                .register(new Info<>(stateStore, UnitView.class, UnitView.class.getSimpleName()))
                .register(new Info<>(stateStore, UnitsView.class, UnitsView.class.getSimpleName()))
                .register(new Info<>(stateStore, ContextView.class, ContextView.class.getSimpleName()))
                .register(new Info<>(stateStore, ContextsView.class, ContextsView.class.getSimpleName()));
    }

//    private static void registerStateAdapters(Stage stage) {
//        final StateAdapterProvider stateAdapterProvider = new StateAdapterProvider(stage.world());
//        stateAdapterProvider.registerAdapter(OrganizationView.class, new OrganizationStateAdapter());
//        new EntryAdapterProvider(stage.world()); // future?
//    }

    private StateStoreProvider(StateStore stateStore) {
        this.stateStore = stateStore;
    }
}
