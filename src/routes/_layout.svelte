<script context="module">
	import SchemataRepository from '../api/SchemataRepository';

	export async function preload(page, session) {

		if(process.browser) {
		const orgs = await SchemataRepository.getOrganizations();

		let units = [];

		let contexts = [];

		let schemas = [];
		
		for(const org of orgs) {
			const orgUnits = await SchemataRepository.getUnits(org.organizationId);
			if(orgUnits) {
				console.log({orgUnits});
				units.push(...orgUnits);
				for(const unit of units) {
					const unitContexts = await SchemataRepository.getContexts(org.organizationId, unit.unitId);
					if(unitContexts) {
						contexts.push(...unitContexts);
						for(const context of contexts) {
							const contextSchemas = await SchemataRepository.getSchemata(org.organizationId, unit.unitId, context.contextId);
							if(contextSchemas) {
								schemas.push(...contextSchemas);
							}
						}
					}
				}
			}
		}

		return { orgs, units, contexts, schemas };
		}
	}
</script>

<script>
	import Nav from '../components/Nav.svelte';
	import { contextsStore, contextStore, firstPage, organizationsStore, organizationStore, schemasStore, schemaStore, unitsStore, unitStore } from '../stores';

	export let orgs;
	export let units;
	export let contexts;
	export let schemas;
	
	if($firstPage) {
		console.log({$firstPage}, "BEFORE");
		console.log(orgs, units, contexts, schemas);

		// maybe need to check if empty
		// array[0] will be undefined if none exist
		if(orgs) {
			$organizationsStore.push(...orgs);
			$organizationStore = orgs[0];
		}
		if(units) {
			$unitsStore.push(...units);
			$unitStore = units[0];
		}
		if(contexts) {
			$contextsStore.push(...contexts);
			$contextStore = contexts[0];
		}
		if(schemas) {
			$schemasStore.push(...schemas);
			$schemaStore = schemas[0];
		}

		$firstPage = false;

		console.log({$firstPage}, "AFTER");
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