<script context="module">
	import SchemataRepository from '../api/SchemataRepository';

	export async function preload(page, session) {

		if(process.browser) {
		const orgs = await SchemataRepository.getOrganizations();

		let units = [];

		let contexts = [];

		let schemas = [];

		let schemaVersions = [];
		
		for(const org of orgs) {
			const orgUnits = await SchemataRepository.getUnits(org.organizationId);
			if(orgUnits) {
				units.push(...orgUnits);
				for(const unit of units.filter(u => u.organizationId == org.organizationId)) {
					const unitContexts = await SchemataRepository.getContexts(org.organizationId, unit.unitId);
					if(unitContexts) {
						contexts.push(...unitContexts);
						for(const context of contexts.filter(c => c.unitId == unit.unitId)) {
							const contextSchemas = await SchemataRepository.getSchemata(org.organizationId, unit.unitId, context.contextId);
							if(contextSchemas) {
								schemas.push(...contextSchemas);
								for(const schema of schemas.filter(s => s.contextId == context.contextId)) {
									const schemaSchemaVersions = await SchemataRepository.getVersions(org.organizationId, unit.unitId, context.contextId, schema.schemaId);
									if(schemaSchemaVersions) {
										schemaVersions.push(...schemaSchemaVersions);
									}
								}
							}
						}
					}
				}
			}
		}

		return { orgs, units, contexts, schemas, schemaVersions };
		}
	}
</script>

<script>
	import Nav from '../components/Nav.svelte';
	import { contextsStore, contextStore, firstPage, organizationsStore, organizationStore, schemasStore, schemaStore, schemaVersionsStore, schemaVersionStore, unitsStore, unitStore } from '../stores';
	import { initContextStores, initOrgStores, initSchemaStores, initSchemaVersionStores, initUnitStores } from '../utils';

	export let orgs;
	export let units;
	export let contexts;
	export let schemas;
	export let schemaVersions;
	
	if($firstPage) {
		console.log({$firstPage}, "BEFORE");
		console.log(orgs, units, contexts, schemas, schemaVersions);
		console.log($organizationsStore, $organizationStore, $unitsStore, $unitStore, $contextsStore, $contextStore, $schemasStore, $schemaStore, $schemaVersionsStore, $schemaVersionStore);		
		setAllStores();

		$firstPage = false;

		console.log({$firstPage}, "AFTER");
		console.log($organizationsStore, $organizationStore, $unitsStore, $unitStore, $contextsStore, $contextStore, $schemasStore, $schemaStore, $schemaVersionsStore, $schemaVersionStore);
	}
	
	function setAllStores() {
		initOrgStores(orgs);
		initUnitStores(units);
		initContextStores(contexts);
		initSchemaStores(schemas);
		initSchemaVersionStores(schemaVersions);
	}

</script>

<style>
	main {
		position: relative;
		max-width: 56em;
		min-width: 20em;
		background-color: white;
		padding: 2em;
		margin: 0 auto;
		box-sizing: border-box;
	}
</style>

<Nav/>

<main>
	<slot></slot>
</main>