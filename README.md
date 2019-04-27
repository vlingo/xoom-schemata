# vlingo-schemata

[![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/vlingo-platform-java/schemata)

The schema registry for the vlingo/platform.

You can run the registry with demo data within docker using `docker run -p 9019:9019 wernerw/vlingo-schemata-demo `.
 
## Run

## Build

Both frontend (npm/webpack) & backend (maven) build are run from maven. 
The npm build can also be called via Maven using the `frontend` profile, e.g. `mvn install -Pfrontend`. 
In this case, Maven downloads node/npm and builds into `target/classes/static`, so the UI
files can be picked up from the jar's classpath.

## Development

### User Interface

vlingo-schemata provides an interface to allow for easy retrieval of schemata and 
schemata meta information, e.g. available versions and publication status.

See https://github.com/vlingo/vlingo-schemata/blob/master/docs/vlingo-schemata-wireframes.pdf for mock ups.

The sample data for `com.google.search.adwords.events.AdWordsAudienceChosen v1.0.1` has the most details.

Build runs on CircleCI: https://circleci.com/gh/wwerner/vlingo-schemata/

A docker image containing mock data is published to 

The UI is built using Vue.js and Vuetify.

* Project setup `npm install`
* Compiles and hot-reloads for development `npm run serve`
* Run your tests `npm run test`
* Lints and fixes files `npm run lint`
* Webpack bundle report `npm run report`
* API calls are proxied to http://localhost:9019/api

#### UI Build

* Compiles and minifies for production `npm run build`


