<script context="module">
	import { writable } from 'svelte/store';
	let current = writable({});
</script>

<script>
	import { adjustStoresTo, deAdjustStoresTo, isObjectInAStore } from "../utils";

	export let file;
	export let selected;
	let item;

	$: if(!isObjectInAStore(file)) deAdjustStoresTo(file.type);
	
	function chooseThis() {
		if(!selected) {
			adjustStoresTo(file);
			$current = item;
		} else {
			deAdjustStoresTo(file.type);
		}
	}

	$: active = (item == $current);
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