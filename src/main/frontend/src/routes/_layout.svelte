<script context="module">
	import SchemataRepository from '../api/SchemataRepository';

	export async function preload(page, session) {

		if(process.browser) {

		let orgs = [];
		
		let units = [];

		let contexts = [];

		let schemas = [];

		let schemaVersions = [];
		
		try {

		orgs.push(...await SchemataRepository.getOrganizations());

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

	} catch (e) {
		console.error(`${e}: API is unreachable.`);
	}

		return { orgs, units, contexts, schemas, schemaVersions };
		}
	}
</script>

<script>
	import Nav from '../components/Nav.svelte';
	import { contextsStore, contextStore, firstPage, organizationsStore, organizationStore, schemasStore, schemaStore, schemaVersionsStore, schemaVersionStore, unitsStore, unitStore } from '../stores';
	import { initStoresOfOne } from '../utils';

	export let segment;

	export let orgs = [];
	export let units = [];
	export let contexts = [];
	export let schemas = [];
	export let schemaVersions = [];
	
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
		// test if it still works without all arrays defined
		// function initStoreOfAll(array, storeOfAll) {
		// 	if(array) {
		// 		storeOfAll.update(arr => arr.concat(...array));
		// 	}
		// }
		
		$organizationsStore = orgs;
		$unitsStore = units;
		$contextsStore = contexts;
		$schemasStore = schemas;
		$schemaVersionsStore = schemaVersions;
		initStoresOfOne();
	}

	//debug, btw. this doesn't fire when store gets set to undefined, for whatever reason
	// $: console.log({$organizationsStore}, {$organizationStore}, {$unitsStore}, {$unitStore}, {$contextsStore}, {$contextStore}, {$schemasStore}, {$schemaStore}, {$schemaVersionsStore}, {$schemaVersionStore});
</script>


<Nav {segment}/>

<main>
	<slot></slot>
</main>


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