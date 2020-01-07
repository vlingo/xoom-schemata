// Copyright © 2012-2018 Vaughn Vernon. All rights reserved.
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
    public final boolean randomPort;
    public final Integer serverPort;
    public final String databaseType;
    public final String databaseDriver;
    public final String databaseUrl;
    public final String databaseName;
    public final String databaseUsername;
    public final String databasePassword;
    public final String databaseOriginator;


    public static SchemataConfig forRuntime(String runtimeType) throws IOException {
        final Properties properties = new EnvVarProperties();
        final String propertiesFile = "/vlingo-schemata-" + runtimeType + ".properties";

        properties.load(Properties.class.getResourceAsStream(propertiesFile));
        return SchemataConfig.from(properties);
    }

    public static SchemataConfig from(Properties props) {
        String portConfig = props.getProperty("server.port");
        Integer port = null;
        if(portConfig != null) {
            port = Integer.parseInt(portConfig);
        }

        return new SchemataConfig(
                Boolean.valueOf(props.getProperty("server.randomPort")),
                port,
                props.getProperty("database.type"),
                props.getProperty("database.driver"),
                props.getProperty("database.url"),
                props.getProperty("database.name"),
                props.getProperty("database.username"),
                props.getProperty("database.password"),
                props.getProperty("database.originator")
        );
    }

    private SchemataConfig(
            boolean randomPort,
            Integer serverPort,
            String databaseType,
            String databaseDriver,
            String databaseUrl,
            String databaseName,
            String databaseUsername,
            String databasePassword,
            String databaseOriginator
    ) {
        this.randomPort = randomPort;
        this.serverPort = serverPort;
        this.databaseType = databaseType;
        this.databaseDriver = databaseDriver;
        this.databaseUrl = databaseUrl;
        this.databaseName = databaseName;
        this.databaseUsername = databaseUsername;
        this.databasePassword = databasePassword;
        this.databaseOriginator = databaseOriginator;
    }
}
