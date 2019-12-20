package io.vlingo.schemata;

import io.vlingo.common.config.EnvVarProperties;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

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
