package io.vlingo.schemata;

import io.vlingo.actors.World;
import io.vlingo.http.resource.*;
import io.vlingo.lattice.model.sourcing.SourcedTypeRegistry;
import io.vlingo.schemata.infra.persistence.EntryAdapters;
import io.vlingo.schemata.resource.SchemaResource;
import io.vlingo.schemata.resource.UiResource;
import io.vlingo.symbio.store.journal.Journal;
import io.vlingo.symbio.store.journal.inmemory.InMemoryJournalActor;

public class Bootstrap {
    private static final int SCHEMATA_PORT = 9019;

    private static Bootstrap instance;
    private final Server server;
    private final World world;

    @SuppressWarnings("rawtypes")
    public Bootstrap() {
        world = World.startWithDefaults("vlingo-schemata");

        NoopJournalListener listener = new NoopJournalListener();
        Journal<String> journal = Journal.using(world.stage(), InMemoryJournalActor.class, listener);

        SourcedTypeRegistry registry = new SourcedTypeRegistry(world);
        EntryAdapters.register(registry, journal);

        Resource schemaResource = SchemaResource.asResource();
        Resource uiResource = UiResource.asResource();
        Resources allResources = Resources.are(schemaResource, uiResource);

        server = Server.startWith(world.stage(),
                allResources,
                SCHEMATA_PORT,
                Configuration.Sizing.define(),
                Configuration.Timing.define());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (instance != null) {
                instance.server.stop();

                System.out.println("\n");
                System.out.println("=======================");
                System.out.println("Stopping vlingo-schemata.");
                System.out.println("=======================");
            }
        }));
    }

    static Bootstrap instance() {
        if (instance == null) {
            instance = new Bootstrap();
        }
        return instance;
    }

    public static void main(final String[] args) {
        System.out.println("=======================");
        System.out.println("service: vlingo-schemata.");
        System.out.println("=======================");
        Bootstrap.instance();
    }
}
