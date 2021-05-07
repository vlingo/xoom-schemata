
# xoom-schemata

[![Gitter](https://img.shields.io/gitter/room/xoom-platform-java/community?logo=gitter)](https://gitter.im/vlingo-platform-java/community)
[![Docker Stars](https://img.shields.io/docker/stars/vlingo/xoom-schemata?logo=docker)](https://hub.docker.com/r/vlingo/xoom-schemata)
[![Docker Pulls](https://img.shields.io/docker/pulls/vlingo/xoom-schemata?logo=docker)](https://hub.docker.com/r/vlingo/xoom-schemata)
[![Build](https://github.com/vlingo/xoom-schemata/workflows/Build/badge.svg)](https://github.com/vlingo/xoom-schemata/actions?query=workflow%3ABuild)


The VLINGO XOOM Schema Registry.


## Quick Start

### Docker

The quickest way to run XOOM Schemata is to use the [docker image](https://hub.docker.com/r/vlingo/xoom-schemata)
published by the VLINGO XOOM Team:

```bash
docker run -it --rm -eXOOM_ENV=dev -p '9019:9019' vlingo/xoom-schemata
```

### Maven

:warning: Make sure you are using a Java 8 JDK.

Build <pre><code>& "<b>{yourInstallPath}</b>\xoom-schemata\mvnw.cmd" clean package -Pfrontend -f "<b>{yourInstallPath}</b>\xoom-schemata\pom.xml"</code></pre>
*e.g. <pre><code>& "<b>d:\vlingo</b>\xoom-schemata\mvnw.cmd" clean package -Pfrontend -f "<b>d:\vlingo</b>\xoom-schemata\pom.xml"</code></pre>*

Run <pre><code>java -jar target/xoom-schemata-<b>{version}</b>-jar-with-dependencies.jar</code></pre>
*e.g. <pre><code>java -jar target/xoom-schemata-<b>1.7.1-SNAPSHOT</b>-jar-with-dependencies.jar</code></pre>*

We provide an interface to allow for easy retrieval of schemata and 
schemata meta information, e.g. available versions and publication status.
The UI/Frontend can be accessed at http://localhost:9019.


## Advanced Options

### Build

If you have maven installed, you can run the full build using:
`mvn clean package -Pfrontend`
Else, there is also a maven wrapper, so you can also build using the command in [Quick Start](#quick-start).

The maven build takes care of the following:
* Generate sources for the schema grammars in `src/main/antlr4`
* Build the backend application
* Download the dependencies for the UI build (`node` & `npm`) [Maven Profile 'frontend']
* Run the UI build (`npm run export` in `src/main/frontend`) [Maven Profile 'frontend']
* Package the backend, frontend and dependencies within a fat jar

#### Docker Build

After building, you can optionally build the docker container with `docker build . -t vlingo/xoom-schemata`.

### Run

If you want to configure the schemata runtime profile, you have several options:
<pre><code>java -jar target/xoom-schemata-<b>{version}</b>-jar-with-dependencies.jar <b>{arg}</b></code></pre>

|arg |What does it do?                            |Properties file|
|--- |---                                         |---|
|dev |starts with an in-memory HSQLDB             |src/main/resources/xoom-schemata-dev.properties |
|prod|starts with a preconfigured PostgreSQL DB   |src/main/resources/xoom-schemata-prod.properties|
|[env](#env)|uses environment variables for configuration|src/main/resources/xoom-schemata-env.properties |

*e.g. <pre><code>java -jar target/xoom-schemata-<b>1.5.1-SNAPSHOT</b>-jar-with-dependencies.jar <b>dev</b></code></pre>*

#### env:
The defaults are the same as configured in **dev**.
|Property|Variable|Default|
|---|---|---|
|server.port        |XOOM_SCHEMATA_PORT       |9019|
|database.type      |XOOM_SCHEMATA_DB_TYPE    |io.vlingo.xoom.schemata.infra.persistence.HSQLDBSchemataObjectStore|
|database.driver    |XOOM_SCHEMATA_DB_DRIVER  |org.hsqldb.jdbc.JDBCDriver|
|database.url       |XOOM_SCHEMATA_DB_URL     |jdbc:hsqldb:mem:|
|database.name      |XOOM_SCHEMATA_DB_NAME    |xoom_schemata|
|database.username  |XOOM_SCHEMATA_DB_USER    |SA|
|database.password  |XOOM_SCHEMATA_DB_PASS    ||
|database.originator|XOOM_SCHEMATA_DB_ORIGINATOR|MAIN| 

#### Docker Run:
You can run the registry with an in-memory database within docker using `docker run -p9019:9019 vlingo/xoom-schemata`.
The docker image supports the three runtime profiles by setting `$XOOM_ENV` inside the Dockerfile accordingly.


## Usage

You can find detailed instructions at:
https://docs.vlingo.io/xoom-schemata#working-with-schema-specifications-and-schema-dependencies.

An example for talking to the schema registry as part of a maven build:
https://github.com/vlingo/xoom-examples/tree/master/xoom-schemata-integration


### API Examples

#### Schema Definitions:

`$ curl -i -X POST -H "Content-Type: application/json" -d '{"organizationId":"","name":"Org1","description":"My organization."}' http://localhost:9019/api/organizations`

`$ curl -i -X POST -H "Content-Type: application/json" -d '{"organizationId":"","unitId":"","name":"Unit1","description":"My unit."}' http://localhost:9019/api/organizations/{orgId}/units`

`$ curl -i -X POST -H "Content-Type: application/json" -d '{"organizationId":"","unitId":"","contextId":"","namespace":"io.vlingo.xoom.schemata","description":"Schemata Context."}' http://localhost:9019/api/organizations/{orgId}/units/{unitId}/contexts`

`$ curl -i -X POST -H "Content-Type: application/json" -d '{"organizationId":"","unitId":"","contextId":"","schemaId":"","category":"Event","name":"SchemaDefined","description":"Schemata was defined event."}' http://localhost:9019/api/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas`

`$ curl -i -X POST -H "Content-Type: application/json" -d '{"organizationId":"","unitId":"","contextId":"","schemaId":"","schemaVersionId":"","description":"Initial revision.","specification":"event SchemaDefined { type eventType }","status":"Draft","previousVersion":"0.0.0","currentVersion":"1.0.0"}' http://localhost:9019/api/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions`

#### Schema Queries:

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/api/organizations`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/api/organizations/{orgId}`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/api/organizations/{orgId}/units`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/api/organizations/{orgId}/units/{unitId}`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/api/organizations/{orgId}/units/{unitId}/contexts`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/api/organizations/{orgId}/units/{unitId}/contexts/{contextId}`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/api/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/api/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/api/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/api/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{versionId}`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/api/schema/categories`
  - Enumeration names: Command, Data, Document, Envelope, Event, Unknown

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/api/schema/scopes`
  - Enumeration names: Public, Private

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/api/code/{reference}/{language}`
  - Takes the form:     /code/Org:Unit:Context:Schema:Version/Language
  - Or more precisely:  /code/vlingo:PlatformDevelopment:io.vlingo.xoom.schemata:SchemaDefined:1.0.0/java

#### Schema Modifications:

`$ curl -i -X PATCH -H "Content-Type: application/json" -d 'My organization changed.' http://localhost:9019/api/organizations/{organizationId}/description`
`$ curl -i -X PATCH -H "Content-Type: application/json" -d 'Org123' http://localhost:9019/api/organizations/{organizationId}/name`

`$ curl -i -X PATCH -H "Content-Type: application/json" -d 'My unit changed.' http://localhost:9019/api/organizations/{orgId}/units/{unitId}/description`
`$ curl -i -X PATCH -H "Content-Type: application/json" -d 'Unit123' http://localhost:9019/api/organizations/{orgId}/units/{unitId}/description`

`$ curl -i -X POST -H "Content-Type: application/json" -d 'My context changed.' http://localhost:9019/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/description`
`$ curl -i -X POST -H "Content-Type: application/json" -d 'io.vlingo.xoom.schemata.changed' http://localhost:9019/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/namespace`

`$ curl -i -X POST -H "Content-Type: application/json" -d 'Command' http://localhost:9019/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/cateogry`
`$ curl -i -X POST -H "Content-Type: application/json" -d 'DefineSchema command defined.' http://localhost:9019/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/description`
`$ curl -i -X POST -H "Content-Type: application/json" -d 'DefineSchema' http://localhost:9019/api/organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/name`

`$ curl -i -X POST -H "Content-Type: application/json" -d 'Initial revision of SchemaDefined.' http://localhost:9019/api/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{versionId}/description`
`$ curl -i -X POST -H "Content-Type: application/json" -d 'event SchemaDefined { type eventType\n timestamp occurredOn }' http://localhost:9019/api/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{versionId}/specification`
`$ curl -i -X POST -H "Content-Type: application/json" -d 'Published' http://localhost:9019/api/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{versionId}/status`


## Development

### Backend

#### Prerequisites:
* Java 8
* Maven, 3.6.0 is known to work, alternatively, you can rely on the bundled maven wrapper

* Generate schema specification sources: `mvn generate-sources`
* Test: `mvn test`
* Build fat jar: `mvn package`

### Frontend

#### Prerequisites:
* Node 12.18.3 is known to work
* If you use vscode, recommended extensions: Svelte for VS Code, Svelte Intellisense

The UI is built using Svelte and Svelte-Materialify.

* Project setup `npm install`
* Compiles and hot-reloads for development `npm run dev`, makes the UI available on http://localhost:8080/
* Compile to static resources for production `npm run export`

When running the UI via `npm run dev`, API calls are proxied to a local backend instance.
It is assumed to be running at http://localhost:9019/.

You can run the backend from:
* the IDE, in case you want to debug the backend in parallel. `XoomInitializer.java`, which gets generated on build, has the `main` method.
* the jar, in case you do not want to delve into the backend code: [Quick Start](#quick-start).
* Docker, in case you don't want bother w/ Java at all: [Docker Run](#docker-run).

Note that this also pulls in the current UI, so don't get confused.


### Testing

#### Unit Tests:

Unit tests live in `src/test/java` and are executed by the Maven build (e.g. `mvn test`) as you would expect.
