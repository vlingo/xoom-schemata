# vlingo-schemata

[![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/vlingo-platform-java/schemata)

The schema registry for the vlingo/platform.


## User Interface

vlingo-schemata provides an interface to allow for easy retrieval of schemata and 
schemata meta information, e.g. available versions and publication status.

See https://github.com/vlingo/vlingo-schemata/blob/master/docs/vlingo-schemata-wireframes.pdf for mock ups.

A sample using mock data is deployed to Zeit Now for easy review: https://vlingo-schemata.now.sh/

The sample data for `com.google.search.adwords.events.AdWordsAudienceChosen v1.0.1` has the most details.

Build runs on CircleCI: https://circleci.com/gh/wwerner/vlingo-schemata/

The UI is built using Vue.js and Vuetify.

* Project setup `npm install`
* Compiles and hot-reloads for development `npm run serve`
* Compiles and minifies for production `npm run build`
* Run your tests `npm run test`
* Lints and fixes files `npm run lint`
* Webpack bundle report `npm run report`
* Deploy to `npm run deploy`
