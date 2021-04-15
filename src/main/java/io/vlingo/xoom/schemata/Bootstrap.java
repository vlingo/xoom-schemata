// Copyright © 2012-2021 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.schemata;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.List;

import io.vlingo.xoom.actors.Grid;
import io.vlingo.xoom.actors.Stage;
import io.vlingo.xoom.cluster.model.Properties;
import io.vlingo.xoom.http.resource.Configuration;
import io.vlingo.xoom.http.resource.Configuration.Timing;
import io.vlingo.xoom.http.resource.StaticFilesConfiguration;
import io.vlingo.xoom.lattice.grid.GridClusterProperties;
import io.vlingo.xoom.schemata.infra.persistence.ProjectionDispatcherProvider;
import io.vlingo.xoom.schemata.infra.persistence.StateStoreProvider;
import io.vlingo.xoom.schemata.infra.persistence.StorageProvider;
import io.vlingo.xoom.turbo.XoomInitializationAware;
import io.vlingo.xoom.turbo.annotation.initializer.ResourceHandlers;
import io.vlingo.xoom.turbo.annotation.initializer.Xoom;
import io.vlingo.xoom.turbo.annotation.initializer.XoomInitializationException;

@Xoom(name = "xoom-schemata")
@ResourceHandlers(packages = "io.vlingo.xoom.schemata.resource")
public class Bootstrap implements XoomInitializationAware {

  @Override
  public void onInit(final Grid grid) {
  }

  @Override
  public Configuration configureServer(final Grid grid, final String[] args) {
    try {
      final Stage stage = grid.world().stage();
      final SchemataConfig config = SchemataConfig.forRuntime(args.length == 0 ? "dev" : args[0]);

      final StateStoreProvider stateStoreProvider = StateStoreProvider.using(stage.world(), config);

      final ProjectionDispatcherProvider projectionDispatcherProvider =
              ProjectionDispatcherProvider.using(stage, stateStoreProvider.stateStore);

      StorageProvider.with(stage.world(), stateStoreProvider.stateStore, projectionDispatcherProvider.storeDispatcher, config);

      final int port =
              config.randomPort ? nextFreePort(9019, 9100) :
                      config.serverPort;

      return Configuration.define().withPort(port).with(Timing.defineWith(7, 3, 100));
    } catch (final Exception exception) {
      throw new XoomInitializationException(exception);
    }
  }

  @Override
  public String parseNodeName(final String[] args) {
    return Schemata.NodeName;
  }

  private int nextFreePort(final int from, final int to) throws IOException {
    int port = from;
    while (port < to) {
      if (isPortFree(port)) {
        return port;
      } else {
        port++;
      }
    }
    throw new IOException("No open port in range " + from + " to " + to);
  }

  private boolean isPortFree(final int port) {
    try {
      new ServerSocket(port).close();
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  @Override
  public Properties clusterProperties() {
    try {
      final java.util.Properties properties = new java.util.Properties();
      properties.load(this.getClass().getResourceAsStream("/xoom-cluster.properties"));
      return Properties.openWith(properties);
    } catch (Throwable t) {
      System.out.println("Unable to load cluster properties for Schemata; using default single-node cluster.");
      return GridClusterProperties.oneNode();
    }
  }

  @Override
  public StaticFilesConfiguration staticFilesConfiguration() {
      final List<String> subPaths = Arrays.asList("/", "/client", "/organization", "/unit", "/context", "/schema", "/schemaVersion");
      return StaticFilesConfiguration.defineWith(100, "frontend", subPaths);
  }

}
