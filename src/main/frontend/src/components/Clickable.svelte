<script context="module">
	import { writable } from 'svelte/store';
	let current = writable({});
</script>

<script>
	import { adjustStoresTo, deAdjustStoresTo, isObjectInAStore } from "../utils";
	import { fade } from 'svelte/transition';
	import { schemaVersionStore } from '../stores';
	
	export let file;
	export let expanded;
	let item;

	$: if(!isObjectInAStore(file)) {
		deAdjustStoresTo(file.type);
	}

	$: whenTreeUpdatesWithVersion($schemaVersionStore);
	function whenTreeUpdatesWithVersion(store) {
		if(store && file.id === store.schemaVersionId) {
			adjustStoresTo(file);
			$current = item;
		}
	}
	
	function toggleThis() {
		if(!expanded) {
			adjustStoresTo(file);
			$current = item;
		} else {
			deAdjustStoresTo(file.type);
		}
	}

</script>

<div transition:fade={{ duration: 100 }} bind:this={item} class:active={item === $current} on:click={toggleThis}>
	<slot/>
</div>


<style>
	.active {
		font-weight: bold;
		/* background-color: var(--vlingo-color1);  only do this on the schemaVersion*/
	}
	div {
		font-weight: lighter;
	}
</style>
