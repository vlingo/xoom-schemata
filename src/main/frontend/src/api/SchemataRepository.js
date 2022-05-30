import Repository from './Repository'

const resources = Object.freeze({
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
})

async function ensure(response, status) {
	if (response.status !== status) {
		const contentType = response.headers.get("content-type");
		let message;
		if (contentType === "application/json") {
			message = JSON.stringify(await response.json());
		} else {
			message = await response.text();
		}
		throw Error(`HTTP ${response.status}: ${response.statusText} (${message}).`); //${response.config.url}
	}
	return response;
}

function ensureOk(response) {
	return ensure(response, 200);
}

function ensureCreated(response) {
	return ensure(response, 201);
}

let fetch;

function setFetchFunction(fetchFunc) {
	fetch = fetchFunc;
}

async function repoGet(path) {
	return Repository.get(path, fetch)
		.then(ensureOk)
		.then(res => res.json());
}

async function repoPost(path, body) {
	return Repository.post(path, body)
		.then(ensureCreated)
		.then(res => res.json());
}

async function repoPut(path, body) {
	return Repository.put(path, body)
		.then(ensureOk)
		.then(res => res.json());
}

async function getOrganizations() {
	return repoGet(resources.organizations())
}
async function getOrganization(organization) {
	return repoGet(resources.organizations(organization))
}

async function getUnits(organization) {
	return repoGet(resources.units(organization))
}
async function getUnit(organization, unit) {
	return repoGet(resources.unit(organization, unit))
}

async function getContexts(organization, unit) {
	return repoGet(resources.contexts(organization, unit))
}
async function getContext(organization, unit, context) {
	return repoGet(resources.context(organization, unit, context))
}

async function getCategories() {
	return repoGet(resources.categories())
}
async function getScopes() {
	return repoGet(resources.scopes())
}

async function getSchemata(organization, unit, context) {
	return repoGet(resources.schemata(organization, unit, context))
}
async function getSchema(organization, unit, context, schema) {
	return repoGet(resources.schema(organization, unit, context, schema))
}

async function getVersions(organization, unit, context, schema) {
	return repoGet(resources.versions(organization, unit, context, schema))
}
async function getVersion(organization, unit, context, schema, version) {
	return repoGet(resources.version(organization, unit, context, schema, version))
}

export default {
	setFetchFunction,
	getOrganizations, getOrganization, getUnits, getUnit, getContexts, getContext, getCategories, getScopes, getSchemata, getSchema, getVersions, getVersion,
	loadSources(organization, unit, context, schema, version, language) {
		return Repository.get(resources.sources(organization, unit, context, schema, version, language), fetch)
			.then(ensureOk)
			.then(response => response.text())
	},

	createOrganization(name, description) {
		return repoPost(resources.organizations(), {
			organizationId: '',
			name: name,
			description: description
		});
	},
	updateOrganization(id, name, description) {
		return repoPut(resources.organization(id), {
			organizationId: id,
			name: name,
			description: description
		});
	},
	createUnit(organization, name, description) {
		return repoPost(resources.units(organization), {
			unitId: '',
			name: name,
			description: description
		});
	},
	updateUnit(organization, id, name, description) {
		return repoPut(resources.unit(organization, id), {
			unitId: id,
			name: name,
			description: description
		});
	},

	createContext(organization, unit, namespace, description) {
		return repoPost(resources.contexts(organization, unit), {
			contextId: '',
			namespace: namespace,
			description: description
		});
	},
	updateContext(organizationId, unitId, id, namespace, description) {
		return repoPut(resources.context(organizationId, unitId, id), {
			contextId: id,
			namespace: namespace,
			description: description
		});
	},

	createSchema(organization, unit, context, name, scope, category, description) {
		return repoPost(resources.schemata(organization, unit, context), {
			schemaId: '',
			name: name,
			scope: scope,
			category: category,
			description: description
		});
	},
	updateSchema(organizationId, unitId, contextId, id, name, category, scope, description) {
		return repoPut(resources.schema(organizationId, unitId, contextId, id), {
			schemaId: id,
			name: name,
			scope: scope,
			category: category,
			description: description
		})
	},
	createSchemaVersion(organization, unit, context, schema, specification, description, previousVersion, currentVersion) {
		return Repository.post(resources.versions(organization, unit, context, schema), {
			schemaVersionId: '',
			specification: specification,
			previousVersion: previousVersion,
			currentVersion: currentVersion,
			description: description
		})
			.then(response => {
				if (response.status === 409) {
					return Promise.reject(response.json());
				} else {
					return response.json();
				}
			})
	},
	patchSchemaVersionDescription(
		organization, unit, context, schema, version, description) {
		return Repository.patch(
			resources.schemaDescription(organization, unit, context, schema, version),
			description
		)
			.then(ensureOk)
			.then(response => response.json())
	},
	patchSchemaVersionStatus(
		organization, unit, context, schema, version, status) {
		return Repository.patch(
			resources.versionStatus(organization, unit, context, schema, version),
			status
		)
			.then(ensureOk)
			.then(response => response.json())
	},
	patchSchemaVersionSpecification(
		organization, unit, context, schema, version, specification) {
		return Repository.patch(
			resources.schemaSpecification(organization, unit, context, schema, version),
			specification
		)
			.then(ensureOk)
			.then(response => response.json())
	},
	async getAll() {
		if (process.browser) {
			let orgs = [];
			let units = [];
			let contexts = [];
			let schemas = [];
			let schemaVersions = [];

			try {
				orgs.push(...await getOrganizations());
				for (const org of orgs) {
					const orgUnits = await getUnits(org.organizationId);
					if (orgUnits) {
						units.push(...orgUnits);
						for (const unit of units.filter(u => u.organizationId == org.organizationId)) {
							const unitContexts = await getContexts(org.organizationId, unit.unitId);
							if (unitContexts) {
								contexts.push(...unitContexts);
								for (const context of contexts.filter(c => c.unitId == unit.unitId)) {
									const contextSchemas = await getSchemata(org.organizationId, unit.unitId, context.contextId);
									if (contextSchemas) {
										schemas.push(...contextSchemas);
										for (const schema of schemas.filter(s => s.contextId == context.contextId)) {
											const schemaSchemaVersions = await getVersions(org.organizationId, unit.unitId, context.contextId, schema.schemaId);
											if (schemaSchemaVersions) {
												schemaVersions.push(...schemaSchemaVersions);
											}
										}
									}
								}
							}
						}
					}
				}
			} catch (e) {
				console.error(`${e}: API is unreachable.`);
			}
			return { orgs, units, contexts, schemas, schemaVersions };
		}
	}
}
