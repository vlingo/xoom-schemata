<script>
	export let mobile;
	export let sidenav;
	import { NavigationDrawer, List, Overlay } from 'svelte-materialify/src';
	import NavItem from './NavItem.svelte';
	import routes from '../util/routes';

	export let segment;

	import { getContext } from 'svelte';
	const isProducer = getContext('isProducer');
	let newRoutes = isProducer ? routes.filter(r => r.href === 'organization' || r.href === 'unit' || r.href === 'context') : routes;
</script>

<NavigationDrawer active={!mobile || sidenav} style="height:100vh;" fixed clipped borderless>
	<!-- <br /> -->
	<!-- <ListGroup eager {offset} active={expanded}></ListGroup> maybe needed around all nav-items -->
	<List nav dense>
		{#each newRoutes as item}
			<NavItem {item} {segment}/>
		{/each}
	</List>
</NavigationDrawer>
<Overlay index="3" active={mobile && sidenav} on:click={() => (sidenav = false)} />
