
# vlingo-schemata

[![Gitter](https://img.shields.io/gitter/room/vlingo-platform-java/community?logo=gitter)](https://gitter.im/vlingo-platform-java/community)
[![Docker Stars](https://img.shields.io/docker/stars/vlingo/vlingo-schemata?logo=docker)](https://hub.docker.com/r/vlingo/vlingo-schemata)
[![Docker Pulls](https://img.shields.io/docker/pulls/vlingo/vlingo-schemata?logo=docker)](https://hub.docker.com/r/vlingo/vlingo-schemata)
[![Travis Build](https://img.shields.io/travis/vlingo/vlingo-schemata?logo=travis)](https://travis-ci.org/vlingo/vlingo-schemata)
[![CircleCI](https://img.shields.io/circleci/build/github/vlingo/vlingo-schemata?logo=CircleCi)](https://circleci.com/gh/vlingo/vlingo-schemata)


The **VLINGO**/PLATFORM schema registry.


## Quick Start

:warning: Make sure you are using a Java 8 JDK.

Build <pre><code>& "<b>{yourInstallPath}</b>\vlingo-schemata\mvnw.cmd" clean package -Pfrontend -f "<b>{yourInstallPath}</b>\vlingo-schemata\pom.xml"</code></pre>
*e.g. <pre><code>& "<b>d:\vlingo</b>\vlingo-schemata\mvnw.cmd" clean package -Pfrontend -f "<b>d:\vlingo</b>\vlingo-schemata\pom.xml"</code></pre>*

Run <pre><code>java -jar vlingo-schemata-<b>{version}</b>-jar-with-dependencies.jar</code></pre>
*e.g. <pre><code>java -jar vlingo-schemata-<b>1.3.4-SNAPSHOT</b>-jar-with-dependencies.jar</code></pre>*

We provide an interface to allow for easy retrieval of schemata and 
schemata meta information, e.g. available versions and publication status.
~~The UI/Frontend can be accessed at http://localhost:9019.~~

:warning: Currently, the UI is accessed by navigating to http://localhost:9019/app/index.html and then clicking one of the navigation links.


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

#### Docker Build:
After building, you can optionally build the docker container with `docker build . -t vlingo/vlingo-schemata`.


### Run

If you want to configure the schemata runtime profile, you have several options:
<pre><code>java -jar vlingo-schemata-<b>{version}</b>-jar-with-dependencies.jar <b>{arg}</b></code></pre>

|arg |What does it do?                            |Properties file|
|--- |---                                         |---|
|dev |starts with an in-memory HSQLDB             |src/main/resources/vlingo-schemata-dev.properties |
|prod|starts with a preconfigured PostgreSQL DB   |src/main/resources/vlingo-schemata-prod.properties|
|[env](#env)|uses environment variables for configuration|src/main/resources/vlingo-schemata-env.properties |

*e.g. <pre><code>java -jar vlingo-schemata-<b>1.3.4-SNAPSHOT</b>-jar-with-dependencies.jar <b>dev</b></code></pre>*

#### env:
The defaults are the same as configured in **dev**.
|Property|Variable|Default|
|---|---|---|
|server.port        |VLINGO_SCHEMATA_PORT       |9019|
|database.type      |VLINGO_SCHEMATA_DB_TYPE    |io.vlingo.schemata.infra.persistence.HSQLDBSchemataObjectStore|
|database.driver    |VLINGO_SCHEMATA_DB_DRIVER  |org.hsqldb.jdbc.JDBCDriver|
|database.url       |VLINGO_SCHEMATA_DB_URL     |jdbc:hsqldb:mem:|
|database.name      |VLINGO_SCHEMATA_DB_NAME    |vlingo_schemata|
|database.username  |VLINGO_SCHEMATA_DB_USER    |SA|
|database.password  |VLINGO_SCHEMATA_DB_PASS    ||
|database.originator|VLINGO_SCHEMATA_DB_ORIGINATOR|MAIN| 

#### Docker Run:
You can run the registry with an in-memory database within docker using `docker run -p9019:9019 vlingo/vlingo-schemata`.
The docker image supports the three runtime profiles by setting `$VLINGO_ENV` inside the Dockerfile accordingly.


## Usage

You can find detailed instructions at:
https://docs.vlingo.io/vlingo-schemata#working-with-schema-specifications-and-schema-dependencies.

An example for talking to the schema registry as part of a maven build:
https://github.com/vlingo/vlingo-examples/tree/master/vlingo-schemata-integration


### API Examples

#### Schema Definitions:

`$ curl -i -X POST -H "Content-Type: application/json" -d '{"organizationId":"","name":"Org1","description":"My organization."}' http://localhost:9019/organizations`

`$ curl -i -X POST -H "Content-Type: application/json" -d '{"organizationId":"","unitId":"","name":"Unit1","description":"My unit."}' http://localhost:9019/organizations/{orgId}/units`

`$ curl -i -X POST -H "Content-Type: application/json" -d '{"organizationId":"","unitId":"","contextId":"","namespace":"io.vlingo.schemata","description":"Schemata Context."}' http://localhost:9019/organizations/{orgId}/units/{unitId}/contexts`

`$ curl -i -X POST -H "Content-Type: application/json" -d '{"organizationId":"","unitId":"","contextId":"","schemaId":"","category":"Event","name":"SchemaDefined","description":"Schemata was defined event."}' http://localhost:9019/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas`

`$ curl -i -X POST -H "Content-Type: application/json" -d '{"organizationId":"","unitId":"","contextId":"","schemaId":"","schemaVersionId":"","description":"Initial revision.","specification":"event SchemaDefined { type eventType }","status":"Draft","previousVersion":"0.0.0","currentVersion":"1.0.0"}' http://localhost:9019/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions`

#### Schema Queries:

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/organizations`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/organizations/{orgId}`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/organizations/{orgId}/units`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/organizations/{orgId}/units/{unitId}`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/organizations/{orgId}/units/{unitId}/contexts`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/organizations/{orgId}/units/{unitId}/contexts/{contextId}`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{versionId}`

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/schema/categories`
  - Enumeration names: Command, Data, Document, Envelope, Event, Unknown

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/schema/scopes`
  - Enumeration names: Public, Private

`$ curl -i -X GET -H "Accept: application/json" http://localhost:9019/code/{reference}/{language}`
  - Takes the form:     /code/Org:Unit:Context:Schema:Version/Language
  - Or more precisely:  /code/vlingo:PlatformDevelopment:io.vlingo.schemata:SchemaDefined:1.0.0/java

#### Schema Modifications:

`$ curl -i -X PATCH -H "Content-Type: application/json" -d 'My organization changed.' http://localhost:9019/organizations/{organizationId}/description`
`$ curl -i -X PATCH -H "Content-Type: application/json" -d 'Org123' http://localhost:9019/organizations/{organizationId}/name`

`$ curl -i -X PATCH -H "Content-Type: application/json" -d 'My unit changed.' http://localhost:9019/organizations/{orgId}/units/{unitId}/description`
`$ curl -i -X PATCH -H "Content-Type: application/json" -d 'Unit123' http://localhost:9019/organizations/{orgId}/units/{unitId}/description`

`$ curl -i -X POST -H "Content-Type: application/json" -d 'My context changed.' http://localhost:9019//organizations/{organizationId}/units/{unitId}/contexts/{contextId}/description`
`$ curl -i -X POST -H "Content-Type: application/json" -d 'io.vlingo.schemata.changed' http://localhost:9019//organizations/{organizationId}/units/{unitId}/contexts/{contextId}/namespace`

`$ curl -i -X POST -H "Content-Type: application/json" -d 'Command' http://localhost:9019//organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/cateogry`
`$ curl -i -X POST -H "Content-Type: application/json" -d 'DefineSchema command defined.' http://localhost:9019//organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/description`
`$ curl -i -X POST -H "Content-Type: application/json" -d 'DefineSchema' http://localhost:9019//organizations/{organizationId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/name`

`$ curl -i -X POST -H "Content-Type: application/json" -d 'Initial revision of SchemaDefined.' http://localhost:9019/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{versionId}/description`
`$ curl -i -X POST -H "Content-Type: application/json" -d 'event SchemaDefined { type eventType\n timestamp occurredOn }' http://localhost:9019/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{versionId}/specification`
`$ curl -i -X POST -H "Content-Type: application/json" -d 'Published' http://localhost:9019/organizations/{orgId}/units/{unitId}/contexts/{contextId}/schemas/{schemaId}/versions/{versionId}/status`


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

#### E2E Tests:

:warning: These need to be converted from Vue to Svelte, so this is not working for now.

End-to-End tests are implemented using [Cypress.io](https://www.cypress.io/). 
Test implementations are in `src/test/e2e`.

To run the tests locally, you need to set the URL of the application under test, open cypress and launch select the tests to launch. 

To run the tests against the development server and a locally running (debuggable) backend:
```
$ # run XoomInitializer.main from your IDE
$ cd <project root>/src/main/frontend
$ npm run dev &
$ cd <project root>/src/test/e2e
$ CYPRESS_BASE_URL=http://localhost:8080/app npx cypress open
```

To run the tests against `vlingo-schemata` within a docker container:
[Docker Run](#docker-run) + match the E2E base URL default:
```
$ cd <project root>/src/test/e2e
$ export CYPRESS_BASE_URL=http://localhost:9019/app # default
$ npx cypress open
```