<script context="module">
	import { writable } from 'svelte/store';
	let current = writable({});
</script>

<script>
	import { adjustStoresTo, deAdjustStoresTo, isObjectInAStore} from "../utils";
	import { contextStore, organizationStore, schemaStore, schemaVersionStore, unitStore } from '../stores';

	export let file;

	export let expanded = true;

	let item;

	let selected = false;

	$: if(($organizationStore || $unitStore || $contextStore || $schemaStore || $schemaVersionStore) && (isObjectInAStore(file))) {
		selected = true;
	} else {
		selected = false;
	}

	function chooseThis() {
		$current = item;
		if(!selected) {
			adjustStoresTo(file);
			// selected = true;
			// setCurrent();
		} else {
			deAdjustStoresTo(file);
			// selected = false;

		}
	}

	$: active = (item == $current) //&& expanded; ->could use this
</script>

<div bind:this={item} class:active={active} class:notActive={!active} on:click={chooseThis}>
	<slot/>
</div>


<style>
	.active {
		font-weight: bold;
		/* background-color: var(--vlingo-color1);  only do this on the schemaVersion*/
	}
	.notActive {
		font-weight: lighter;
	}
</style>