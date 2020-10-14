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
	import { Button, Icon, MaterialApp } from "svelte-materialify/src";
	import { contextsStore, contextStore, firstPage, organizationsStore, organizationStore, schemasStore, schemaStore, schemaVersionsStore, schemaVersionStore, theme, unitsStore, unitStore } from '../stores';
	import { initStoresOfOne } from '../utils';
	import AppBar from 'svelte-materialify/src/components/AppBar';
	import { mdiMenu, mdiWeatherNight, mdiWeatherSunny } from '@mdi/js';
	import { onMount } from 'svelte';
	import SiteNavigation from '../components/SiteNavigation.svelte';
	import Container from 'svelte-materialify/src/components/Grid/Container.svelte';

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

	let sidenav = false;
	let breakpoints = {};
	let mobile = false;
	function checkMobile() {
		mobile = window.matchMedia(breakpoints['md-and-down']).matches;
	}
	onMount(() => {
		$theme = window.localStorage.getItem('theme') || 'light';
		const unsubscribe = theme.subscribe((value) => {
			window.localStorage.setItem('theme', value);
		});
		import('svelte-materialify/src/utils/breakpoints').then(({ default: data }) => {
	    	breakpoints = data;
		});
		checkMobile();
		return unsubscribe;
	});

	const toggleTheme = () => $theme = ($theme === "light") ? "dark" : "light";
	$: bgTheme = ($theme === "light") ? "#ffffff" : "#212121"
	//debug, btw. this doesn't fire when store gets set to undefined, for whatever reason
	$: console.log({$organizationsStore}, {$organizationStore}, {$unitsStore}, {$unitStore}, {$contextsStore}, {$contextStore}, {$schemasStore}, {$schemaStore}, {$schemaVersionsStore}, {$schemaVersionStore});
</script>

<svelte:window on:resize={checkMobile} />

<div style="height: 100%; background-color: {bgTheme}">
<MaterialApp theme={$theme}>
	<AppBar fixed style="width:100%"> <!-- class={'primary-color theme--dark'} -->
    	<div slot="icon">
    	  {#if mobile}
    	    <Button fab depressed on:click={() => (sidenav = !sidenav)} aria-label="Open Menu">
    	    	<Icon path={mdiMenu} />
    	    </Button>
    	  {/if}
		</div>
		<a href="." slot="title" class="text--primary"> VLINGO/SCHEMATA </a>
		<div style="flex-grow:1" />
    	<!-- <a
    	  href="https://github.com/TheComputerM/svelte-materialify"
    	  target="_blank"
    	  rel="noopener">
    	  <Button class="white-text grey darken-3" aria-label="GitHub" fab={mobile}>
    	    <Icon path={mdiGithub} class={!mobile ? 'mr-3' : ''} />
    	    {#if !mobile}GitHub{/if}
    	  </Button>
    	</a> -->
    	<Button fab text on:click={toggleTheme} aria-label="Toggle Theme">
    		<Icon path={$theme === "light" ? mdiWeatherNight : mdiWeatherSunny}/>
    	</Button>
	</AppBar>

	<SiteNavigation {segment} {mobile} bind:sidenav />

	<main class:navigation-enabled={!mobile}>
		<Container>
    	<!-- {#if ...}
    		<Loading />
    	{/if} -->
		<slot />
		</Container>
  	</main>
</MaterialApp>
</div>

<style>
	main {
	  padding-top: 5rem;
	}
	.navigation-enabled {
	  padding: 5rem 11rem 0 18rem;
	}
</style>

<!-- <style>
	main {
		position: relative;
		max-width: 56em;
		min-width: 20em;
		background-color: white;
		padding: 2em;
		margin: 0 auto;
		box-sizing: border-box;
	}
</style> -->