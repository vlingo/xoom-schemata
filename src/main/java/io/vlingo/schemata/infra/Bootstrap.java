package io.vlingo.schemata.infra;

import io.vlingo.actors.World;
import io.vlingo.http.resource.Configuration;
import io.vlingo.http.resource.Resources;
import io.vlingo.http.resource.Server;
import io.vlingo.schemata.resource.TypeResource;

public class Bootstrap {
    public static void main(final String[] args) {
        World world = World.startWithDefaults("schemata");
        Resources resources = Resources.are(
                new TypeResource().resources(10)
        );
        Server server = Server.startWith(world.stage(), resources, 6582, Configuration.Sizing.define(), Configuration.Timing.define());

        Runtime.getRuntime().addShutdownHook(new Thread(server::stop));
    }
}
