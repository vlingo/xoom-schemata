<script context="module">
	import SchemataRepository from '../api/SchemataRepository';
	export async function preload() {
		SchemataRepository.setFetchFunction(this.fetch)
		return await SchemataRepository.getAll();
	}
</script>

<script>
	import { Button, Icon, MaterialApp, Container, AppBar } from "svelte-materialify/src";
	import { contextsStore, firstPage, isMobile, organizationsStore, schemasStore, schemaVersionsStore, theme, unitsStore} from '../stores';
	import { initStoresOfOne } from '../utils';
	import { mdiMenu, mdiWeatherNight, mdiWeatherSunny, mdiGithub } from '@mdi/js';
	import { onMount, setContext } from 'svelte';
	import SiteNavigation from '../components/SiteNavigation.svelte';
	import logo from '../images/xoom-horizontal_schemata.png';
	import { stores } from '@sapper/app';

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
	const toggleTheme = () => $theme = ($theme === "light") ? "dark" : "light";
	$: bgTheme = ($theme === "light") ? "#ffffff" : "#212121";

	onMount(() => {
		SchemataRepository.setFetchFunction(fetch);
		isMobile.check();
	})

	const { page } = stores();
  $: setContext('fromDesigner', !!$page.query.designer);
  $: setContext('isProducer', !!$page.query.producer);

	//debug, btw. this doesn't fire when store gets set to undefined, for whatever reason
	// $: console.log({$organizationsStore}, {$organizationStore}, {$unitsStore}, {$unitStore}, {$contextsStore}, {$contextStore}, {$schemasStore}, {$schemaStore}, {$schemaVersionsStore}, {$schemaVersionStore});
</script>

<svelte:window on:resize={isMobile.check} />

<div style="height: 100%; background-color: {bgTheme}">
<MaterialApp theme={$theme}>
	<AppBar fixed style="width:100%">
    	<div slot="icon">
    	  {#if $isMobile}
    	    <Button fab depressed on:click={() => (sidenav = !sidenav)} aria-label="Open Menu">
    	    	<Icon path={mdiMenu} />
    	    </Button>
    	  {/if}
		</div>
		<a href="." slot="title" class="text--primary d-flex">
			<img
				style="width: 256px;"
				src={logo}
				alt="VLINGO XOOM Schemata"
			/>
		</a>
		<div style="flex-grow:1" />
    	<a href="https://github.com/vlingo/xoom-schemata" target="_blank" rel="noopener">
    	  <Button class="white-text grey darken-3" aria-label="GitHub" fab>
    	    <Icon path={mdiGithub} />
    	  </Button>
    	</a>
    	<Button fab text on:click={toggleTheme} aria-label="Toggle Theme">
    		<Icon path={$theme === "light" ? mdiWeatherNight : mdiWeatherSunny}/>
    	</Button>
	</AppBar>

	<SiteNavigation {segment} mobile={$isMobile} bind:sidenav />

	<main class:navigation-enabled={!$isMobile}>
		<Container>
    	<!-- {#if ...}
    		<Loading />
    	{/if} -->
		<slot />
		</Container>
  	</main>
</MaterialApp>
</div>

<style lang="scss" global>
	@import 'svelte-materialify/src/styles/variables';

	main {
	  padding-top: 5rem;
	}
	.navigation-enabled {
	  padding: 5rem 11rem 0 18rem;
	}
	.error {
		label, .s-input__details {
			color: $error-color !important;
		}
	}
	.s-card.vl-card {
		box-shadow: 0 0 64px 16px rgba(0, 0, 0, 0.075) !important;
		border-radius: 16px !important;
	}

	:global(.flex-child) {
		flex: 0 0 50%;
		padding: 12px;
	}
</style>
