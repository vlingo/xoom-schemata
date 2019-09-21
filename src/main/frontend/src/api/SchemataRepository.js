import Repository from '@/api/Repository'


const resources = {
    organizations: () => '/organizations',
    units: (o) => `/organizations/${o}/units`,
    contexts: (o, u) => `/organizations/${o}/units/${u}/contexts`,
    categories: () => '/schema/categories',
    scopes: () => '/schema/scopes',
    schemata: (o, u, c) => `/organizations/${o}/units/${u}/contexts/${c}/schemas`,
    versions: (o, u, c, s) => `/organizations/${o}/units/${u}/contexts/${c}/schemas/${s}/versions`
}

function ensure(response, status) {
    if (response.status !== status) {
        throw Error(`HTTP ${response.status}: ${response.statusText} (${response.config.url}).`);
    }
    return response;
}

function ensureOk(response) {
    return ensure(response, 200);
}

function ensureCreated(response) {
    return ensure(response, 201);
}

export default {
    getOrganizations() {
        return Repository.get(resources.organizations())
            .then(ensureOk)
            .then(response => response.data)
    },

    getUnits(organization) {
        return Repository.get(resources.units(organization))
            .then(ensureOk)
            .then(response => response.data)
    },

    getContexts(organization, unit) {
        return Repository.get(resources.contexts(organization, unit))
            .then(ensureOk)
            .then(response => response.data)
    },
    getCategories() {
        return Repository.get(resources.categories())
            .then(ensureOk)
            .then(response => response.data)
    },
    getScopes() {
        return Repository.get(resources.scopes())
            .then(ensureOk)
            .then(response => response.data)
    },
    getSchemata(organization, unit, context) {
        return Repository.get(resources.schemata(organization, unit, context))
            .then(ensureOk)
            .then(response => response.data)
    },

    getVersions(organization, unit, context, schema) {
        return Repository.get(resources.versions(organization, unit, context, schema))
            .then(ensureOk)
            .then(response => response.data)
    },

    createOrganization(name, description) {
        return Repository.post(resources.organizations(),
            {
                organizationId: '',
                name: name,
                description: description
            }
        )
            .then(ensureCreated)
            .then(response => response.data)
    },
    createUnit(organization, name, description) {
        return Repository.post(resources.units(organization),
            {
                unitId: '',
                name: name,
                description: description
            }
        )
            .then(ensureCreated)
            .then(response => response.data)
    },
    createContext(organization, unit, namespace, description) {
        return Repository.post(resources.contexts(organization, unit),
            {
                contextId: '',
                namespace: namespace,
                description: description
            }
        )
            .then(ensureCreated)
            .then(response => response.data)
    },
    createSchema(organization, unit, context, name, scope, category, description) {
        return Repository.post(resources.schemata(organization, unit, context),
            {
                contextId: '',
                name: name,
                scope: scope,
                category: category,
                description: description
            }
        )
            .then(ensureCreated)
            .then(response => response.data)
    },
}