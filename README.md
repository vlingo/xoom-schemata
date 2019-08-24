# vlingo-schemata

[![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/vlingo-platform-java/schemata)

The vlingo/PLATFORM schema registry.
 
## Run

You can run the registry with demo data within docker using `docker run -p 9019:9019 wernerw/vlingo-schemata:demo`.

After building the fat jar, you can also simply execute it via `java -jar vlingo-schemata-<version>-jar-with-dependencies.jar`

## Build

Run the full build using `mvn clean package -Pfrontend`. 

Afterwards, you can build the docker container with `docker build . -t vlingo-schemata-demo`.

CI build runs on CircleCI: https://circleci.com/gh/wwerner/vlingo-schemata/.

A docker image containing mock data is published to https://hub.docker.com/r/wernerw/vlingo-schemata.


The maven build takes care of the following:
* Generate sources for the schema grammars in `src/main/antlr4`
* Build the backend application
* Download the dependencies for the UI build (`node` & `npm`) [Maven Profile 'frontend']
* Run the UI build (`npm run build` in `src/main/frontend`) [Maven Profile 'frontend']
* Package the backend, frontend and dependencies within a fat jar

## Development

### Prerequisites

* Java >= 8
* Maven, 3.6.0 is known to work. Alternatively, you can rely on the bundled maven wrapper.
* [frontend only] NodeJS & npm, Node 11.14.0 is known to work. Earlier versions probably too.
* [frontend only; recommended] Chrome Devtools Vue.js extension 

### Backend

* Generate schema specification sources: `mvn generate-sources`
* Test: `mvn test`
* Build fat jar: `mvn package`

### Frontend

vlingo-schemata provides an interface to allow for easy retrieval of schemata and 
schemata meta information, e.g. available versions and publication status.

The UI is built using Vue.js and Vuetify.

* Project setup `npm install`
* Compiles and hot-reloads for development `npm run serve`, makes the UI available on http://localhost:8080/
* Compiles and minifies for production `npm run build`
* Run your tests `npm run test`
* Lints and fixes files `npm run lint`
* Webpack bundle report `npm run report`

When running the UI via `npm run serve`, API calls are proxied to a local backend instance.
It is assumed to be running at http://localhost:9019/api.

You can run the backend ...
* ... from the IDE, in case you want to debug the backend in parallel. `Bootstrap.java` has the `main` method.
* ... from the jar, in case you just need the current backend state and do not want to delve into the backend code: 
`java -jar vlingo-schemata-<version>-jar-with-dependencies.jar`
* ... from Docker, in case you don't want bother w/ Java at all: 
`docker run -p 9019:9019 wernerw/vlingo-schemata:demo`. 
Note that this also pulls in the current UI, so don't get confused.
