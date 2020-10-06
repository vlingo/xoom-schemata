<script context="module">
	import { writable } from 'svelte/store';
	let current = writable({});
</script>

<script>
	import { adjustStoresTo, deAdjustStoresTo, isObjectInAStore, isStoreEmpty} from "../utils";
	import { schemaVersionStore } from '../stores';

	export let file;
	export let selected = false;
	let item;

	//maybe "!isStoreEmpty($schemaVersionStore)" needs to be just $schemaVersionStore
	$: if(isObjectInAStore(file) || !isStoreEmpty($schemaVersionStore) && $schemaVersionStore.schemaVersionId == file.id) $current = item;

	function chooseThis() {
		if(!selected) {
			adjustStoresTo(file);
			$current = item;
			selected = true;
		} else {
			deAdjustStoresTo(file.type);
			$current = {};
			selected = false;
		}
	}

	$: active = (item == $current)
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