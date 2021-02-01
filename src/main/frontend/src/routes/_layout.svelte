<script context="module">
	import SchemataRepository from '../api/SchemataRepository';
	export async function preload() {
		SchemataRepository.setFetchFunction(this.fetch)
		return await SchemataRepository.getAll();
	}
</script>

<script>
	import { Button, Icon, MaterialApp } from "svelte-materialify/src";
	import { contextsStore, contextStore, firstPage, mobileStore, organizationsStore, organizationStore, schemasStore, schemaStore, schemaVersionsStore, schemaVersionStore, theme, unitsStore, unitStore } from '../stores';
	import { initStoresOfOne } from '../utils';
	import AppBar from 'svelte-materialify/src/components/AppBar';
	import { mdiMenu, mdiWeatherNight, mdiWeatherSunny, mdiGithub } from '@mdi/js';
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
		setAllStores();
		$firstPage = false;
	}
	
	function setAllStores() {
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
	$: $mobileStore = mobile;
	function checkMobile() {
		mobile = window.matchMedia(breakpoints['md-and-down']).matches;
	}
	onMount(() => {
		SchemataRepository.setFetchFunction(fetch);
		$theme = window.localStorage.getItem('theme') || 'light';
		const unsubscribe = theme.subscribe((value) => {
			window.localStorage.setItem('theme', value);
		});
		import('svelte-materialify/src/utils/breakpoints').then(({ default: data }) => {
			breakpoints = data;
			checkMobile();
		});
		return unsubscribe;
	});

	const toggleTheme = () => $theme = ($theme === "light") ? "dark" : "light";
	$: bgTheme = ($theme === "light") ? "#ffffff" : "#212121"
	//debug, btw. this doesn't fire when store gets set to undefined, for whatever reason
	// $: console.log({$organizationsStore}, {$organizationStore}, {$unitsStore}, {$unitStore}, {$contextsStore}, {$contextStore}, {$schemasStore}, {$schemaStore}, {$schemaVersionsStore}, {$schemaVersionStore});
</script>

<svelte:window on:resize={checkMobile} />

<div style="height: 100%; background-color: {bgTheme}">
<MaterialApp theme={$theme}>
	<AppBar fixed style="width:100%">
    	<div slot="icon">
    	  {#if mobile}
    	    <Button fab depressed on:click={() => (sidenav = !sidenav)} aria-label="Open Menu">
    	    	<Icon path={mdiMenu} />
    	    </Button>
    	  {/if}
		</div>
		<a href="." slot="title" class="text--primary"><span style="color: var(--theme-text-primary)"> VLINGO/SCHEMATA </span></a>
		<div style="flex-grow:1" />
    	<a href="https://github.com/vlingo/vlingo-schemata" target="_blank" rel="noopener">
    	  <Button class="white-text grey darken-3" aria-label="GitHub" fab>
    	    <Icon path={mdiGithub} />
    	  </Button>
    	</a>
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