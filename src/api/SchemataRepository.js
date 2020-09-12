import Repository from './Repository'


const resources = {
  organizations: () => '/organizations',
  organization: (o) => `${resources.organizations()}/${o}`,
  units: (o) => `${resources.organization(o)}/units`,
  unit: (o, u) => `${resources.units(o)}/${u}`,
  contexts: (o, u) => `${resources.unit(o, u)}/contexts`,
  context: (o, u, c) => `${resources.contexts(o, u)}/${c}`,
  categories: () => '/schema/categories',
  scopes: () => '/schema/scopes',
  schemata: (o, u, c) => `${resources.context(o, u, c)}/schemas`,
  schema: (o, u, c, s) => `${resources.schemata(o, u, c)}/${s}`,
  schemaDescription: (o, u, c, s, v) => `/organizations/${o}/units/${u}/contexts/${c}/schemas/${s}/versions/${v}/description`,
  schemaSpecification: (o, u, c, s, v) => `${resources.version(o, u, c, s, v)}/specification`,
  versions: (o, u, c, s) => `${resources.schema(o, u, c, s)}/versions`,
  version: (o, u, c, s, v) => `${resources.versions(o, u, c, s)}/${v}`,
  versionStatus: (o, u, c, s, v) => `${resources.version(o, u, c, s, v)}/status`,
  sources: (o, u, c, s, v, lang) => `/code/${o}:${u}:${c}:${s}:${v}/${lang}`,
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

async function repoGet(path) {
  return Repository.get(path)
  .then(ensureOk)
  .then(res => res.json());
}

export default {
  getOrganizations() {
    return repoGet(resources.organizations())
  },
  getOrganization(organization) {
    return repoGet(resources.organizations(organization))
  },
  getUnits(organization) {
    return repoGet(resources.units(organization))
  },
  getUnit(organization, unit) {
    return repoGet(resources.unit(organization, unit))
  },

  getContexts(organization, unit) {
    return repoGet(resources.contexts(organization, unit))
  },
  getContext(organization, unit, context) {
    return repoGet(resources.context(organization, unit, context))
  },

  getCategories() {
    return repoGet(resources.categories())
  },
  getScopes() {
    return repoGet(resources.scopes())
  },
  getSchemata(organization, unit, context) {
    return repoGet(resources.schemata(organization, unit, context))
  },
  getSchema(organization, unit, context, schema) {
    return repoGet(resources.schema(organization, unit, context, schema))
  },

  getVersions(organization, unit, context, schema) {
    return repoGet(resources.versions(organization, unit, context, schema))
  },
  getVersion(organization, unit, context, schema, version) {
    return repoGet(resources.version(organization, unit, context, schema, version))
  },

  async createOrganization(name, description) {
    const response = await Repository.post(resources.organizations(),
      {
        organizationId: '',
        name: name,
        description: description
      }
    );
    const res = await ensureCreated(response);
    return res.json();
  },
  updateOrganization(id, name, description) {
    return Repository.put(resources.organization(id), {
        organizationId: id,
        name: name,
        description: description
      })
      .then(ensureOk)
      .then(response => response.data)
  },
  async createUnit(organization, name, description) {
    const response = await Repository.post(resources.units(organization),
      {
        unitId: '',
        name: name,
        description: description
      }
    );
    const res = await ensureCreated(response);
    return res.json();
  },
  updateUnit(organization, id, name, description) {
    return Repository.put(resources.unit(organization, id), {
        unitId: id,
        name: name,
        description: description
      })
      .then(ensureOk)
      .then(response => response.data)
  },

  async createContext(organization, unit, namespace, description) {
    const response = await Repository.post(resources.contexts(organization, unit),
      {
        contextId: '',
        namespace: namespace,
        description: description
      });
    const res = await ensureCreated(response);
    return res.json();
  },
  updateContext(organizationId, unitId, id, namespace, description) {
    return Repository.put(resources.context(organizationId, unitId, id), {
        contextId: id,
        namespace: namespace,
        description: description
      })
      .then(ensureOk)
      .then(response => response.data)
  },

  async createSchema(organization, unit, context, name, scope, category, description) {
    const response = await Repository.post(resources.schemata(organization, unit, context),
      {
        schemaId: '',
        name: name,
        scope: scope,
        category: category,
        description: description
      });
    const res = await ensureCreated(response);
    return res.json();
  },
  updateSchema(organizationId, unitId, contextId, id, name, category, scope, description) {
    return Repository.put(resources.schema(organizationId, unitId, contextId, id), {
        schemaId: id,
        name: name,
        scope: scope,
        category: category,
        description: description
      })
      .then(ensureOk)
      .then(response => response.data)
  },
  async createSchemaVersion(organization, unit, context, schema, specification, description, previousVersion, currentVersion) {
    const response = await Repository.post(resources.versions(organization, unit, context, schema),
      {
        schemaVersionId: '',
        specification: specification,
        previousVersion: previousVersion,
        currentVersion: currentVersion,
        description: description
      }
    );
    const res = await ensureCreated(response);
    return res.json();
  },
  saveSchemaVersionSpecification(
    organization, unit, context, schema, version, specification) {
    let config = {
      headers: {
        'Content-Type': 'application/json'
      },
      responseType: 'text'
    };
    return Repository.patch(
      resources.schemaSpecification(organization, unit, context, schema, version),
      specification,
      config
      )
      .then(ensureOk)
      .then(response => response.data)
  },
  saveSchemaVersionDescription(
    organization, unit, context, schema, version, description) {
    let config = {
      headers: {
        'Content-Type': 'application/json'
      },
      responseType: 'text'
    };
    return Repository.patch(
      resources.schemaDescription(organization, unit, context, schema, version),
      description,
      config
      )
      .then(ensureOk)
      .then(response => response.data)
  },
  setSchemaVersionStatus(
    organization, unit, context, schema, version, status) {

    let config = {
      headers: {
        'Content-Type': 'application/json'
      },
      responseType: 'text'
    };
    return Repository.patch(
      resources.versionStatus(organization, unit, context, schema, version),
      status,
      config
      )
      .then(ensureOk)
      .then(response => response.data)
  },
  loadSources(
    organization, unit, context, schema, version, language) {
    let config = {
      headers: {
        'Content-Type': 'application/json'
      },
      responseType: 'text'
    };
    return Promise.all([
      this.getOrganization(organization),
      this.getUnit(organization, unit),
      this.getContext(organization, unit, context),
      this.getSchema(organization, unit, context, schema),
      this.getVersion(organization, unit, context, schema, version),
    ]).then(([org, unit, context, schema, version]) => {
        return repoGet(
          resources.sources(
            org.name,
            unit.name,
            context.namespace,
            schema.name,
            version.currentVersion,
            language), config)
      })
      .then(ensureOk)
      .then(response => response.data)
  },
}
