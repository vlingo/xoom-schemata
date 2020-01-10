const faker = require('faker');

const wp = require('@cypress/webpack-preprocessor')
const axios = require('axios');

const createOrganization = function (config) {
    return axios.post(config.env.apiUrl + '/organizations', {
        organizationId: '',
        name: faker.company.companyName(),
        description: faker.company.catchPhrase()
    }).then(response => response.data)
}

const createUnit = function (organization, config) {
    return axios.post(config.env.apiUrl
        + '/organizations/' + organization.organizationId
        + '/units', {
        unitId: '',
        name: faker.commerce.department(),
        description: faker.lorem.sentence()
    }).then(response => response.data)
}

const createContext = (unit, config) => {
    return axios.post(
        config.env.apiUrl + '/organizations/' + unit.organizationId
        + '/units/' + unit.unitId
        + '/contexts', {
            contextId: '',
            namespace: faker.internet.domainName(),
            description: 'foo'
        }).then(response => response.data)
}

const createSchema = (context, config) => {
    return axios.post(
        config.env.apiUrl + '/organizations/' + context.organizationId
        + '/units/' + context.unitId
        + '/contexts/' + context.contextId
        + '/schemas', {

            contextId: '',
            name: faker.company.catchPhraseNoun(),
            scope: faker.random.arrayElement(['Public', 'Private']),
            category: faker.random.arrayElement(['Command', 'Data', 'Document', 'Envelope', 'Event', 'Unknown']),
            description: faker.lorem.sentence()
        }).then(response => response.data)
}

const createSchemaVersion = (schema, config) => {
    let majorMinorVersion = faker.random.number(9) + '.' + faker.random.number(9)
    let patchVersion = faker.random.number(9)
    let prevVersion = majorMinorVersion + '.' + patchVersion
    let currentVersion = majorMinorVersion + '.' + (patchVersion + 1)

    return axios.post(
        config.env.apiUrl + '/organizations/' + schema.organizationId
        + '/units/' + schema.unitId
        + '/contexts/' + schema.contextId
        + '/schemas/' + schema.schemaId
        + '/versions', {
            organizationId: schema.organizationId,
            unitId: schema.unitId,
            contextId: schema.contextId,
            schemaId: schema.schemaId,
            schemaVersionId: '',
            specification: 'event SalutationHappened { type eventType }',
            previousVersion: prevVersion,
            currentVersion: currentVersion,
            description: faker.lorem.sentence()
        }).then(response => response.data)
}


module.exports = (on, config) => {
    const options = {
        webpackOptions: {
            resolve: {
                extensions: [".ts", ".tsx", ".js"]
            },
            module: {
                rules: [
                    {
                        test: /\.tsx?$/,
                        loader: "ts-loader",
                        options: {
                            transpileOnly: true,
                        },
                    }
                ]
            }
        },
    }
    on('file:preprocessor', wp(options))

    on('task', {'schemata:withOrganization': () => createOrganization(config)})
    on('task', {'schemata:withUnit': (organization) => createUnit(organization, config)})
    on('task', {'schemata:withContext': (unit) => createUnit(unit, config)})
    on('task', {'schemata:withSchema': (context) => createUnit(context, config)})
    on('task', {'schemata:withSchemaVersion': (schema) => createUnit(schema, config)})
    on('task', {
        'schemata:withTestData': () => {
            let data = {
                organization: undefined,
                unit: undefined,
                context: undefined,
                schema: undefined,
                version: undefined
            };
            return createOrganization(config)
                .then(org => {
                    data.organization = org
                    return createUnit(org, config)
                })
                .then(unit => {
                    data.unit = unit
                    return createContext(unit, config)
                })
                .then(context => {
                    data.context = context
                    return createSchema(context, config)
                })
                .then(schema => {
                    data.schema = schema
                    return createSchemaVersion(schema, config)
                })
                .then(version => data.version = version)
                .then(version => data.version.compatibleSpecification =
                    'event SalutationHappened { type eventType\nversion semVer }')
                .then(version => data.version.incompatibleSpecification =
                    'event SalutationHappened { type renamedEventType\nversion semVer }')
                .then(() => data)
        }
    })
}
