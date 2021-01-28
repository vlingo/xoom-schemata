// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.schemata;

import java.io.IOException;
import java.util.Properties;

import io.vlingo.common.config.EnvVarProperties;

public class SchemataConfig {
    public static final String RUNTIME_TYPE_PROD = "prod";
    public static final String RUNTIME_TYPE_DEV = "dev";
    public static final String RUNTIME_TYPE_ENV = "env";

    public final boolean randomPort;
    public final Integer serverPort;
    public final String databaseDriver;
    public final String databaseUrl;
    public final String databaseName;
    public final String databaseUsername;
    public final String databasePassword;
    public final String databaseOriginator;
    public final String runtimeType;
    public final long confirmationExpirationInterval;
    public final long confirmationExpiration;


    public static SchemataConfig forRuntime(String runtimeType) throws IOException {
        final Properties properties = new EnvVarProperties();
        final String propertiesFile = "/vlingo-schemata-" + runtimeType + ".properties";

        properties.load(SchemataConfig.class.getResourceAsStream(propertiesFile));

        return SchemataConfig.from(properties, runtimeType);
    }

    public static SchemataConfig from(Properties props, final String runtimeType) {
        String portConfig = props.getProperty("server.port");
        Integer port = null;
        if(portConfig != null) {
            port = Integer.parseInt(portConfig);
        }

        return new SchemataConfig(
                Boolean.valueOf(props.getProperty("server.randomPort")),
                port,
                props.getProperty("database.driver"),
                props.getProperty("database.url"),
                props.getProperty("database.name"),
                props.getProperty("database.username"),
                props.getProperty("database.password"),
                props.getProperty("database.originator"),
                Long.parseLong(props.getProperty("dispatcher.control.confirmation_expiration_interval", "2000")),
                Long.parseLong(props.getProperty("dispatcher.control.confirmation_expiration", "1000")),
                runtimeType
        );
    }

    public boolean isDevelopmentRuntimeType() {
      return runtimeType.equalsIgnoreCase(RUNTIME_TYPE_DEV);
    }

    public boolean isEnvironmentRuntimeType() {
      return runtimeType.equalsIgnoreCase(RUNTIME_TYPE_ENV);
    }

    public boolean isProductionRuntimeType() {
      return runtimeType.equalsIgnoreCase(RUNTIME_TYPE_PROD);
    }

    private SchemataConfig(
            boolean randomPort,
            Integer serverPort,
            String databaseDriver,
            String databaseUrl,
            String databaseName,
            String databaseUsername,
            String databasePassword,
            String databaseOriginator,
            long confirmationExpirationInterval,
            long confirmationExpiration,
            final String runtimeType
    ) {
        this.randomPort = randomPort;
        this.serverPort = serverPort;
        this.databaseDriver = databaseDriver;
        this.databaseUrl = databaseUrl;
        this.databaseName = databaseName;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
        this.databaseOriginator = databaseOriginator;
        this.confirmationExpirationInterval = confirmationExpirationInterval;
        this.confirmationExpiration = confirmationExpiration;
        this.runtimeType = runtimeType;
    }
}
