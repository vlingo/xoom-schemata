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

	export let orgs;
	export let units;
	export let contexts;
	export let schemas;
	export let schemaVersions;
	
	if($firstPage) {
		console.log({$firstPage}, "BEFORE");
		console.log(orgs, units, contexts, schemas, schemaVersions);
		console.log($contextsStore, $contextStore, $organizationsStore, $organizationStore, $schemasStore, $schemaStore, $schemaVersionsStore, $schemaVersionStore, $unitsStore, $unitStore );
		// maybe need to check if empty
		// array reset is maybe not needed
		// array[0] will be undefined if none exist
		if(orgs) {
			$organizationsStore = [];
			$organizationsStore.push(...orgs);
			$organizationStore = orgs[0];
		}
		if(units) {
			$unitsStore = [];
			$unitsStore.push(...units);
			$unitStore = units[0];
		}
		if(contexts) {
			$contextsStore = [];
			$contextsStore.push(...contexts);
			$contextStore = contexts[0];
		}
		if(schemas) {
			$schemasStore = [];
			$schemasStore.push(...schemas);
			$schemaStore = schemas[0];
		}
		if(schemaVersions) {
			$schemaVersionsStore = [];
			$schemaVersionsStore.push(...schemaVersions);
			$schemaVersionStore = schemaVersions[0];
		}

		$firstPage = false;

		console.log({$firstPage}, "AFTER");
		console.log($contextsStore, $contextStore, $organizationsStore, $organizationStore, $schemasStore, $schemaStore, $schemaVersionsStore, $schemaVersionStore, $unitsStore, $unitStore );
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